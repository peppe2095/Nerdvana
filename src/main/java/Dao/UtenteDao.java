package Dao;

import Model.Utente;
import Model.Enum.Ruolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtenteDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public UtenteDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare un nuovo utente nel database
    public void addUtente(Utente utente) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO UTENTE (nome, cognome, email, numeroCivico, indirizzo, cap, dataNascita, ruolo, telefono, passwordHash, cittaResidenza) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getNumeroCivico());
            ps.setString(5, utente.getIndirizzo());
            ps.setString(6, utente.getCap());
            ps.setDate(7, new java.sql.Date(utente.getDataNascita().getTime()));
            ps.setString(8, utente.getRuolo().name());
            ps.setString(9, utente.getTelefono());
            ps.setString(10, utente.getPasswordHash());
            ps.setString(11, utente.getCittaResidenza());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare un utente tramite il suo ID
    public Utente getUtenteById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        Utente utente = null;

        try {
            String sql = "SELECT * FROM UTENTE WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                utente = new Utente();
                utente.setNome(rs.getString("nome"));
                utente.setCognome(rs.getString("cognome"));
                utente.setEmail(rs.getString("email"));
                utente.setNumeroCivico(rs.getString("numeroCivico"));
                utente.setIndirizzo(rs.getString("indirizzo"));
                utente.setCap(rs.getString("cap"));
                utente.setDataNascita(rs.getDate("dataNascita"));
                utente.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                utente.setTelefono(rs.getString("telefono"));
                utente.setPasswordHash(rs.getString("passwordHash"));
                utente.setCittaResidenza(rs.getString("cittaResidenza"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'utente", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return utente;
    }

    // Metodo per recuperare un utente tramite email (utile per il login)
    public Utente getUtenteByEmail(String email) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Utente utente = null;

        try {
            String sql = "SELECT * FROM UTENTE WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                utente = new Utente();
                utente.setNome(rs.getString("nome"));
                utente.setCognome(rs.getString("cognome"));
                utente.setEmail(rs.getString("email"));
                utente.setNumeroCivico(rs.getString("numeroCivico"));
                utente.setIndirizzo(rs.getString("indirizzo"));
                utente.setCap(rs.getString("cap"));
                utente.setDataNascita(rs.getDate("dataNascita"));
                utente.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                utente.setTelefono(rs.getString("telefono"));
                utente.setPasswordHash(rs.getString("passwordHash"));
                utente.setCittaResidenza(rs.getString("cittaResidenza"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'utente per email", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return utente;
    }

    // Metodo per recuperare tutti gli utenti per ruolo
    public List<Utente> getUtentiByRuolo(Ruolo ruolo) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Utente> utenti = new ArrayList<>();

        try {
            String sql = "SELECT * FROM UTENTE WHERE ruolo = ? ORDER BY cognome, nome";
            ps = connection.prepareStatement(sql);
            ps.setString(1, ruolo.name());
            rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = new Utente();
                utente.setNome(rs.getString("nome"));
                utente.setCognome(rs.getString("cognome"));
                utente.setEmail(rs.getString("email"));
                utente.setNumeroCivico(rs.getString("numeroCivico"));
                utente.setIndirizzo(rs.getString("indirizzo"));
                utente.setCap(rs.getString("cap"));
                utente.setDataNascita(rs.getDate("dataNascita"));
                utente.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                utente.setTelefono(rs.getString("telefono"));
                utente.setPasswordHash(rs.getString("passwordHash"));
                utente.setCittaResidenza(rs.getString("cittaResidenza"));
                utenti.add(utente);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli utenti per ruolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return utenti;
    }

    // Metodo per recuperare tutti gli utenti
    public List<Utente> getAllUtenti() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Utente> utenti = new ArrayList<>();

        try {
            String sql = "SELECT * FROM UTENTE ORDER BY cognome, nome";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = new Utente();
                utente.setNome(rs.getString("nome"));
                utente.setCognome(rs.getString("cognome"));
                utente.setEmail(rs.getString("email"));
                utente.setNumeroCivico(rs.getString("numeroCivico"));
                utente.setIndirizzo(rs.getString("indirizzo"));
                utente.setCap(rs.getString("cap"));
                utente.setDataNascita(rs.getDate("dataNascita"));
                utente.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                utente.setTelefono(rs.getString("telefono"));
                utente.setPasswordHash(rs.getString("passwordHash"));
                utente.setCittaResidenza(rs.getString("cittaResidenza"));
                utenti.add(utente);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutti gli utenti", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return utenti;
    }

    // Metodo per aggiornare un utente esistente
    public void updateUtente(Utente utente) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE UTENTE SET nome=?, cognome=?, email=?, numeroCivico=?, indirizzo=?, cap=?, dataNascita=?, ruolo=?, telefono=?, passwordHash=?, cittaResidenza=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getNumeroCivico());
            ps.setString(5, utente.getIndirizzo());
            ps.setString(6, utente.getCap());
            ps.setDate(7, new java.sql.Date(utente.getDataNascita().getTime()));
            ps.setString(8, utente.getRuolo().name());
            ps.setString(9, utente.getTelefono());
            ps.setString(10, utente.getPasswordHash());
            ps.setString(11, utente.getCittaResidenza());
            ps.setInt(12, utente.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per aggiornare solo la password di un utente
    public void updatePasswordUtente(int id, String nuovaPasswordHash) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE UTENTE SET passwordHash=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, nuovaPasswordHash);
            ps.setInt(2, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento della password dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per verificare se un'email esiste già
    public boolean existsByEmail(String email) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            String sql = "SELECT COUNT(*) FROM UTENTE WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nella verifica dell'esistenza dell'email", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exists;
    }

    // Metodo per cancellare un utente tramite il suo ID
    public void deleteUtente(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM UTENTE WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare un utente tramite email
    public void deleteUtenteByEmail(String email) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM UTENTE WHERE email=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione dell'utente per email", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}