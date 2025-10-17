package Model;

public class Fattura {
    private int id;
    private int ordineId;
    private String urlFattura;

    public Fattura() {}

    public Fattura( int ordineId, String urlFattura) {
        this.ordineId = ordineId;
        this.urlFattura = urlFattura;
    }

    public int getId() { return id; }

    public int getOrdineId() { return ordineId; }
    public void setOrdineId(int ordineId) { this.ordineId = ordineId; }

    public String getUrlFattura() { return urlFattura; }
    public void setUrlFattura(String urlFattura) { this.urlFattura = urlFattura; }

}

