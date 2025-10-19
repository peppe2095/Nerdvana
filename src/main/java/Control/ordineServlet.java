package Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Connessione.GestoreConnessioneDatabase;
import Dao.*;
import Dto.ApiResponse;
import Model.Articolo;
import Model.Enum.Stato;
import Model.Ordine;
import Model.OrdineArticolo;
import Model.Utente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "ordineServlet", urlPatterns = {"/api/order/*"})
public class ordineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().create();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        switch (path) {
            case "/create":
                createOrder(req, resp);
                return;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        if ("/summary".equals(path)) {
            getSummary(req, resp);
            return;
        }
        if ("/user-list".equals(path)) {
            getUserOrders(req, resp);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
    }

    @SuppressWarnings("unchecked")
    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Devi effettuare il login per completare l'ordine")));
            return;
        }
        Utente utente = (Utente) session.getAttribute("utente");

        Object o = session.getAttribute("carrello");
        Map<String, Integer> cart = (o instanceof Map) ? (Map<String, Integer>) o : new HashMap<>();
        if (cart.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Il carrello è vuoto")));
            return;
        }

        Integer cardId = null;
        String nomeTitolare = null, numeroCarta = null, cvv = null, scadenza = null;
        try {
            String cardParam = req.getParameter("cardId");
            if (cardParam != null && !cardParam.trim().isEmpty()) {
                cardId = Integer.parseInt(cardParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        // Dati per pagamento con carta non salvata (opzionale)
        nomeTitolare = req.getParameter("nomeTitolare");
        numeroCarta = req.getParameter("numeroCarta");
        cvv = req.getParameter("cvv");
        scadenza = req.getParameter("scadenza");

        // Se non viene passato un cardId, richiediamo i dati minimi della carta one-shot
        if (cardId == null) {
            if (nomeTitolare == null || numeroCarta == null || cvv == null || scadenza == null ||
                    nomeTitolare.trim().isEmpty() || numeroCarta.trim().isEmpty() || cvv.trim().isEmpty() || scadenza.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Dati della carta mancanti. Seleziona una carta salvata o inserisci i dati della nuova carta.")));
                return;
            }

            // Normalizza numero carta (solo cifre) e semplici validazioni
            numeroCarta = numeroCarta.replaceAll("\\s+", "");
            if (!numeroCarta.matches("\\d{16}")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Il numero della carta deve contenere 16 cifre")));
                return;
            }
            if (!cvv.matches("\\d{3}")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Il CVV deve contenere 3 cifre")));
                return;
            }
        }

        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            ArticoloDao articoloDao = new ArticoloDao(conn);
            OrdineDao ordineDao = new OrdineDao(conn);
            OrdineArticoloDao ordineArticoloDao = new OrdineArticoloDao(conn);

            // Calcola totale e numero articoli, e prepara mapping articoloId->qty
            double totale = 0.0;
            int numArticoli = 0;
            Map<Integer, Integer> righe = new LinkedHashMap<>();

            for (Map.Entry<String, Integer> e : cart.entrySet()) {
                String numeroSeriale = e.getKey();
                int qty = Math.max(1, e.getValue());
                Articolo a = articoloDao.getArticoloByNumeroSeriale(numeroSeriale);
                if (a == null) continue;
                numArticoli += qty;
                totale += a.getPrezzo() * qty;
                righe.put(a.getId(), righe.getOrDefault(a.getId(), 0) + qty);
            }

            if (righe.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Articoli non validi nel carrello")));
                return;
            }

            Date now = new Date();
            Ordine ordine = new Ordine();
            ordine.setUtenteId(utente.getId());
            ordine.setCartaCreditoId(cardId); // può essere null
            ordine.setNumeroArticoli(numArticoli);
            ordine.setImporto(totale);
            ordine.setDataSpedizione(null);
            ordine.setDataArrivo(null);
            ordine.setStato(Stato.in_attesa);
            ordine.setDataCreazione(now);
            ordine.setDataAggiornamento(now);

            // Salva ordine
            ordineDao.addOrdine(ordine);

            // Usa l'ID generato dal DB
            int ordineId = ordine.getId();
            if (ordineId <= 0) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Impossibile determinare l'ID dell'ordine")));
                return;
            }

            // Inserisci righe ordine
            for (Map.Entry<Integer, Integer> r : righe.entrySet()) {
                OrdineArticolo oa = new OrdineArticolo();
                oa.setOrdineId(ordineId);
                oa.setArticoloId(r.getKey());
                oa.setQuantita(r.getValue());
                ordineArticoloDao.addOrdineArticolo(oa);
            }

            // Svuota carrello
            session.setAttribute("carrello", new HashMap<String, Integer>());

            String fatturaUrl = req.getContextPath() + "/api/invoice/pdf?orderId=" + ordineId;
            String summaryUrl = req.getContextPath() + "/Jsp/order-summary.jsp?orderId=" + ordineId;

            Map<String, Object> data = new HashMap<>();
            data.put("orderId", ordineId);
            data.put("summaryUrl", summaryUrl);
            data.put("fatturaUrl", fatturaUrl);

            resp.getWriter().write(gson.toJson(ApiResponse.ok(data)));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore nella creazione dell'ordine: " + e.getMessage())));
        }
    }

    private void getSummary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Non autorizzato")));
            return;
        }
        Utente utente = (Utente) session.getAttribute("utente");
        int orderId = parseInt(req.getParameter("orderId"), -1);
        if (orderId <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Parametro orderId mancante")));
            return;
        }

        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            OrdineDao ordineDao = new OrdineDao(conn);
            OrdineArticoloDao ordineArticoloDao = new OrdineArticoloDao(conn);
            ArticoloDao articoloDao = new ArticoloDao(conn);

            Ordine ordine = ordineDao.getOrdineById(orderId);
            if (ordine == null || ordine.getUtenteId() != utente.getId()) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Ordine non trovato")));
                return;
            }

            List<OrdineArticolo> righe = ordineArticoloDao.getOrdineArticoliByOrdineId(orderId);
            List<Map<String, Object>> items = new ArrayList<>();
            for (OrdineArticolo oa : righe) {
                Articolo a = articoloDao.getArticoloById(oa.getArticoloId());
                Map<String, Object> row = new HashMap<>();
                row.put("articolo", Dto.ArticoloDto.from(a));
                row.put("qty", oa.getQuantita());
                items.add(row);
            }

            String fatturaUrl = req.getContextPath() + "/api/invoice/pdf?orderId=" + orderId;

            Map<String, Object> data = new HashMap<>();
            data.put("ordine", ordine);
            data.put("items", items);
            data.put("fatturaUrl", fatturaUrl);

            resp.getWriter().write(gson.toJson(ApiResponse.ok(data)));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore nel recupero del riepilogo: " + e.getMessage())));
        }
    }

    private void getUserOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Non autorizzato")));
            return;
        }
        Utente utente = (Utente) session.getAttribute("utente");
        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            OrdineDao ordineDao = new OrdineDao(conn);
            List<Model.Ordine> ordini = ordineDao.getOrdiniByUtenteId(utente.getId());
            List<Map<String, Object>> out = new ArrayList<>();
            for (Model.Ordine o : ordini) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", o.getId());
                m.put("numeroArticoli", o.getNumeroArticoli());
                m.put("importo", o.getImporto());
                m.put("stato", o.getStato() != null ? o.getStato().name() : null);
                m.put("dataCreazione", o.getDataCreazione());
                m.put("fatturaUrl", req.getContextPath() + "/api/invoice/pdf?orderId=" + o.getId());
                out.add(m);
            }
            resp.getWriter().write(gson.toJson(ApiResponse.ok(out)));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore nel recupero ordini utente: " + e.getMessage())));
        }
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}