package Model;

public class OrdineArticolo {
    private int id;
    private int ordineId;
    private int articoloId;
    private int quantita;

    public OrdineArticolo() {}

    public OrdineArticolo( int ordineId, int articoloId, int quantita) {
        
        this.ordineId = ordineId;
        this.articoloId = articoloId;
        this.quantita = quantita;
    }

    public int getId() { return id; }
   

    public int getOrdineId() { return ordineId; }
    public void setOrdineId(int ordineId) { this.ordineId = ordineId; }

    public int getArticoloId() { return articoloId; }
    public void setArticoloId(int articoloId) { this.articoloId = articoloId; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
}
