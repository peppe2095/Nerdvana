package Model;

import java.io.Serializable;

public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String NumeroSeriale;
    private String nome;
    private String tipo;
    private double prezzo;
    private int quantita;
    private String descrizione;
    private byte[] immagine;
    private int selectedQuantity;

    public ProductBean() {
        NumeroSeriale = "";
        nome = "";
        tipo = "";
        prezzo = 0.0;
        quantita = 0;
        descrizione = "";
        immagine = null;
        selectedQuantity = 0;
    }

    public ProductBean(String NumeroSeriale, String nome, String tipo, double prezzo, int quantita, String descrizione, byte[] immagine) {
        this.NumeroSeriale = NumeroSeriale;
        this.nome = nome;
        this.tipo = tipo;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.selectedQuantity = 0;
    }

    public String getNumeroSeriale() {
        return NumeroSeriale;
    }

    public void setNumeroSeriale(String numeroSeriale) {
        this.NumeroSeriale = numeroSeriale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public byte[] getImmagine() {
        return immagine;
    }

    public void setImmagine(byte[] immagine) {
        this.immagine = immagine;
    }

    public byte[] getImageBytes() {
        return getImmagine();
    }

    public void setImageBytes(byte[] immagine) {
        setImmagine(immagine);
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ProductBean other = (ProductBean) obj;
        return NumeroSeriale.equals(other.NumeroSeriale);
    }

    @Override
    public int hashCode() {
        return NumeroSeriale.hashCode();
    }

    @Override
    public String toString() {
        return tipo + " (" + NumeroSeriale + "), " + prezzo + ", " + quantita + ". " + descrizione;
    }
}
