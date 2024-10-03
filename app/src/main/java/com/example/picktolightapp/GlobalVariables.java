package com.example.picktolightapp;

import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.User.User;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {

    private static GlobalVariables instance;

    private int lastDestinationId;
    private String[] lastUserManagementOptions;
    private String lastOptionGroupName;
    private User userToSee;

    private List<Dispositivo> currentDispositivi = new ArrayList<>();

    private String IP = "";
    private String Port = "";

    private Dispositivo dispositivoToSee;
    private Componente componenteToSee;

    private boolean dispositivoNeedToCreate = false;
    private boolean componenteNeedToCreate = false;

    private boolean bSeeDispositivoLog = false;
    private int dispositivoIDToSeeLog = -1;

    private GlobalVariables() {
    }

    public static synchronized GlobalVariables getInstance() {
        if (instance == null) {
            instance = new GlobalVariables();
        }
        return instance;
    }


    public int getLastDestinationId() {
        return lastDestinationId;
    }

    public void setLastDestinationId(int destinationId) {
        this.lastDestinationId = destinationId;
    }

    public String[] getLastUserManagementOptions() {
        return lastUserManagementOptions;
    }

    public void setLastUserManagementOptions(String[] lastUserManagementOptions) {
        this.lastUserManagementOptions = lastUserManagementOptions;
    }

    public String getLastOptionGroupName() {
        return lastOptionGroupName;
    }

    public void setLastOptionGroupName(String lastOptionGroupName) {
        this.lastOptionGroupName = lastOptionGroupName;
    }

    public User getUserToSee() {
        return userToSee;
    }

    public void setUserToSee(User userToSee) {
        this.userToSee = userToSee;
    }

    public String getIP() {
        return IP;
    }

    public String getPort() {
        return Port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(String port) {
        Port = port;
    }

    public List<Dispositivo> getCurrentDispositivi() {
        return currentDispositivi;
    }

    public void setCurrentDispositivi(List<Dispositivo> currentDispositivi) {
        this.currentDispositivi = currentDispositivi;
    }

    public void setDispositivoToSee(Dispositivo dispositivoToSee) {
        this.dispositivoToSee = dispositivoToSee;
    }

    public Dispositivo getDispositivoToSee() {
        return dispositivoToSee;
    }

    public boolean isDispositivoNeedToCreate() {
        return dispositivoNeedToCreate;
    }

    public void setDispositivoNeedToCreate(boolean dispositivoNeedToCreate) {
        this.dispositivoNeedToCreate = dispositivoNeedToCreate;
    }

    public boolean isComponenteNeedToCreate() {
        return componenteNeedToCreate;
    }

    public void setComponenteNeedToCreate(boolean componenteNeedToCreate) {
        this.componenteNeedToCreate = componenteNeedToCreate;
    }

    public Componente getComponenteToSee() {
        return componenteToSee;
    }

    public void setComponenteToSee(Componente componenteToSee) {
        this.componenteToSee = componenteToSee;
    }

    public boolean isSeeDispositivoLog() {
        return bSeeDispositivoLog;
    }

    public void setbSeeDispositivoLog(boolean bSeeDispositivoLog) {
        this.bSeeDispositivoLog = bSeeDispositivoLog;
    }

    public int getDispositivoIDToSeeLog() {
        return dispositivoIDToSeeLog;
    }

    public void setDispositivoIDToSeeLog(int dispositivoIDToSeeLog) {
        this.dispositivoIDToSeeLog = dispositivoIDToSeeLog;
    }
}
