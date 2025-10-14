package Control;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dto.ApiResponse;
import Model.Utente;
import Utils.PasswordUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Connessione.GestoreConnessioneDatabase;
import Dao.UtenteDao;

//@WebServlet("/API/autenticazione/*")
public class autenticazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().create();

    public autenticazioneServlet() {
        super();
    }

    protected void gestisciRegistrazione(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String dataNascitaParam = request.getParameter("dataNascita");
        String cittaResidenza = request.getParameter("cittaResidenza");
        String indirizzo = request.getParameter("indirizzo");
        String numeroCivico = request.getParameter("numeroCivico");
        String cap = request.getParameter("cap");
        String telefono = request.getParameter("telefono");
        
        if (nome == null || cognome == null || email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.error("Tutti i campi sono obbligatori")));
            return;
        }
        
        Date dataNascita = null;
        if (dataNascitaParam != null && !dataNascitaParam.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                dataNascita = sdf.parse(dataNascitaParam);
                
                if (dataNascita.after(new Date())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(gson.toJson(ApiResponse.error("La data di nascita non può essere futura")));
                    return;
                }
            } catch (ParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Formato data non valido (usa yyyy-MM-dd)")));
                return;
            }
        }
        
        try {
            Connection connection = GestoreConnessioneDatabase.getConnection();
            UtenteDao utenteDao = new UtenteDao(connection);
            
            System.out.println("ho creato utenteDao");
            if (utenteDao.existsByEmail(email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Email già in uso")));
                return;
            }
            
            Utente utente = new Utente();
            utente.setNome(nome);
            System.out.println("nome: " + nome);
            utente.setCognome(cognome);
            System.out.println("cognome: " + cognome);
            utente.setEmail(email);
            System.out.println("email: " + email);
            utente.setPasswordHash(PasswordUtils.hashPassword(password));
            System.out.println("password hashata");
            utente.setCittaResidenza(cittaResidenza);
            System.out.println("citta: " + cittaResidenza);
            utente.setIndirizzo(indirizzo);
            System.out.println("indirizzo: " + indirizzo);
            utente.setNumeroCivico(numeroCivico);
            System.out.println("numero civico: " + numeroCivico);
            utente.setCap(cap);
            System.out.println("cap: " + cap);
            utente.setTelefono(telefono);
            System.out.println("telefono: " + telefono);
            utente.setDataNascita(dataNascita);
            System.out.println("data di nascita: " + dataNascita);
            utente.setRuolo(Model.Enum.Ruolo.cliente);
            System.out.println("ruolo: cliente");
            
            utenteDao.addUtente(utente);
            System.out.println("ho aggiunto utente");
            
            utente = utenteDao.getUtenteByEmail(email);
            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);
            
            System.out.println("ho settato utente in sessione");
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.okMessage("Registrazione avvenuta con successo")));
            
        } catch (Exception e) {
            System.out.println("errore catch: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);	
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.error("Errore durante la registrazione: " + e.getMessage())));
        }
    }

    protected void gestisciLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.error("Email e password sono obbligatorie")));
            return;
        }
        
        try {
            Connection connection = GestoreConnessioneDatabase.getConnection();
            UtenteDao utenteDao = new UtenteDao(connection);
            
            System.out.println("ho creato utenteDao per login");
            Utente utente = utenteDao.getUtenteByEmail(email);
            
            if (utente == null || !PasswordUtils.checkPassword(password, utente.getPasswordHash())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(ApiResponse.error("Email o password errate")));
                return;
            }
            
            // Login riuscito - crea sessione
            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);
            
            System.out.println("Login effettuato per utente: " + utente.getEmail());
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.okMessage("Login effettuato con successo")));
            
        } catch (Exception e) {
            System.out.println("errore catch login: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.error("Errore durante il login: " + e.getMessage())));
        }
    }

    protected void gestisciLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Sessione invalidata");
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(ApiResponse.okMessage("Logout avvenuto con successo")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("autenticazioneServlet doPost");
        String path = request.getPathInfo();
        System.out.println("Path: " + path);
        
        if (path == null) {
            path = "";
        }
        
        if (path.equals("/registrazione")) {
            System.out.println("registrazione");
            gestisciRegistrazione(request, response);
        } else if (path.equals("/login")) {
            System.out.println("login");
            gestisciLogin(request, response);
        } else if (path.equals("/logout")) {
            System.out.println("logout");
            gestisciLogout(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(ApiResponse.error("Endpoint non trovato")));
        }
    }
}