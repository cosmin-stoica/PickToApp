package com.example.picktolightapp.Model_DB.Dispositivo;

import com.example.picktolightapp.Model_DB.Componente.Componente;

public class Dispositivo {

    private int id;
    private int qty;
    private int qty_min;
    private boolean rilevato;
    private Componente componente;

    public Dispositivo(int id, int qty, int qty_min){
        this.id = id;
        this.qty = qty;
        this.qty_min = qty_min;
        rilevato = false;
        componente = new Componente(-1,"","","");
    }

    public int getId() {
        return id;
    }

    public int getQty() {
        return qty;
    }

    public int getQty_min() {
        return qty_min;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setQty_min(int qty_min) {
        this.qty_min = qty_min;
    }

    public boolean isRilevato() {
        return rilevato;
    }

    public void setRilevato(boolean rilevato) {
        this.rilevato = rilevato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dispositivo that = (Dispositivo) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public Componente getComponente() {
        return componente;
    }

    public void setComponente(Componente componente) {
        this.componente = componente;
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "id=" + id +
                ", qty=" + qty +
                ", qty_min=" + qty_min +
                ", rilevato=" + rilevato +
                ", componente=" + componente +
                '}';
    }
}
