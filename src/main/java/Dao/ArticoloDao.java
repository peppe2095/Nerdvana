package Dao;

import Model.Articolo;

import Model.Enum.Tipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticoloDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public ArticoloDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare un nuovo articolo nel database
    public void addArticolo(Articolo articolo) throws SQLException {
    	//PreparedStatement serve per convertiree la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO ARTICOLO (numeroSeriale, nome, tipo, prezzo, quantita, descrizione, url) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, articolo.getNumeroSeriale());
            ps.setString(2, articolo.getNome());
            ps.setString(3, articolo.getTipo().name());
            ps.setDouble(4, articolo.getPrezzo());
            ps.setInt(5, articolo.getQuantita());
            ps.setString(6, articolo.getDescrizione());
            ps.setString(7, articolo.getUrl());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio dell'articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare un articolo tramite il suo ID
    public Articolo getArticoloById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        Articolo articolo = null;

        try {
            String sql = "SELECT * FROM ARTICOLO WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                articolo = new Articolo();
                articolo.setNumeroSeriale(rs.getString("numeroSeriale"));
                articolo.setNome(rs.getString("nome"));
                articolo.setTipo(Tipo.valueOf(rs.getString("tipo")));
                articolo.setPrezzo(rs.getDouble("prezzo"));
                articolo.setQuantita(rs.getInt("quantita"));
                articolo.setDescrizione(rs.getString("descrizione"));
                articolo.setUrl(rs.getString("url"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'articolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return articolo;
    }

    // Metodo per recuperare tutti gli articoli
    public List<Articolo> getAllArticoli() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Articolo> articoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ARTICOLO";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Articolo articolo = new Articolo();
                articolo.setNumeroSeriale(rs.getString("numeroSeriale"));
                articolo.setNome(rs.getString("nome"));
                articolo.setTipo(Tipo.valueOf(rs.getString("tipo")));
                articolo.setPrezzo(rs.getDouble("prezzo"));
                articolo.setQuantita(rs.getInt("quantita"));
                articolo.setDescrizione(rs.getString("descrizione"));
                articolo.setUrl(rs.getString("url"));
                articoli.add(articolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutti gli articoli", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return articoli;
    }

    // Metodo per aggiornare un articolo esistente
    public void updateArticolo(Articolo articolo) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ARTICOLO SET numeroSeriale=?, nome=?, tipo=?, prezzo=?, quantita=?, descrizione=?, url=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, articolo.getNumeroSeriale());
            ps.setString(2, articolo.getNome());
            ps.setString(3, articolo.getTipo().name());
            ps.setDouble(4, articolo.getPrezzo());
            ps.setInt(5, articolo.getQuantita());
            ps.setString(6, articolo.getDescrizione());
            ps.setString(7, articolo.getUrl());
            ps.setInt(8, articolo.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento dell'articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare un articolo tramite il suo ID
    public void deleteArticolo(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ARTICOLO WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione dell'articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}