package com.example.picktolightapp.Model.Operation;

public enum Operation {
    MODIFY_OWN_USERNAME(0, "Modifica il proprio username"),
    MODIFY_OWN_PASSWORD(1, "Modifica la propria password"),
    MODIFY_OTHERS_TYPE(2, "Modifica tipo altrui"),
    VIEW_LOG(3, "Visualizza log"),
    DELETE_LOG(4, "Elimina log"),
    VIEW_USERS(5, "Visualizza utenti"),
    MODIFY_USERS(6,"Modifica utenti"),
    DELETE_USERS(7, "Elimina utenti"),
    CREATE_USER(8, "Crea utente"),
    TEST_TCPIP(9, "Test TCPIP"),
    CONF_NET(10, "Configurazione Rete");


    private final int id;
    private final String name;

    Operation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static Operation getById(int id) {
        for (Operation operation : values()) {
            if (operation.getId() == id) {
                return operation;
            }
        }
        throw new IllegalArgumentException("No operation with id " + id);
    }
}
