package com.example.picktolightapp.Model.Dispositivo;

public class Dispositivo {

    private int id;
    private int qty;
    private int qty_min;

    public Dispositivo(int id, int qty, int qty_min){
        this.id = id;
        this.qty = qty;
        this.qty_min = qty_min;
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
}
