package Model;
import Model.Enum.Tipo;

public class Articolo {
	private int id;
    private String numeroSeriale;
    private String nome;
    private Tipo tipo;
    private double prezzo;
    private int quantita;
    private String descrizione;
    private String urlImmagine;

    public Articolo() {}

    public Articolo(String numeroSeriale, String nome, Tipo tipo,
                    double prezzo, int quantita, String descrizione, String urlImmagine) {
       
        this.numeroSeriale = numeroSeriale;
        this.nome = nome;
        this.tipo = tipo;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.descrizione = descrizione;
        this.urlImmagine = urlImmagine;
    }


    public int getId() { return id; }

    public String getNumeroSeriale() { return numeroSeriale; }
    public void setNumeroSeriale(String numeroSeriale) { this.numeroSeriale = numeroSeriale; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getUrl() { return urlImmagine; }
    public void setUrl(String url) { this.urlImmagine = url; }
}
