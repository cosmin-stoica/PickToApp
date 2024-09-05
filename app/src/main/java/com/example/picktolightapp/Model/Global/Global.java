package com.example.picktolightapp.Model.Global;

public class Global {

    private String ip;
    private String port;

    public Global(String ip, String port){
        this.ip = ip;
        this.port = port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
