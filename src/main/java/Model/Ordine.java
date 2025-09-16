package Model;

import java.util.Date;
import Model.Enum.Stato;

public class Ordine {
    private int id;
    private int utenteId;
    private Integer cartaCreditoId; // può essere null
    private int numeroArticoli;
    private double importo;
    private Date dataSpedizione;
    private Date dataArrivo;
    private Stato stato; // in_attesa, confermato, spedito, consegnato, annullato
    private Date dataCreazione;
    private Date dataAggiornamento;

    public Ordine() {}

    public Ordine( int utenteId, Integer cartaCreditoId, int numeroArticoli,
                  double importo, Date dataSpedizione, Date dataArrivo,
                  Stato stato, Date dataCreazione, Date dataAggiornamento) {
       
        this.utenteId = utenteId;
        this.cartaCreditoId = cartaCreditoId;
        this.numeroArticoli = numeroArticoli;
        this.importo = importo;
        this.dataSpedizione = dataSpedizione;
        this.dataArrivo = dataArrivo;
        this.stato = stato;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    public int getId() { return id; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    public Integer getCartaCreditoId() { return cartaCreditoId; }
    public void setCartaCreditoId(Integer cartaCreditoId) { this.cartaCreditoId = cartaCreditoId; }

    public int getNumeroArticoli() { return numeroArticoli; }
    public void setNumeroArticoli(int numeroArticoli) { this.numeroArticoli = numeroArticoli; }

    public double getImporto() { return importo; }
    public void setImporto(double importo) { this.importo = importo; }

    public Date getDataSpedizione() { return dataSpedizione; }
    public void setDataSpedizione(Date dataSpedizione) { this.dataSpedizione = dataSpedizione; }

    public Date getDataArrivo() { return dataArrivo; }
    public void setDataArrivo(Date dataArrivo) { this.dataArrivo = dataArrivo; }

    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }

    public Date getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(Date dataCreazione) { this.dataCreazione = dataCreazione; }

    public Date getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(Date dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }
}
