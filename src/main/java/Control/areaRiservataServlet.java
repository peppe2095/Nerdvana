package Control;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
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
import Model.CartaDiCredito;
import Model.Enum.Ruolo;
import Dao.UtenteDao;
import Dao.CartaDiCreditoDao;
import Dto.ApiResponse;
import Dto.UtenteConCarteDto;

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
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

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
            int idUtente = utenteSessione.getId();
            

            // Verifica che l'utente stia accedendo ai propri dati (a meno che non sia admin)
            if (idUtente != utenteSessione.getId() && 
                !"ADMIN".equalsIgnoreCase(utenteSessione.getRuolo().name())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Non hai i permessi per accedere a questi dati")
                ));
                return;
            }

            // Recupera i dati aggiornati dal database
            Connection connection = GestoreConnessioneDatabase.getConnection();
            UtenteDao utenteDao = new UtenteDao(connection);
            CartaDiCreditoDao cartaDao = new CartaDiCreditoDao(connection);
            
            Utente utente = utenteDao.getUtenteById(idUtente);

            if (utente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Utente non trovato")
                ));
                return;
            }

            // Recupera le carte di credito dell'utente
            List<CartaDiCredito> carte = cartaDao.getCarteDiCreditoByUtenteId(idUtente);
            
            // Mascherare i dati sensibili delle carte
            for (CartaDiCredito carta : carte) {
                // Maschera numero carta (mostra solo ultime 4 cifre)
                String numCarta = carta.getNumeroCarta();
                if (numCarta != null && numCarta.length() >= 4) {
                    String ultimeCifre = numCarta.substring(numCarta.length() - 4);
                    carta.setNumeroCarta("**** **** **** " + ultimeCifre);
                }
                // Maschera CVV completamente
                carta.setCvv("***");
            }

            // Rimuovi la password dall'oggetto per sicurezza
            utente.setPasswordHash(null);

            // Crea il DTO con utente e carte
            UtenteConCarteDto dto = new UtenteConCarteDto(utente, carte);

            // Ritorna i dati
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(
                ApiResponse.ok(dto)
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
            int idUtente = utenteSessione.getId();

            // Recupera i parametri della carta
            String nomeTitolare = request.getParameter("nomeTitolare");
            String numeroCarta = request.getParameter("numeroCarta");
            String cvv = request.getParameter("cvv");
            String scadenzaStr = request.getParameter("scadenza");

            // Validazione
            if (nomeTitolare == null || nomeTitolare.trim().isEmpty() ||
                numeroCarta == null || numeroCarta.trim().isEmpty() ||
                cvv == null || cvv.trim().isEmpty() ||
                scadenzaStr == null || scadenzaStr.trim().isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Tutti i campi sono obbligatori")
                ));
                return;
            }

            // Rimuovi spazi dal numero carta
            numeroCarta = numeroCarta.replaceAll("\\s+", "");

            // Validazione numero carta (deve essere 16 cifre)
            if (!numeroCarta.matches("\\d{16}")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Il numero carta deve contenere 16 cifre")
                ));
                return;
            }

            // Validazione CVV (deve essere 3 cifre)
            if (!cvv.matches("\\d{3}")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("Il CVV deve contenere 3 cifre")
                ));
                return;
            }

            // Parsing della data di scadenza
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date scadenza = sdf.parse(scadenzaStr);

            // Verifica che la carta non sia scaduta
            if (scadenza.before(new java.util.Date())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(
                    ApiResponse.error("La carta di credito è scaduta")
                ));
                return;
            }
            // Crea l'oggetto CartaDiCredito
            CartaDiCredito nuovaCarta = new CartaDiCredito(
                idUtente,
                nomeTitolare,
                numeroCarta,
                cvv,
                scadenza
            );

            // Salva nel database
            Connection connection = GestoreConnessioneDatabase.getConnection();
            CartaDiCreditoDao cartaDao = new CartaDiCreditoDao(connection);
            cartaDao.addCartaDiCredito(nuovaCarta);
            
            // Recupera i dati aggiornati
            UtenteDao utenteDao = new UtenteDao(connection);
            Utente utente = utenteDao.getUtenteById(idUtente);
            List<CartaDiCredito> carte = cartaDao.getCarteDiCreditoByUtenteId(idUtente);

            // Mascherare i dati sensibili delle carte
            for (CartaDiCredito carta : carte) {
                String numCarta = carta.getNumeroCarta();
                if (numCarta != null && numCarta.length() >= 4) {
                    String ultimeCifre = numCarta.substring(numCarta.length() - 4);
                    carta.setNumeroCarta("**** **** **** " + ultimeCifre);
                }
                carta.setCvv("***");
            }

            utente.setPasswordHash(null);

            // Crea il DTO con utente e carte aggiornate
            UtenteConCarteDto dto = new UtenteConCarteDto(utente, carte);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(
                ApiResponse.ok(dto)
            ));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(
                ApiResponse.error("Errore nell'aggiunta della carta: " + e.getMessage())
            ));
        }
    }
}