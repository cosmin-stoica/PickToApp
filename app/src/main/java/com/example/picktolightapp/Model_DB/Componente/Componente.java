package com.example.picktolightapp.Model_DB.Componente;

public class Componente {

    private int id;
    private String img;
    private String name;
    private String barcode;

    public Componente(int id, String img, String name, String barcode){
        this.id = id;
        this.img = img;
        this.name = name;
        this.barcode = barcode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public String getName() {
       if(this == null)
           return "none";
       else
           return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Componente{" +
                "id=" + id +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
