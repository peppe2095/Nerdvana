package Dto;

import Model.Articolo;
import Model.Enum.Tipo;

public class ArticoloDto {
    public String numeroSeriale;
    public String nome;
    public String tipo;
    public double prezzo;
    public int quantita;
    public String descrizione;
    public String url;

    public static ArticoloDto from(Articolo a) {
        ArticoloDto d = new ArticoloDto();
        d.numeroSeriale = a.getNumeroSeriale();
        d.nome = a.getNome();
        Tipo t = a.getTipo();
        d.tipo = (t != null) ? t.name() : null;
        d.prezzo = a.getPrezzo();
        d.quantita = a.getQuantita();
        d.descrizione = a.getDescrizione();
        d.url = a.getUrl();
        return d;
    }
}