package Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Dto.ApiResponse;
import Dto.ArticoloDto;
import Connessione.GestoreConnessioneDatabase;
import Dao.WishListDao;
import Dao.WishListArticoloDao;
import Dao.ArticoloDao;
import Model.Utente;
import Model.WishList;
import Model.WishListArticolo;

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
import java.util.HashSet;
import java.util.Set;

public class WishlistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Consentiamo solo /items: la wishlist è gestita esclusivamente a livello DB per utenti autenticati
        String path = req.getPathInfo();
        if (path == null) path = "";
        if ("/items".equals(path)) {
            listItems(req, resp);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
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

    // Aggiunge un prodotto alla wishlist
    // Richiede utente autenticato: la wishlist è salvata su DB (WISHLIST, WISHLIST_ARTICOLO)
    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(true);
        String numeroSeriale = trim(req.getParameter("id")); // nel frontend usiamo il numeroSeriale come id logico
        if (numeroSeriale == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Parametro id mancante")));
            return;
        }

        Utente utente = (Utente) s.getAttribute("utente");
        if (utente == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Devi effettuare il login per usare la wishlist")));
            return;
        }

        // Persistenza su DB
        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            WishListDao wlDao = new WishListDao(conn);
            WishListArticoloDao wlaDao = new WishListArticoloDao(conn);
            ArticoloDao artDao = new ArticoloDao(conn);

            WishList wl = wlDao.getOrCreateWishListForUtente(utente.getId());
            // Ricavo l'articolo dal numero seriale
            Model.Articolo art = artDao.getArticoloByNumeroSeriale(numeroSeriale);
            if (art == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(ApiResponse.error("Articolo non trovato")));
                return;
            }
            // Evito duplicati
            if (!wlaDao.existsWishListArticolo(wl.getId(), art.getId())) {
                wlaDao.addWishListArticolo(new Model.WishListArticolo(wl.getId(), art.getId()));
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.okMessage("ok")));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore DB wishlist: " + e.getMessage())));
        }
    }

    // Rimuove un prodotto dalla wishlist
    // Richiede utente autenticato: elimina la riga da WISHLIST_ARTICOLO
    private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(true);
        String numeroSeriale = trim(req.getParameter("id"));
        if (numeroSeriale == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Parametro id mancante")));
            return;
        }

        Utente utente = (Utente) s.getAttribute("utente");
        if (utente == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Devi effettuare il login per usare la wishlist")));
            return;
        }

        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            WishListDao wlDao = new WishListDao(conn);
            WishListArticoloDao wlaDao = new WishListArticoloDao(conn);
            ArticoloDao artDao = new ArticoloDao(conn);

            WishList wl = wlDao.getOrCreateWishListForUtente(utente.getId());
            Model.Articolo art = artDao.getArticoloByNumeroSeriale(numeroSeriale);
            if (art != null) {
                wlaDao.deleteWishListArticoloByWishlistIdAndArticoloId(wl.getId(), art.getId());
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.okMessage("ok")));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore DB wishlist: " + e.getMessage())));
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> getWishlist(HttpSession s) {
        Object o = s.getAttribute("wishlist");
        if (o instanceof Set) return (Set<String>) o;
        Set<String> set = new HashSet<>();
        s.setAttribute("wishlist", set);
        return set;
    }

    private static String trim(String s) {
        return s == null ? null : (s.trim().isEmpty() ? null : s.trim());
    }

    private void listItems(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(true);
        Utente utente = (Utente) s.getAttribute("utente");
        if (utente == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Devi effettuare il login per usare la wishlist")));
            return;
        }
        List<ArticoloDto> items = new ArrayList<>();
        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            ArticoloDao artDao = new ArticoloDao(conn);
            // Lettura da DB
            WishListDao wlDao = new WishListDao(conn);
            WishList wl = wlDao.getOrCreateWishListForUtente(utente.getId());
            WishListArticoloDao wlaDao = new WishListArticoloDao(conn);
            List<WishListArticolo> righe = wlaDao.getWishListArticoliByWishlistId(wl.getId());
            for (WishListArticolo r : righe) {
                Model.Articolo a = artDao.getArticoloById(r.getArticoloId());
                if (a != null) items.add(ArticoloDto.from(a));
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.ok(items)));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(ApiResponse.error("Errore nel recupero wishlist: " + e.getMessage())));
        }
    }
}
