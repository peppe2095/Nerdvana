package Dto;

import Model.Utente;
import Model.CartaDiCredito;
import java.util.List;

public class UtenteConCarteDto {
    private Utente utente;
    private List<CartaDiCredito> carte;

    public UtenteConCarteDto() {}

    public UtenteConCarteDto(Utente utente, List<CartaDiCredito> carte) {
        this.utente = utente;
        this.carte = carte;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public List<CartaDiCredito> getCarte() {
        return carte;
    }

    public void setCarte(List<CartaDiCredito> carte) {
        this.carte = carte;
    }
}