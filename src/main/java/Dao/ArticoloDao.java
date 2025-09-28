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


    // Recupero paginato con filtro opzionale per tipo
    public List<Articolo> getArticoliPaged(int page, int size, Tipo tipo) throws SQLException {

        //size = numero di elementi che voglio mostrare all'utente
        //se mi viene passata una size < o = a 0, allora di default sarà 8
        if (size <= 0) size = 8;
        //page è il numero della pagina che vogliamo mostrare
        //se è < = 0 allora di default sarà 1
        if (page <= 0) page = 1;
        //offset = numero di elementi da saltare prima di iniziare a prendere i risultati
        //esempio: immaginiamo che vogliamo mostrare 5 elementi all'utente per ogni pagina
        // allora in pagina 1 -> offset = 0 perché partiamo dal primo elemento,
        // pagina 2 -> offset = 5 (size) perché vogliamo prendere dal 5 elemento in poi
        // pagina 3 -> offset = size * 2, perché vogliamo prendere dal 10 elemento in poi
        int offset = (page - 1) * size;

        // Dichiara un PreparedStatement che verrà usato per eseguire query SQL parametrizzate 
        // Questo oggetto aiuta a prevenire SQL injection e gestire i parametri in modo sicuro
        PreparedStatement ps = null;

        // Dichiara un ResultSet che conterrà i risultati della query
        // ResultSet è come una tabella temporanea che contiene i dati restituiti dal database
        ResultSet rs = null;

        // Crea una nuova ArrayList vuota che conterrà gli oggetti Articolo 
        // recuperati dal database. Questa lista verrà popolata e restituita dal metodo
        List<Articolo> articoli = new ArrayList<>();

        try {
            // Costruisce la query SQL;
            String base = "SELECT * FROM ARTICOLO";

            // Aggiunge una clausola WHERE solo se è specificato un tipo
            // se tipo != null allora il valore della variabile sarà : "WHERE tipo = ?"
            // altrimenti where sarà stringa vuota
            // Il ? serve per metterci il valore del tipo SE ci viene passato
            String where = (tipo != null) ? " WHERE tipo = ?" : "";

            // Aggiunge l'ordinamento per ID e la paginazione
            // LIMIT limita il numero di risultati,
            // OFFSET indica da dove iniziare, quindi se prendiamo dalla seconda pagina, e ogni pagina ha 5 elementi
            // noi prendiamo dal 6 in poi.
            String orderLimit = " ORDER BY id LIMIT ? OFFSET ?";

            // Concatena le parti della query
            String sql = base + where + orderLimit;

            // Prepara lo statement SQL con la query costruita
            ps = connection.prepareStatement(sql);

            // Indice per tenere traccia dei parametri da sostituire ai placeholder ?
            int idx = 1;

            // Se c'è un tipo specificato, imposta il primo parametro con il nome del tipo
            // L'operatore ++ post-incrementa idx dopo averlo usato
            if (tipo != null) {
                ps.setString(idx++, tipo.name());
            }

            // Imposta i parametri per LIMIT e OFFSET della paginazione
            ps.setInt(idx++, size);
            ps.setInt(idx, offset);
        
            // Esegue la query e ottiene i risultati
            rs = ps.executeQuery();
        
            // Itera sui risultati finché ci sono righe da processare
            while (rs.next()) {
                // Per ogni riga, crea un nuovo oggetto Articolo e lo popola con i dati...
                Articolo articolo = new Articolo();
                articolo.setId(rs.getInt("id"));
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
            throw new SQLException("Errore nel recupero paginato degli articoli", e);
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

    // Conta totale articoli con filtro opzionale per tipo
    public int countArticoli(Tipo tipo) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //questa query semplicemente conta tutti gli articoli per il loro tipo
        try {
            String base = "SELECT COUNT(*) FROM ARTICOLO";
            String where = (tipo != null) ? " WHERE tipo = ?" : "";
            String sql = base + where;
            ps = connection.prepareStatement(sql);
            if (tipo != null) {
                ps.setString(1, tipo.name());
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new SQLException("Errore nel conteggio degli articoli", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
                articolo.setId(rs.getInt("id"));
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
                articolo.setId(rs.getInt("id"));
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
    // Metodo per recuperare un articolo tramite il suo numero seriale (chiave "business")
    public Articolo getArticoloByNumeroSeriale(String numeroSeriale) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Articolo articolo = null;
        try {
            String sql = "SELECT * FROM ARTICOLO WHERE numeroSeriale = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, numeroSeriale);
            rs = ps.executeQuery();
            if (rs.next()) {
                articolo = new Articolo();
                articolo.setId(rs.getInt("id"));
                articolo.setNumeroSeriale(rs.getString("numeroSeriale"));
                articolo.setNome(rs.getString("nome"));
                articolo.setTipo(Tipo.valueOf(rs.getString("tipo")));
                articolo.setPrezzo(rs.getDouble("prezzo"));
                articolo.setQuantita(rs.getInt("quantita"));
                articolo.setDescrizione(rs.getString("descrizione"));
                articolo.setUrl(rs.getString("url"));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero dell'articolo per numero seriale", e);
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
}