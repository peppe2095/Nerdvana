package Model;



public class WishList {
    private int id;
    private int utenteId;

    public WishList() {}

    public WishList( int utenteId) {
      
        this.utenteId = utenteId;
    }

    public int getId() { return id; }
   

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
}
