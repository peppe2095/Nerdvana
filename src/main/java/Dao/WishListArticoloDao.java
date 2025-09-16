package Dao;

import Model.WishListArticolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishListArticoloDao {

    // La connessione non è più statica, ma viene iniettata nel costruttore
    private Connection connection;

    // Nuovo costruttore che accetta una connessione
    public WishListArticoloDao(Connection connection) {
        this.connection = connection;
    }

    // Metodo per salvare un nuovo wishlist-articolo nel database
    public void addWishListArticolo(WishListArticolo wishListArticolo) throws SQLException {
        //PreparedStatement serve per convertire la query in un formato comprensibile per il database, viene sempre messa a inizio metodo nei dao
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO WISHLIST_ARTICOLO (wishlistId, articoloId) VALUES (?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishListArticolo.getWishlistId());
            ps.setInt(2, wishListArticolo.getArticoloId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nel salvataggio del wishlist-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per recuperare un wishlist-articolo tramite il suo ID
    public WishListArticolo getWishListArticoloById(int id) throws SQLException {
        PreparedStatement ps = null;
        //ResultSet è una struttura dati che contiene il risultato di una query SQL eseguita su un database
        ResultSet rs = null;
        WishListArticolo wishListArticolo = null;

        try {
            String sql = "SELECT * FROM WISHLIST_ARTICOLO WHERE id = ?";
            ps = connection.prepareStatement(sql);
            //setto il primo parametro della query (il ?) con il valore di id
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                wishListArticolo = new WishListArticolo();
                wishListArticolo.setWishlistId(rs.getInt("wishlistId"));
                wishListArticolo.setArticoloId(rs.getInt("articoloId"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero del wishlist-articolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishListArticolo;
    }

    // Metodo per recuperare tutti gli articoli di una wishlist specifica
    public List<WishListArticolo> getWishListArticoliByWishlistId(int wishlistId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<WishListArticolo> wishListArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM WISHLIST_ARTICOLO WHERE wishlistId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishlistId);
            rs = ps.executeQuery();

            while (rs.next()) {
                WishListArticolo wishListArticolo = new WishListArticolo();
                wishListArticolo.setWishlistId(rs.getInt("wishlistId"));
                wishListArticolo.setArticoloId(rs.getInt("articoloId"));
                wishListArticoli.add(wishListArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero degli articoli della wishlist", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishListArticoli;
    }

    // Metodo per recuperare tutte le wishlist che contengono un articolo specifico
    public List<WishListArticolo> getWishListArticoliByArticoloId(int articoloId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<WishListArticolo> wishListArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM WISHLIST_ARTICOLO WHERE articoloId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, articoloId);
            rs = ps.executeQuery();

            while (rs.next()) {
                WishListArticolo wishListArticolo = new WishListArticolo();
                wishListArticolo.setWishlistId(rs.getInt("wishlistId"));
                wishListArticolo.setArticoloId(rs.getInt("articoloId"));
                wishListArticoli.add(wishListArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero delle wishlist per l'articolo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishListArticoli;
    }

    // Metodo per recuperare un wishlist-articolo specifico tramite wishlistId e articoloId
    public WishListArticolo getWishListArticoloByWishlistIdAndArticoloId(int wishlistId, int articoloId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        WishListArticolo wishListArticolo = null;

        try {
            String sql = "SELECT * FROM WISHLIST_ARTICOLO WHERE wishlistId = ? AND articoloId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishlistId);
            ps.setInt(2, articoloId);
            rs = ps.executeQuery();

            if (rs.next()) {
                wishListArticolo = new WishListArticolo();
                wishListArticolo.setWishlistId(rs.getInt("wishlistId"));
                wishListArticolo.setArticoloId(rs.getInt("articoloId"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero del wishlist-articolo specifico", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishListArticolo;
    }

    // Metodo per verificare se un articolo è già presente in una wishlist
    public boolean existsWishListArticolo(int wishlistId, int articoloId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            String sql = "SELECT COUNT(*) FROM WISHLIST_ARTICOLO WHERE wishlistId = ? AND articoloId = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishlistId);
            ps.setInt(2, articoloId);
            rs = ps.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nella verifica dell'esistenza del wishlist-articolo", e);
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

    // Metodo per recuperare tutti i wishlist-articoli
    public List<WishListArticolo> getAllWishListArticoli() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<WishListArticolo> wishListArticoli = new ArrayList<>();

        try {
            String sql = "SELECT * FROM WISHLIST_ARTICOLO";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                WishListArticolo wishListArticolo = new WishListArticolo();
                wishListArticolo.setWishlistId(rs.getInt("wishlistId"));
                wishListArticolo.setArticoloId(rs.getInt("articoloId"));
                wishListArticoli.add(wishListArticolo);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero di tutti i wishlist-articoli", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wishListArticoli;
    }

    // Metodo per aggiornare un wishlist-articolo esistente
    public void updateWishListArticolo(WishListArticolo wishListArticolo) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE WISHLIST_ARTICOLO SET wishlistId=?, articoloId=? WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishListArticolo.getWishlistId());
            ps.setInt(2, wishListArticolo.getArticoloId());
            ps.setInt(3, wishListArticolo.getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nell'aggiornamento del wishlist-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare un wishlist-articolo tramite il suo ID
    public void deleteWishListArticolo(int id) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST_ARTICOLO WHERE id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione del wishlist-articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per rimuovere un articolo specifico da una wishlist
    public void deleteWishListArticoloByWishlistIdAndArticoloId(int wishlistId, int articoloId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST_ARTICOLO WHERE wishlistId=? AND articoloId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishlistId);
            ps.setInt(2, articoloId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella rimozione dell'articolo dalla wishlist", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutti gli articoli di una wishlist specifica
    public void deleteWishListArticoliByWishlistId(int wishlistId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST_ARTICOLO WHERE wishlistId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, wishlistId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione degli articoli della wishlist", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per cancellare tutte le wishlist che contengono un articolo specifico
    public void deleteWishListArticoliByArticoloId(int articoloId) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM WISHLIST_ARTICOLO WHERE articoloId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, articoloId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Errore nella cancellazione delle wishlist per l'articolo", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}