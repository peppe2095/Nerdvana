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
 * Servlet per gestire il recupero di articoli dal database con funzionalità di paginazione e filtraggio.
 * Esempio di chiamata JSON:
 * GET /getArticoli?format=json&page=1&size=8&tipo=Manga
 * - format=json: richiede risposta in formato JSON
 * - page=1: numero pagina (default 1)
 * - size=8: elementi per pagina (default 8)
 * - tipo=Manga: filtra solo articoli di tipo Manga (opzionale)
 */
public class getArticoli extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Estrae i parametri della richiesta
        String format = param(request, "format");       // Formato risposta (json/html)
        String tipoParam = param(request, "tipo");      // Filtro per tipo articolo
        int page = intParam(request, "page", 1);       // Numero pagina (default 1)
        int size = intParam(request, "size", 8);       // Elementi per pagina (default 8)

        // Converte il parametro 'tipo' in enum Tipo se valido
        // Es: "Manga" -> Tipo.MANGA, stringa non valida -> null (nessun filtro)
        Tipo tipoFilter = null;
        if (tipoParam != null && !tipoParam.isEmpty()) {
            try {
                tipoFilter = Tipo.valueOf(tipoParam);
            } catch (IllegalArgumentException ignored) {
                // Tipo non valido: lasciamo null per non applicare filtro
            }
        }

        // Inizializza Gson per serializzazione JSON
        Gson gson = new GsonBuilder().create();

        // Variabili per gestione paginazione e risultati
        Connection connection = null;
        List<Articolo> pageItems = new ArrayList<>();   // Articoli della pagina corrente
        int total = 0;                                  // Totale articoli
        int totalPages = 1;                            // Totale pagine

        try {
            // Ottiene connessione dal pool
            connection = GestoreConnessioneDatabase.getConnection();
            ArticoloDao articoloDao = new ArticoloDao(connection);

            // Calcola totale articoli e pagine
            total = articoloDao.countArticoli(tipoFilter);
            if (size <= 0) size = 8;                   // Normalizza size
            if (page <= 0) page = 1;                   // Normalizza page
            // Calcola il numero totale di pagine dividendo il totale degli articoli per la dimensione della pagina
            // e arrotondando per eccesso per gestire divisioni non esatte (es: 10 articoli / 3 per pagina = 4 pagine)
            totalPages = (int) Math.ceil(total / (double) size);
            if (totalPages == 0) totalPages = 1;       // Almeno 1 pagina
            if (page > totalPages) page = totalPages;  // Evita pagine fuori range

            // Recupera articoli paginati e filtrati
            pageItems = articoloDao.getArticoliPaged(page, size, tipoFilter);

        } catch (SQLException e) {
            // In caso di errore SQL, se il formato richiesto è JSON:
            // 1. Imposta lo status code 500 (errore interno)
            // 2. Imposta il tipo di contenuto a JSON
            // 3. Invia una risposta JSON con il messaggio di errore
            if ("json".equalsIgnoreCase(format)) {
                // Formato JSON: risposta errore 500 con messaggio
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Errore nel caricamento degli articoli: " + e.getMessage())));
                return;
            }
            request.setAttribute("error", "Errore nel caricamento degli articoli: " + e.getMessage());
        } catch (Exception e) {
            // Gestisce altri errori imprevisti
            if ("json".equalsIgnoreCase(format)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Errore imprevisto: " + e.getMessage())));
                return;
            }
            request.setAttribute("error", "Errore imprevisto: " + e.getMessage());
        } finally {
            // Chiude sempre la connessione
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }

        //QUESTO SI CHIAMA Pattern di "Content Negotiation"
        // che permette alla stessa servlet di rispondere in modi diversi a seconda del tipo di richiesta ricevuta.

        // CASO A:
        // L'IF controlla se il tipo di ritorno deve essere JSON e
        // serve per gestire Richieste AJAX da JavaScript: getArticoli?format=json&page=1&size=8
        // Allora facciamo in modo di ritornare solo i dati in formato JSON

        if ("json".equalsIgnoreCase(format)) {
            // Risposta JSON: converte articoli in DTO (Data Transfer Object)
            List<ArticoloDto> items = pageItems.stream()  // Crea uno stream dalla lista di articoli
                    .map(ArticoloDto::from)  // Converte ogni Articolo in ArticoloDto usando il metodo from()
                    .collect(Collectors.toList());  // Raccoglie i DTO convertiti in una nuova lista

            PagedResponse<ArticoloDto> payload = new PagedResponse<>(items, page, size, total, totalPages);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(payload));
            //NELLA RESPONSE SARÀ PRESENTE UN OGGETTO SIMILE:
            /*
            {
            "items": [
                {
                    "numeroSeriale": "ART001",
                        "nome": "One Piece Vol.1",
                        "tipo": "MANGA",
                        "prezzo": 15.5,
                        "quantita": 10,
                        "descrizione": "Primo volume del manga...",
                        "url": "Database/Images/onepiece1.jpg"
                }
            ],
            "page": 2,
                "size": 8,
                "total": 156,
                "totalPages": 20
        }*/
            return;
        }

        // CASO B Risposta HTML: passa attributi alla JSP
        // In questo caso pageItems viene associato alla richiesta nella variabile "articoli"
        // Poi dalla JSP possiamo accedere agli articoli così:
        // <% List articoli = (List)request.getAttribute("articoli"); %>
        request.setAttribute("articoli", pageItems);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * Implementa POST reindirizzando a GET
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    //FUNZIONE CHE CONTROLLA SE I PARAMETRI SONO NULL O VUOTI
    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null && !v.trim().isEmpty() ? v.trim() : null;
    }

    //FUNZIONE CHE CONTROLLA SE I PARAMETRI SONO DEI NUMERI INTERI VALIDI
    private static int intParam(HttpServletRequest req, String name, int def) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (Exception e) {
            return def;
        }
    }

}