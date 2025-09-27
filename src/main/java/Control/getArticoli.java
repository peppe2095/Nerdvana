package Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Connessione.GestoreConnessioneDatabase;
import Dao.ArticoloDao;
import Dto.ApiResponse;
import Dto.ArticoloDto;
import Dto.PagedResponse;
import Model.Articolo;
import Model.Enum.Tipo;

/**
 * Servlet semplice per ottenere gli articoli.
 * - forward a index.jsp (HTML)
 * - risposta JSON con paginazione e filtro per tipo: /getArticoli?format=json&page=1&size=8&tipo=Manga
 */
public class getArticoli extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String format = param(request, "format");
        String tipoParam = param(request, "tipo");
        int page = intParam(request, "page", 1);
        int size = intParam(request, "size", 8);

        // Converte il parametro 'tipo' in enum se valido
        Tipo tipoFilter = null;
        if (tipoParam != null && !tipoParam.isEmpty()) {
            try {
                tipoFilter = Tipo.valueOf(tipoParam);
            } catch (IllegalArgumentException ignored) {
                // tipo non valido: lasciamo null per non applicare filtro
            }
        }

        Gson gson = new GsonBuilder().create();

        Connection connection = null;
        List<Articolo> pageItems = new ArrayList<>();
        int total = 0;
        int totalPages = 1;
        try {
            connection = GestoreConnessioneDatabase.getConnection();
            ArticoloDao articoloDao = new ArticoloDao(connection);

            // Conteggio totale e calcolo pagine
            total = articoloDao.countArticoli(tipoFilter);
            if (size <= 0) size = 8;
            if (page <= 0) page = 1;
            totalPages = (int) Math.ceil(total / (double) size);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages; // evita fuori range

            // Recupero items paginati già filtrati a DB
            pageItems = articoloDao.getArticoliPaged(page, size, tipoFilter);
        } catch (SQLException e) {
            if ("json".equalsIgnoreCase(format)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Errore nel caricamento degli articoli: " + e.getMessage())));
                return;
            }
            request.setAttribute("error", "Errore nel caricamento degli articoli: " + e.getMessage());
        } catch (Exception e) {
            if ("json".equalsIgnoreCase(format)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Errore imprevisto: " + e.getMessage())));
                return;
            }
            request.setAttribute("error", "Errore imprevisto: " + e.getMessage());
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }

        if ("json".equalsIgnoreCase(format)) {
            // Mappa gli articoli in DTO per il frontend
            List<ArticoloDto> items = pageItems.stream().map(ArticoloDto::from).collect(Collectors.toList());
            PagedResponse<ArticoloDto> payload = new PagedResponse<>(items, page, size, total, totalPages);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(payload));
            return;
        }

        // Per la versione HTML classica si passa tutto (senza paginazione)
        request.setAttribute("articoli", pageItems);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null && !v.trim().isEmpty() ? v.trim() : null;
    }

    private static int intParam(HttpServletRequest req, String name, int def) {
        try { return Integer.parseInt(req.getParameter(name)); } catch (Exception e) { return def; }
    }



}
