package Model;



public class WishList {
    private int id;
    private int utenteId;

    public WishList() {}

    public WishList(int utenteId) {
        this.utenteId = utenteId;
    }

    public int getId() { return id; }
    // Setter aggiunto per permettere ai DAO di valorizzare l'ID letto dal DB
    public void setId(int id) { this.id = id; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
}
