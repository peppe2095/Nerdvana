package Model;

public class WishListArticolo {
    private int id;
    private int wishlistId;
    private int articoloId;

    public WishListArticolo() {}

    public WishListArticolo( int wishlistId, int articoloId) {
        
        this.wishlistId = wishlistId;
        this.articoloId = articoloId;
    }

    public int getId() { return id; }
    

    public int getWishlistId() { return wishlistId; }
    public void setWishlistId(int wishlistId) { this.wishlistId = wishlistId; }

    public int getArticoloId() { return articoloId; }
    public void setArticoloId(int articoloId) { this.articoloId = articoloId; }
}
