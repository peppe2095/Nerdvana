package Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class Order {

    private int id;
    private String usernameUtente;
    private int numeroArticoli;
    private BigDecimal importo;
    private Date dataSpedizione;
    private Date dataArrivo;
    private int numeroFattura;
    private List<OrderItem> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsernameUtente() {
        return usernameUtente;
    }

    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }

    public int getNumeroArticoli() {
        return numeroArticoli;
    }

    public void setNumeroArticoli(int numeroArticoli) {
        this.numeroArticoli = numeroArticoli;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public Date getDataSpedizione() {
        return dataSpedizione;
    }

    public void setDataSpedizione(Date dataSpedizione) {
        this.dataSpedizione = dataSpedizione;
    }

    public Date getDataArrivo() {
        return dataArrivo;
    }

    public void setDataArrivo(Date dataArrivo) {
        this.dataArrivo = dataArrivo;
    }

    public int getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(int numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public static class OrderItem {
        private String numeroSerialeArticolo;
        private String nomeArticolo;
        private int quantità;
        private BigDecimal prezzoUnitario;

        public String getNumeroSerialeArticolo() {
            return numeroSerialeArticolo;
        }

        public void setNumeroSerialeArticolo(String numeroSerialeArticolo) {
            this.numeroSerialeArticolo = numeroSerialeArticolo;
        }

        public String getNomeArticolo() {
            return nomeArticolo;
        }

        public void setNomeArticolo(String nomeArticolo) {
            this.nomeArticolo = nomeArticolo;
        }

        public int getQuantità() {
            return quantità;
        }

        public void setQuantità(int quantità) {
            this.quantità = quantità;
        }

        public BigDecimal getPrezzoUnitario() {
            return prezzoUnitario;
        }

        public void setPrezzoUnitario(BigDecimal prezzoUnitario) {
            this.prezzoUnitario = prezzoUnitario;
        }
    }
}
