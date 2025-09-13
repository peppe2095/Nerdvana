package Control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import Model.User;


public class userServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Parametri di connessione al database
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/NerdvanaStore?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "admin"; // inserisci la tua password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        List<User> userList = new ArrayList<>();

        try {
            // Carica il driver JDBC di MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connessione al database
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

                // Trasforma i risultati in oggetti User
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    userList.add(new User(id, name, email));
                }

                // Converti la lista in JSON e invia al client
                String json = gson.toJson(userList);
                out.print(json);

            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Si è verificato un errore: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
}