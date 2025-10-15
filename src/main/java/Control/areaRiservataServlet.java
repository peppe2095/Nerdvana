package Control;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Connessione.GestoreConnessioneDatabase;
import Model.Utente;
import Model.Enum.Ruolo;
import Dao.UtenteDao;
import Dto.ApiResponse;

@WebServlet("/areaRiservataServlet")
public class areaRiservataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public areaRiservataServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Gson gson = new GsonBuilder().create();

        try {
            // Verifica sessione
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Sessione non valida. Effettua il login.")
                ));
                return;
            }

            // Recupera l'utente dalla sessione
            Utente utenteSessione = (Utente) session.getAttribute("utente");
            if(utenteSessione.getRuolo()== Ruolo.admin) { 
            	
            	
            }
            // Puoi anche recuperare l'ID dal parametro se necessario
            
            int idUtente =utenteSessione.getId() ;
            System.out.println("ID Utente in sessione: " + idUtente);
      
            // Verifica che l'utente stia accedendo ai propri dati
            // (a meno che non sia admin)
            if (idUtente != utenteSessione.getId() && 
                !"ADMIN".equalsIgnoreCase(utenteSessione.getRuolo().name())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Non hai i permessi per accedere a questi dati")
                ));
                return;
            }
            System.out.println("ID Utente richiesto: " + idUtente);
            // Recupera i dati aggiornati dal database
            Connection connection = GestoreConnessioneDatabase.getConnection();
            UtenteDao utenteDao = new UtenteDao(connection);
            Utente utente = utenteDao.getUtenteById(idUtente);

            if (utente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Utente non trovato")
                ));
                return;
            }

            // Rimuovi la password dall'oggetto per sicurezza
            utente.setPasswordHash(null);

            // Ritorna i dati utente
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(
                ApiResponse.ok(utente)
            ));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(
                ApiResponse.error("Formato ID utente non valido")
            ));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(
                ApiResponse.error("Errore nel recupero dei dati: " + e.getMessage())
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}