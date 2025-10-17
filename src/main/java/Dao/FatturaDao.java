package Dao;

import Model.Fattura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FatturaDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public FatturaDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare una nuova fattura nel database
    public void addFattura(Fattura fattura) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO FATTURA (ordine_id, urlFattura) VALUES (?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, fattura.getOrdineId());
            ps.setString(2, fattura.getUrlFattura());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio della fattura", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare una fattura tramite il suo ID
    public Fattura getFatturaById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        Fattura fattura = null;

        try {
            String sql = "SELECT * FROM FATTURA WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                fattura = new Fattura();
                fattura.setOrdineId(rs.getInt("ordine_id"));
                fattura.setUrlFattura(rs.getString("urlFattura"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero della fattura", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return fattura;
    }

    // Metodo per recuperare una fattura tramite l'ID dell'ordine
    public Fattura getFatturaByOrdineId(int ordineId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Fattura fattura = null;

        try {
            String sql = "SELECT * FROM FATTURA WHERE ordine_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineId);
            rs = ps.executeQuery();

            if (rs.next()) {
                fattura = new Fattura();
                fattura.setOrdineId(rs.getInt("ordine_id"));
                fattura.setUrlFattura(rs.getString("urlFattura"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero della fattura per ordine", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return fattura;
    }

    // Metodo per recuperare tutte le fatture
    public List<Fattura> getAllFatture() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Fattura> fatture = new ArrayList<>();

        try {
            String sql = "SELECT * FROM FATTURA";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Fattura fattura = new Fattura();
                fattura.setOrdineId(rs.getInt("ordine_id"));
                fattura.setUrlFattura(rs.getString("urlFattura"));
                fatture.add(fattura);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutte le fatture", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return fatture;
    }

    // Metodo per aggiornare una fattura esistente
    public void updateFattura(Fattura fattura) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE FATTURA SET ordine_id=?, urlFattura=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, fattura.getOrdineId());
            ps.setString(2, fattura.getUrlFattura());
            ps.setInt(3, fattura.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento della fattura", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare una fattura tramite il suo ID
    public void deleteFattura(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM FATTURA WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione della fattura", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare una fattura tramite l'ID dell'ordine
    public void deleteFatturaByOrdineId(int ordineId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM FATTURA WHERE ordine_id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineId);
            ps.executeUpdate() ;
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione della fattura per ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}