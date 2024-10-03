package com.example.picktolightapp.Model_DB.Operation;

public enum Operation {
    MODIFY_OWN_USERNAME(0, "Modifica il proprio username"),
    MODIFY_OWN_PASSWORD(1, "Modifica la propria password"),
    MODIFY_OTHERS_TYPE(2, "Modifica tipo altrui"),
    VIEW_GENERAL_LOG(3, "Visualizza log generale"),
    DELETE_LOG(4, "Elimina log"),
    VIEW_USERS(5, "Visualizza utenti"),
    MODIFY_USERS(6,"Modifica utenti"),
    DELETE_USERS(7, "Elimina utenti"),
    CREATE_USER(8, "Crea utente"),
    TEST_TCPIP(9, "Test TCPIP"),
    CONF_NET(10, "Configurazione rete"),
    VIEW_DISP(11, "Visualizza dispositivi"),
    VIEW_COMP(12, "Visualizza componenti"),
    VIEW_LIVE(13, "Visualizza live"),
    MODIFY_COMPONENT(14, "Modifica componente"),
    DELETE_COMPONENT(15, "Elimina componente"),
    CREATE_COMPONENT(16, "Crea componente"),
    DETECT_DISP(17, "Rileva dispositivi"),
    DELETE_DISP(18, "Elimina dispositivo"),
    CREATE_DISP(19, "Modifica dispositivo"),
    MODIFY_DISP(20, "Crea dispositivo"),
    ADD_DISP_QTY(21, "Aggiungi quantità dispositivo"),
    REMOVE_DISP_QTY(22, "Rimuovi quantità dispositivo"),
    SEE_DISP_HISTORY(23, "Vedi storico dispositivo"),
    DELETE_DISP_HISTORY(24, "Elimina storico dispositivo"),
    START_WORK(25, "Avvia ciclo"),
    HANDLE_



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
