package Dao;

import Model.WishList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishListDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public WishListDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare una nuova wishlist nel database
    public void addWishList(WishList wishList) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO WISHLIST (utenteId) VALUES (?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishList.getUtenteId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio della wishlist", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare una wishlist tramite il suo ID
    public WishList getWishListById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        WishList wishList = null;

        try {
            String sql = "SELECT * FROM WISHLIST WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                wishList = new WishList();
                wishList.setUtenteId(rs.getInt("utenteId"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero della wishlist", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishList;
    }

    // Metodo per recuperare una wishlist tramite l'ID dell'utente
    public WishList getWishListByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        WishList wishList = null;

        try {
            String sql = "SELECT * FROM WISHLIST WHERE utenteId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            rs = ps.executeQuery();

            if (rs.next()) {
                wishList = new WishList();
                wishList.setUtenteId(rs.getInt("utenteId"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero della wishlist per utente", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishList;
    }

    // Metodo per verificare se un utente ha già una wishlist
    public boolean existsWishListForUtente(int utenteId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            String sql = "SELECT COUNT(*) FROM WISHLIST WHERE utenteId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            rs = ps.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nella verifica dell'esistenza della wishlist per l'utente", e);
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

    // Metodo per recuperare tutte le wishlist
    public List<WishList> getAllWishLists() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<WishList> wishLists = new ArrayList<>();

        try {
            String sql = "SELECT * FROM WISHLIST";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                WishList wishList = new WishList();
                wishList.setUtenteId(rs.getInt("utenteId"));
                wishLists.add(wishList);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutte le wishlist", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishLists;
    }

    // Metodo per aggiornare una wishlist esistente
    public void updateWishList(WishList wishList) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE WISHLIST SET utenteId=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishList.getUtenteId());
            ps.setInt(2, wishList.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento della wishlist", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare una wishlist tramite il suo ID
    public void deleteWishList(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione della wishlist", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare una wishlist tramite l'ID dell'utente
    public void deleteWishListByUtenteId(int utenteId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST WHERE utenteId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, utenteId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione della wishlist per utente", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per ottenere o creare una wishlist per un utente
    public WishList getOrCreateWishListForUtente(int utenteId) throws SQLException {
        WishList wishList = getWishListByUtenteId(utenteId);
        
        if (wishList == null) {
            // Se l'utente non ha una wishlist, ne creiamo una nuova
            wishList = new WishList(utenteId);
            addWishList(wishList);
            // Recuperiamo la wishlist appena creata per ottenere l'ID generato
            wishList = getWishListByUtenteId(utenteId);
        }
        
        return wishList;
    }
}