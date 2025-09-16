package Dao;

import Model.Ordine;
import Model.Enum.Stato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdineDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public OrdineDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare un nuovo ordine nel database
    public void addOrdine(Ordine ordine) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO ORDINE (utenteId, cartaCreditoId, numeroArticoli, importo, dataSpedizione, dataArrivo, stato, dataCreazione, dataAggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordine.getUtenteId());
            
            // Gestione del cartaCreditoId che può essere null
            if (ordine.getCartaCreditoId() != null) {
                ps.setInt(2, ordine.getCartaCreditoId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            
            ps.setInt(3, ordine.getNumeroArticoli());
            ps.setDouble(4, ordine.getImporto());
            
            // Gestione delle date che possono essere null
            if (ordine.getDataSpedizione() != null) {
                ps.setDate(5, new java.sql.Date(ordine.getDataSpedizione().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            
            if (ordine.getDataArrivo() != null) {
                ps.setDate(6, new java.sql.Date(ordine.getDataArrivo().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, ordine.getStato().name());
            ps.setDate(8, new java.sql.Date(ordine.getDataCreazione().getTime()));
            ps.setDate(9, new java.sql.Date(ordine.getDataAggiornamento().getTime()));
            
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio dell'ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare un ordine tramite il suo ID
    public Ordine getOrdineById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        Ordine ordine = null;

        try {
            String sql = "SELECT * FROM ORDINE WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                ordine = new Ordine();
                ordine.setUtenteId(rs.getInt("utenteId"));
                
                // Gestione del cartaCreditoId che può essere null
                Integer cartaCreditoId = rs.getInt("cartaCreditoId");
                if (rs.wasNull()) {
                    ordine.setCartaCreditoId(null);
                } else {
                    ordine.setCartaCreditoId(cartaCreditoId);
                }
                
                ordine.setNumeroArticoli(rs.getInt("numeroArticoli"));
                ordine.setImporto(rs.getDouble("importo"));
                ordine.setDataSpedizione(rs.getDate("dataSpedizione"));
                ordine.setDataArrivo(rs.getDate("dataArrivo"));
                ordine.setStato(Stato.valueOf(rs.getString("stato")));
                ordine.setDataCreazione(rs.getDate("dataCreazione"));
                ordine.setDataAggiornamento(rs.getDate("dataAggiornamento"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'ordine", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordine;
    }

    // Metodo per recuperare tutti gli ordini di un utente
    public List<Ordine> getOrdiniByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Ordine> ordini = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE WHERE utenteId = ? ORDER BY dataCreazione DESC";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setUtenteId(rs.getInt("utenteId"));
                
                Integer cartaCreditoId = rs.getInt("cartaCreditoId");
                if (rs.wasNull()) {
                    ordine.setCartaCreditoId(null);
                } else {
                    ordine.setCartaCreditoId(cartaCreditoId);
                }
                
                ordine.setNumeroArticoli(rs.getInt("numeroArticoli"));
                ordine.setImporto(rs.getDouble("importo"));
                ordine.setDataSpedizione(rs.getDate("dataSpedizione"));
                ordine.setDataArrivo(rs.getDate("dataArrivo"));
                ordine.setStato(Stato.valueOf(rs.getString("stato")));
                ordine.setDataCreazione(rs.getDate("dataCreazione"));
                ordine.setDataAggiornamento(rs.getDate("dataAggiornamento"));
                ordini.add(ordine);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli ordini dell'utente", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordini;
    }

    // Metodo per recuperare tutti gli ordini per stato
    public List<Ordine> getOrdiniByStato(Stato stato) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Ordine> ordini = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE WHERE stato = ? ORDER BY dataCreazione DESC";
            ps = connection.prepareStatement(sql);
            ps.setString(1, stato.name());
            rs = ps.executeQuery();

            while (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setUtenteId(rs.getInt("utenteId"));
                
                Integer cartaCreditoId = rs.getInt("cartaCreditoId");
                if (rs.wasNull()) {
                    ordine.setCartaCreditoId(null);
                } else {
                    ordine.setCartaCreditoId(cartaCreditoId);
                }
                
                ordine.setNumeroArticoli(rs.getInt("numeroArticoli"));
                ordine.setImporto(rs.getDouble("importo"));
                ordine.setDataSpedizione(rs.getDate("dataSpedizione"));
                ordine.setDataArrivo(rs.getDate("dataArrivo"));
                ordine.setStato(Stato.valueOf(rs.getString("stato")));
                ordine.setDataCreazione(rs.getDate("dataCreazione"));
                ordine.setDataAggiornamento(rs.getDate("dataAggiornamento"));
                ordini.add(ordine);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli ordini per stato", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordini;
    }

    // Metodo per recuperare tutti gli ordini
    public List<Ordine> getAllOrdini() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Ordine> ordini = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ORDINE ORDER BY dataCreazione DESC";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setUtenteId(rs.getInt("utenteId"));
                
                Integer cartaCreditoId = rs.getInt("cartaCreditoId");
                if (rs.wasNull()) {
                    ordine.setCartaCreditoId(null);
                } else {
                    ordine.setCartaCreditoId(cartaCreditoId);
                }
                
                ordine.setNumeroArticoli(rs.getInt("numeroArticoli"));
                ordine.setImporto(rs.getDouble("importo"));
                ordine.setDataSpedizione(rs.getDate("dataSpedizione"));
                ordine.setDataArrivo(rs.getDate("dataArrivo"));
                ordine.setStato(Stato.valueOf(rs.getString("stato")));
                ordine.setDataCreazione(rs.getDate("dataCreazione"));
                ordine.setDataAggiornamento(rs.getDate("dataAggiornamento"));
                ordini.add(ordine);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutti gli ordini", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ordini;
    }

    // Metodo per aggiornare un ordine esistente
    public void updateOrdine(Ordine ordine) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ORDINE SET utenteId=?, cartaCreditoId=?, numeroArticoli=?, importo=?, dataSpedizione=?, dataArrivo=?, stato=?, dataCreazione=?, dataAggiornamento=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ordine.getUtenteId());
            
            if (ordine.getCartaCreditoId() != null) {
                ps.setInt(2, ordine.getCartaCreditoId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            
            ps.setInt(3, ordine.getNumeroArticoli());
            ps.setDouble(4, ordine.getImporto());
            
            if (ordine.getDataSpedizione() != null) {
                ps.setDate(5, new java.sql.Date(ordine.getDataSpedizione().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            
            if (ordine.getDataArrivo() != null) {
                ps.setDate(6, new java.sql.Date(ordine.getDataArrivo().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, ordine.getStato().name());
            ps.setDate(8, new java.sql.Date(ordine.getDataCreazione().getTime()));
            ps.setDate(9, new java.sql.Date(ordine.getDataAggiornamento().getTime()));
            ps.setInt(10, ordine.getId());
            
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento dell'ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per aggiornare solo lo stato di un ordine
    public void updateStatoOrdine(int id, Stato nuovoStato) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ORDINE SET stato=?, dataAggiornamento=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, nuovoStato.name());
            ps.setDate(2, new java.sql.Date(new Date().getTime()));
            ps.setInt(3, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento dello stato dell'ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare un ordine tramite il suo ID
    public void deleteOrdine(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ORDINE WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione dell'ordine", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutti gli ordini di un utente
    public void deleteOrdiniByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ORDINE WHERE utenteId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione degli ordini dell'utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}