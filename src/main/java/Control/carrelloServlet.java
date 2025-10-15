package Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Dto.ApiResponse;
import Dto.ArticoloDto;
import Connessione.GestoreConnessioneDatabase;
import Dao.ArticoloDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class carrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Questo metodo gestisce le richieste GET verso la servlet del carrello
        // Ci sono due possibili endpoint:
        // 1. /api/cart/items -> restituisce lista dettagliata degli articoli con quantità 
        // 2. /api/cart -> restituisce solo la mappa id->quantità

        // Otteniamo il path dalla richiesta (es. /items oppure nulla)
        String path = req.getPathInfo();
        if (path == null) path = "";

        // Se il path è "/items" eseguiamo la logica per ottenere i dettagli completi
        if ("/items".equals(path)) {
            // Recuperiamo la sessione utente (creandola se non esiste)
            HttpSession s = req.getSession(true);

            // Otteniamo il carrello dalla sessione usando il metodo helper getCart()
            Map<String, Integer> cart = getCart(s);

            // Creiamo una lista per contenere gli articoli con le relative quantità
            List<java.util.Map<String, Object>> items = new ArrayList<>();

            // Apriamo una connessione al database usando try-with-resources per chiuderla automaticamente
            try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
                // Creiamo un DAO per accedere agli articoli
                ArticoloDao artDao = new ArticoloDao(conn);

                // Per ogni coppia id->quantità nel carrello
                for (java.util.Map.Entry<String, Integer> e : cart.entrySet()) {
                    String seriale = e.getKey();     // Numero seriale dell'articolo
                    int qty = e.getValue();          // Quantità nel carrello

                    // Recuperiamo i dettagli dell'articolo dal database
                    Model.Articolo a = artDao.getArticoloByNumeroSeriale(seriale);

                    // Se l'articolo esiste
                    if (a != null) {
                        // Convertiamo l'articolo in DTO
                        ArticoloDto dto = Dto.ArticoloDto.from(a);

                        // Creiamo una mappa per contenere articolo e quantità
                        java.util.Map<String, Object> row = new java.util.HashMap<>();
                        row.put("articolo", dto);
                        row.put("qty", qty);

                        // Aggiungiamo la riga alla lista
                        items.add(row);
                    }
                }

                // Impostiamo il tipo di risposta come JSON
                resp.setContentType("application/json;charset=UTF-8");

                // Scriviamo la risposta JSON con la lista degli articoli
                resp.getWriter().write(gson.toJson(ApiResponse.ok(items)));
                return;

            } catch (SQLException ex) {
                // In caso di errore database
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(ApiResponse.error("Errore nel recupero carrello: " + ex.getMessage())));
                return;
            }
        }

        // Se il path non è "/items", restituiamo solo la mappa grezza del carrello
        Map<String, Integer> cart = getCart(req.getSession(true));
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(ApiResponse.ok(cart)));
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        switch (path) {
            case "/add":
                add(req, resp);
                break;
            case "/remove":
                remove(req, resp);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
        }
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(true);
        Map<String, Integer> cart = getCart(s);
        String id = trim(req.getParameter("id"));
        int qty = parseInt(req.getParameter("qty"), 1);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Parametro id mancante")));
            return;
        }
        cart.put(id, cart.getOrDefault(id, 0) + Math.max(1, qty));
        s.setAttribute("carrello", cart);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(ApiResponse.ok(cart)));
    }

    private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(true);
        Map<String, Integer> cart = getCart(s);
        String id = trim(req.getParameter("id"));
        int qty = parseInt(req.getParameter("qty"), 1);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Parametro id mancante")));
            return;
        }
        int current = cart.getOrDefault(id, 0) - Math.max(1, qty);
        if (current > 0) cart.put(id, current); else cart.remove(id);
        s.setAttribute("carrello", cart);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(ApiResponse.ok(cart)));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> getCart(HttpSession s) {
        Object o = s.getAttribute("carrello");
        if (o instanceof Map) return (Map<String, Integer>) o;
        Map<String, Integer> map = new HashMap<>();
        s.setAttribute("carrello", map);
        return map;
    }

    private static String trim(String s) {
        return s == null ? null : (s.trim().isEmpty() ? null : s.trim());
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
