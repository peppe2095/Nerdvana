package Dto;

import java.util.List;

/**
 * Classe generica per la paginazione dei risultati.
 * Il parametro T rappresenta il tipo di dato contenuto nella lista paginata.
 * Viene utilizzata nella servlet getArticoli per restituire una porzione della lista totale
 * degli articoli insieme ai dati
 */
public class PagedResponse<T> {
    public List<T> items;        // Lista degli elementi nella pagina corrente
    public int page;            // Numero della pagina corrente
    public int size;            // Numero di elementi per pagina
    public int total;           // Numero totale di elementi
    public int totalPages;      // Numero totale di pagine

    public PagedResponse() {
    }

    public PagedResponse(List<T> items, int page, int size, int total, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
    }
}