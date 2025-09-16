package Dao;

import Model.CartaDiCredito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartaDiCreditoDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public CartaDiCreditoDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare una nuova carta di credito nel database
    public void addCartaDiCredito(CartaDiCredito carta) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO CARTA_DI_CREDITO (utenteId, nomeTitolare, numeroCarta, cvv, scadenza) VALUES (?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, carta.getUtenteId());
            ps.setString(2, carta.getNomeTitolare());
            ps.setString(3, carta.getNumeroCarta());
            ps.setString(4, carta.getCvv());
            ps.setDate(5, new java.sql.Date(carta.getScadenza().getTime()));
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio della carta di credito", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare una carta di credito tramite il suo ID
    public CartaDiCredito getCartaDiCreditoById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        CartaDiCredito carta = null;

        try {
            String sql = "SELECT * FROM CARTA_DI_CREDITO WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                carta = new CartaDiCredito();
                carta.setUtenteId(rs.getInt("utenteId"));
                carta.setNomeTitolare(rs.getString("nomeTitolare"));
                carta.setNumeroCarta(rs.getString("numeroCarta"));
                carta.setCvv(rs.getString("cvv"));
                carta.setScadenza(rs.getDate("scadenza"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero della carta di credito", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return carta;
    }

    // Metodo per recuperare tutte le carte di credito di un utente
    public List<CartaDiCredito> getCarteDiCreditoByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CartaDiCredito> carte = new ArrayList<>();

        try {
            String sql = "SELECT * FROM CARTA_DI_CREDITO WHERE utenteId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            rs = ps.executeQuery();

            while (rs.next()) {
                CartaDiCredito carta = new CartaDiCredito();
                carta.setUtenteId(rs.getInt("utenteId"));
                carta.setNomeTitolare(rs.getString("nomeTitolare"));
                carta.setNumeroCarta(rs.getString("numeroCarta"));
                carta.setCvv(rs.getString("cvv"));
                carta.setScadenza(rs.getDate("scadenza"));
                carte.add(carta);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero delle carte di credito dell'utente", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return carte;
    }

    // Metodo per recuperare tutte le carte di credito
    public List<CartaDiCredito> getAllCarteDiCredito() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CartaDiCredito> carte = new ArrayList<>();

        try {
            String sql = "SELECT * FROM CARTA_DI_CREDITO";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                CartaDiCredito carta = new CartaDiCredito();
                carta.setUtenteId(rs.getInt("utenteId"));
                carta.setNomeTitolare(rs.getString("nomeTitolare"));
                carta.setNumeroCarta(rs.getString("numeroCarta"));
                carta.setCvv(rs.getString("cvv"));
                carta.setScadenza(rs.getDate("scadenza"));
                carte.add(carta);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutte le carte di credito", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return carte;
    }

    // Metodo per aggiornare una carta di credito esistente
    public void updateCartaDiCredito(CartaDiCredito carta) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE CARTA_DI_CREDITO SET utenteId=?, nomeTitolare=?, numeroCarta=?, cvv=?, scadenza=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, carta.getUtenteId());
            ps.setString(2, carta.getNomeTitolare());
            ps.setString(3, carta.getNumeroCarta());
            ps.setString(4, carta.getCvv());
            ps.setDate(5, new java.sql.Date(carta.getScadenza().getTime()));
            ps.setInt(6, carta.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento della carta di credito", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare una carta di credito tramite il suo ID
    public void deleteCartaDiCredito(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM CARTA_DI_CREDITO WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione della carta di credito", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutte le carte di credito di un utente
    public void deleteCarteDiCreditoByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM CARTA_DI_CREDITO WHERE utenteId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione delle carte di credito dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}