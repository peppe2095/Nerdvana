package Dao;

import Model.OrdineArticolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdineArticoloDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public OrdineArticoloDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare un nuovo ordine-articolo nel database
    public void addOrdineArticolo(OrdineArticolo ordineArticolo) throws SQLException {
        PreparedStatement ps = null;
        String sql = "INSERT INTO ORDINE_ARTICOLO (ordine_id, articolo_id, quantita) VALUES (?, ?, ?)";
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineArticolo.getOrdineId());
            ps.setInt(2, ordineArticolo.getArticoloId());
            ps.setInt(3, ordineArticolo.getQuantita());

            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ignore) {}
            throw new SQLException("Errore nel salvataggio dell'ordine-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare un ordine-articolo tramite il suo ID
    public OrdineArticolo getOrdineArticoloById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        OrdineArticolo ordineArticolo = null;

        try {
            String sql = "SELECT * FROM ORDINE_ARTICOLO WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                ordineArticolo = new OrdineArticolo();
                ordineArticolo.setOrdineId(rs.getInt("ordine_id"));
                ordineArticolo.setArticoloId(rs.getInt("articolo_id"));
                ordineArticolo.setQuantita(rs.getInt("quantita"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'ordine-articolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordineArticolo;
    }

    // Metodo per recuperare tutti gli articoli di un ordine specifico
    public List<OrdineArticolo> getOrdineArticoliByOrdineId(int ordineId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrdineArticolo> ordineArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE_ARTICOLO WHERE ordine_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrdineArticolo ordineArticolo = new OrdineArticolo();
                ordineArticolo.setOrdineId(rs.getInt("ordine_id"));
                ordineArticolo.setArticoloId(rs.getInt("articolo_id"));
                ordineArticolo.setQuantita(rs.getInt("quantita"));
                ordineArticoli.add(ordineArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli articoli dell'ordine", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordineArticoli;
    }

    // Metodo per recuperare tutti gli ordini che contengono un articolo specifico
    public List<OrdineArticolo> getOrdineArticoliByArticoloId(int articoloId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrdineArticolo> ordineArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE_ARTICOLO WHERE articolo_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, articoloId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrdineArticolo ordineArticolo = new OrdineArticolo();
                ordineArticolo.setOrdineId(rs.getInt("ordine_id"));
                ordineArticolo.setArticoloId(rs.getInt("articolo_id"));
                ordineArticolo.setQuantita(rs.getInt("quantita"));
                ordineArticoli.add(ordineArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli ordini per l'articolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordineArticoli;
    }

    // Metodo per recuperare un ordine-articolo specifico tramite ordineId e articoloId
    public OrdineArticolo getOrdineArticoloByOrdineIdAndArticoloId(int ordineId, int articoloId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        OrdineArticolo ordineArticolo = null;

        try {
            String sql = "SELECT * FROM ORDINE_ARTICOLO WHERE ordine_id = ? AND articolo_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineId);
            ps.setInt(2, articoloId);
            rs = ps.executeQuery();

            if (rs.next()) {
                ordineArticolo = new OrdineArticolo();
                ordineArticolo.setOrdineId(rs.getInt("ordine_id"));
                ordineArticolo.setArticoloId(rs.getInt("articolo_id"));
                ordineArticolo.setQuantita(rs.getInt("quantita"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'ordine-articolo specifico", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordineArticolo;
    }

    // Metodo per recuperare tutti gli ordine-articoli
    public List<OrdineArticolo> getAllOrdineArticoli() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrdineArticolo> ordineArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE_ARTICOLO";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrdineArticolo ordineArticolo = new OrdineArticolo();
                ordineArticolo.setOrdineId(rs.getInt("ordine_id"));
                ordineArticolo.setArticoloId(rs.getInt("articolo_id"));
                ordineArticolo.setQuantita(rs.getInt("quantita"));
                ordineArticoli.add(ordineArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutti gli ordine-articoli", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordineArticoli;
    }

    // Metodo per aggiornare un ordine-articolo esistente
    public void updateOrdineArticolo(OrdineArticolo ordineArticolo) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ORDINE_ARTICOLO SET ordine_id=?, articolo_id=?, quantita=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineArticolo.getOrdineId());
            ps.setInt(2, ordineArticolo.getArticoloId());
            ps.setInt(3, ordineArticolo.getQuantita());
            ps.setInt(4, ordineArticolo.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento dell'ordine-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare un ordine-articolo tramite il suo ID
    public void deleteOrdineArticolo(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ORDINE_ARTICOLO WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione dell'ordine-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutti gli articoli di un ordine specifico
    public void deleteOrdineArticoliByOrdineId(int ordineId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ORDINE_ARTICOLO WHERE ordineId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordineId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione degli articoli dell'ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutti gli ordini che contengono un articolo specifico
    public void deleteOrdineArticoliByArticoloId(int articoloId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ORDINE_ARTICOLO WHERE articolo_id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, articoloId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione degli ordini per l'articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}