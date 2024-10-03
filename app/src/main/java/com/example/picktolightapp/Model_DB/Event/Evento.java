package com.example.picktolightapp.Model_DB.Event;

import java.sql.Timestamp;

public class Evento {

    private TipoEvento tipo;
    private Timestamp timestamp;
    private String descrizione;

    public Evento(TipoEvento tipo, Timestamp timestamp, String descrizione) {
        this.tipo = tipo;
        this.timestamp = timestamp;
        this.descrizione = descrizione;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public int getTipoCodice() {
        return tipo.getCodice();
    }

    public void setTipoByCodice(int codice) {
        this.tipo = TipoEvento.fromCodice(codice);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "tipo=" + tipo +
                ", timestamp=" + timestamp +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }
}
