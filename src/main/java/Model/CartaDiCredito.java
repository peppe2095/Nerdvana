package Model;



import java.util.Date;

public class CartaDiCredito {
    private int id;
    private int utenteId;
    private String nomeTitolare;
    private String numeroCarta;
    private String cvv;
    private Date scadenza;

    public CartaDiCredito() {}

    public CartaDiCredito( int utenteId, String nomeTitolare, String numeroCarta, String cvv, Date scadenza) {
        
        this.utenteId = utenteId;
        this.nomeTitolare = nomeTitolare;
        this.numeroCarta = numeroCarta;
        this.cvv = cvv;
        this.scadenza = scadenza;
    }

    public int getId() { return id; }
    

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    public String getNomeTitolare() { return nomeTitolare; }
    public void setNomeTitolare(String nomeTitolare) { this.nomeTitolare = nomeTitolare; }

    public String getNumeroCarta() { return numeroCarta; }
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public Date getScadenza() { return scadenza; }
    public void setScadenza(Date scadenza) { this.scadenza = scadenza; }
}
