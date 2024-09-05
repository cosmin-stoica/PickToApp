package com.example.picktolightapp.Model.User;

public enum TipoUser {
    GUEST (0),
    OPERATORE (1),
    COORD (2),
    ADMIN (3),
    ROOT ( 4);

    private final int codice;

    TipoUser(int codice) {
        this.codice = codice;
    }

    public int getCodice() {
        return codice;
    }

    public String getCodiceString(){
        switch (codice){
            case 0:
                return "Guest";
            case 1:
                return "Operatore";
            case 2:
                return "Coord";
            case 3:
                return "Admin";
            case 4:
                return "Root";
        }
        return "undenified";
    }

    public static TipoUser createTipoByString(String tipo) {
        switch (tipo) {
            case "Guest":
                return TipoUser.GUEST;
            case "Operatore":
                return TipoUser.OPERATORE;
            case "Coord":
                return TipoUser.COORD;
            case "Admin":
                return TipoUser.ADMIN;
            case "Root":
                return TipoUser.ROOT;
            default:
                throw new IllegalArgumentException("Unknown TipoUser: " + tipo);
        }
    }

    public static com.example.picktolightapp.Model.User.TipoUser fromCodice(int codice) {
        for (com.example.picktolightapp.Model.User.TipoUser tipo : com.example.picktolightapp.Model.User.TipoUser.values()) {
            if (tipo.getCodice() == codice) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoEvento non valido: " + codice);
    }
}
