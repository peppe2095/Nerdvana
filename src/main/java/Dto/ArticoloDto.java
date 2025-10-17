package Dto;

import Model.Articolo;
import Model.Enum.Tipo;

/**
 * DTO (Data Transfer Object) per la classe Articolo.
 * I DTO sono oggetti utilizzati per trasferire dati backend e frontend
 *  Li usiamo perché offrono:
 * - Separazione delle responsabilità tra modello di dominio (model) e rappresentazione dei dati (controller)
 * - Controllo preciso dei dati che mostriamo lato frontend
 */
public class ArticoloDto {
    // Campi pubblici per facilitare la serializzazione JSON
    public int id;
    public String numeroSeriale;
    public String nome;
    public String tipo;
    public double prezzo;
    public int quantita;
    public String descrizione;
    public String url;

    /**
     * Metodo statico che converte un oggetto Articolo in ArticoloDto.
     *  viene quasi sempre usato nei DTO che permette di:
     * - Mappare in modo pulito il modello (Articolo) e il DTO
     * - Gestire le conversioni di tipo necessarie (es. Tipo enum -> String)
     */
    public static ArticoloDto from(Articolo a) {
        ArticoloDto d = new ArticoloDto();
        d.id = a.getId();
        d.numeroSeriale = a.getNumeroSeriale();
        d.nome = a.getNome();
        Tipo t = a.getTipo();
        // Conversione sicura dell'enum Tipo in String
        d.tipo = (t != null) ? t.name() : null;
        d.prezzo = a.getPrezzo();
        d.quantita = a.getQuantita();
        d.descrizione = a.getDescrizione();
        d.url = a.getUrl();
        return d;
    }
}