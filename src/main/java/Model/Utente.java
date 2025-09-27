package Model;

import java.util.Date;

import Model.Enum.Ruolo;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String numeroCivico;
    private String indirizzo;
    private String cap;
    private Date dataNascita;
    private Ruolo ruolo; // cliente, admin
    private String telefono;
    private String passwordHash;
    private String cittaResidenza;

    // Costruttore vuoto
    public Utente() {}

    // Costruttore completo
    public Utente(String nome, String cognome, String email, String numeroCivico,
                  String indirizzo, String cap, Date dataNascita, Ruolo ruolo, String telefono,
                  String passwordHash, String cittaResidenza) {
  
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.numeroCivico = numeroCivico;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.dataNascita = dataNascita;
        this.ruolo = ruolo;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.cittaResidenza = cittaResidenza;
    }

    // Getter e Setter
    public int getId() { return id; }
    // Setter aggiunto per permettere ai DAO di valorizzare l'ID letto dal DB
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumeroCivico() { return numeroCivico; }
    public void setNumeroCivico(String numeroCivico) { this.numeroCivico = numeroCivico; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }

    public Date getDataNascita() { return dataNascita; }
    public void setDataNascita(Date dataNascita) { this.dataNascita = dataNascita; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCittaResidenza() { return cittaResidenza; }
    public void setCittaResidenza(String cittaResidenza) { this.cittaResidenza = cittaResidenza; }
}
