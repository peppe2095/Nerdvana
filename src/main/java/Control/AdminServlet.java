package Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Dto.ApiResponse;
import Model.Utente;
import Model.Articolo;
import Model.Enum.Ruolo;
import Model.Enum.Tipo;
import Model.Ordine;
import Connessione.GestoreConnessioneDatabase;
import Dao.UtenteDao;
import Dao.ArticoloDao;
import Dao.OrdineDao;
import Utils.PasswordUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().create();

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object u = session.getAttribute("utente");
        if (!(u instanceof Utente)) return false;
        return ((Utente) u).getRuolo() == Ruolo.admin;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        resp.setContentType("application/json;charset=UTF-8");

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Accesso negato: solo admin")));
            return;
        }

        try {
            if ("/ordini/list".equals(path)) {
                Connection connection = GestoreConnessioneDatabase.getConnection();
                OrdineDao ordineDao = new OrdineDao(connection);
                List<Ordine> ordini = ordineDao.getAllOrdini();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(ApiResponse.ok(ordini)));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore server: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        resp.setContentType("application/json;charset=UTF-8");

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Accesso negato: solo admin")));
            return;
        }

        try {
            if ("/articoli/add".equals(path)) {
                String numeroSeriale = req.getParameter("numeroSeriale");
                String nome = req.getParameter("nome");
                String tipoStr = req.getParameter("tipo");
                String prezzoStr = req.getParameter("prezzo");
                String quantitaStr = req.getParameter("quantita");
                String descrizione = req.getParameter("descrizione");
                String url = req.getParameter("url");

                if (numeroSeriale == null || nome == null || tipoStr == null || prezzoStr == null || quantitaStr == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(ApiResponse.error("Campi obbligatori mancanti")));
                    return;
                }

                Tipo tipo = Tipo.valueOf(tipoStr);
                double prezzo = Double.parseDouble(prezzoStr);
                int quantita = Integer.parseInt(quantitaStr);

                Connection connection = GestoreConnessioneDatabase.getConnection();
                ArticoloDao articoloDao = new ArticoloDao(connection);
                Articolo articolo = new Articolo();
                articolo.setNumeroSeriale(numeroSeriale);
                articolo.setNome(nome);
                articolo.setTipo(tipo);
                articolo.setPrezzo(prezzo);
                articolo.setQuantita(quantita);
                articolo.setDescrizione(descrizione);
                articolo.setUrl(url);
                articoloDao.addArticolo(articolo);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(ApiResponse.okMessage("Articolo aggiunto")));
            } else if ("/utenti/register-admin".equals(path)) {
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String nome = req.getParameter("nome");
                String cognome = req.getParameter("cognome");

                if (email == null || password == null || nome == null || cognome == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(ApiResponse.error("Campi obbligatori mancanti")));
                    return;
                }

                Connection connection = GestoreConnessioneDatabase.getConnection();
                UtenteDao utenteDao = new UtenteDao(connection);
                if (utenteDao.existsByEmail(email)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write(gson.toJson(ApiResponse.error("Email già in uso")));
                    return;
                }
                Utente u = new Utente();
                u.setNome(nome);
                u.setCognome(cognome);
                u.setEmail(email);
                u.setPasswordHash(PasswordUtils.hashPassword(password));
                u.setRuolo(Ruolo.admin);
                // campi opzionali vuoti per registrazione rapida admin
                u.setCap("");
                u.setCittaResidenza("");
                u.setIndirizzo("");
                u.setNumeroCivico("");
                u.setTelefono("");
                u.setDataNascita(new java.util.Date());
                utenteDao.addUtente(u);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(ApiResponse.okMessage("Admin registrato")));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore server: " + e.getMessage())));
        }
    }
}
