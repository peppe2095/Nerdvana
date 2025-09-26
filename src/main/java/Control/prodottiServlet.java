package Control;

import Connessione.GestoreConnessioneDatabase;
import Dao.ArticoloDao;
import Model.Articolo;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/prodotti")
public class prodottiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection connection = null;
        try {
            // Ottieni la connessione dal database
        	connection = GestoreConnessioneDatabase.getConnection();
            
            ArticoloDao articoloDao = new ArticoloDao(connection);
            
            // Parametri di paginazione
            String pageParam = request.getParameter("page");
            String sizeParam = request.getParameter("size");
            String categoriaParam = request.getParameter("categoria");
            
            int currentPage = 1;
            int pageSize = DEFAULT_PAGE_SIZE;
            
            try {
                if (pageParam != null && !pageParam.trim().isEmpty()) {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
            
            try {
                if (sizeParam != null && !sizeParam.trim().isEmpty()) {
                    pageSize = Integer.parseInt(sizeParam);
                    if (pageSize < 1) pageSize = DEFAULT_PAGE_SIZE;
                    if (pageSize > 50) pageSize = 50; // Limite massimo
                }
            } catch (NumberFormatException e) {
                pageSize = DEFAULT_PAGE_SIZE;
            }
            
            // Calcola offset per la query
            int offset = (currentPage - 1) * pageSize;
            
            List<Articolo> articoli;
            int totalArticoli;
            
            if (categoriaParam != null && !categoriaParam.trim().isEmpty()) {
                // Filtra per categoria
                articoli = getArticoliByCategoriaPaginated(articoloDao, categoriaParam, offset, pageSize);
                totalArticoli = getTotalArticoliByCategoria(articoloDao, categoriaParam);
            } else {
                // Tutti gli articoli
                articoli = getArticoliPaginated(articoloDao, offset, pageSize);
                totalArticoli = getTotalArticoli(articoloDao);
            }
            
            // Calcola informazioni di paginazione
            int totalPages = (int) Math.ceil((double) totalArticoli / pageSize);
            
            // Calcola range di pagine da mostrare (es. 1-5, 6-10, etc.)
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);
            
            // Assicurati di mostrare sempre 5 pagine se possibile
            if (endPage - startPage < 4) {
                if (startPage == 1) {
                    endPage = Math.min(totalPages, startPage + 4);
                } else {
                    startPage = Math.max(1, endPage - 4);
                }
            }
            
            // Set attributi per la JSP
            request.setAttribute("articoli", articoli);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalArticoli", totalArticoli);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("startPage", startPage);
            request.setAttribute("endPage", endPage);
            request.setAttribute("categoria", categoriaParam);
            
            // Forward alla JSP
            request.getRequestDispatcher("prodotti.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "Errore nel caricamento dei prodotti: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "Errore imprevisto: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private List<Articolo> getArticoliPaginated(ArticoloDao dao, int offset, int limit) throws SQLException {
        // Implementazione semplificata - idealmente dovresti avere metodi specifici nel DAO
        List<Articolo> allArticoli = dao.getAllArticoli();
        List<Articolo> paginatedArticoli = new ArrayList<>();
        
        int start = Math.min(offset, allArticoli.size());
        int end = Math.min(offset + limit, allArticoli.size());
        
        for (int i = start; i < end; i++) {
            paginatedArticoli.add(allArticoli.get(i));
        }
        
        return paginatedArticoli;
    }
    
    private List<Articolo> getArticoliByCategoriaPaginated(ArticoloDao dao, String categoria, int offset, int limit) throws SQLException {
        List<Articolo> allArticoli = dao.getAllArticoli();
        List<Articolo> filteredArticoli = new ArrayList<>();
        
        // Filtra per categoria
        for (Articolo articolo : allArticoli) {
            if (articolo.getTipo() != null && articolo.getTipo().name().equalsIgnoreCase(categoria)) {
                filteredArticoli.add(articolo);
            }
        }
        
        // Applica paginazione
        List<Articolo> paginatedArticoli = new ArrayList<>();
        int start = Math.min(offset, filteredArticoli.size());
        int end = Math.min(offset + limit, filteredArticoli.size());
        
        for (int i = start; i < end; i++) {
            paginatedArticoli.add(filteredArticoli.get(i));
        }
        
        return paginatedArticoli;
    }
    
    private int getTotalArticoli(ArticoloDao dao) throws SQLException {
        List<Articolo> articoli = dao.getAllArticoli();
        return articoli != null ? articoli.size() : 0;
    }
    
    private int getTotalArticoliByCategoria(ArticoloDao dao, String categoria) throws SQLException {
        List<Articolo> allArticoli = dao.getAllArticoli();
        int count = 0;
        
        if (allArticoli != null) {
            for (Articolo articolo : allArticoli) {
                if (articolo.getTipo() != null && articolo.getTipo().name().equalsIgnoreCase(categoria)) {
                    count++;
                }
            }
        }
        
        return count;
    }
}

