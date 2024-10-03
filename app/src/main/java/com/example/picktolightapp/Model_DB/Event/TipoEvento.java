package com.example.picktolightapp.Model_DB.Event;

public enum TipoEvento {
    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3),
    SERVER(4);

    private final int codice;

    TipoEvento(int codice) {
        this.codice = codice;
    }

    public int getCodice() {
        return codice;
    }

    public static TipoEvento fromCodice(int codice) {
        for (TipoEvento tipo : TipoEvento.values()) {
            if (tipo.getCodice() == codice) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoEvento non valido: " + codice);
    }
}
