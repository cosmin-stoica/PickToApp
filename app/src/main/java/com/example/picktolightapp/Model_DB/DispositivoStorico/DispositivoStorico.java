package com.example.picktolightapp.Model_DB.DispositivoStorico;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;

import java.sql.Timestamp;

public class DispositivoStorico {

    private int iIdentifierDipositivo;
    private String sOperation;
    private String sTimestamp;

    public DispositivoStorico(int iIdentifierDipositivo, String sOperation, String sTimestamp){
        this.iIdentifierDipositivo = iIdentifierDipositivo;
        this.sOperation = sOperation;
        this.sTimestamp = sTimestamp;
    }

    public DispositivoStorico(int iIdentifierDipositivo, String sOperation){
        this.iIdentifierDipositivo = iIdentifierDipositivo;
        this.sOperation = sOperation;
    }


    public String getOperation() {
        return sOperation;
    }
    public String getTimestamp() {
        return sTimestamp;
    }

    public int getIdentifierDipositivo() {
        return iIdentifierDipositivo;
    }

    public void setIdentifierDipositivo(int iIdentifierDipositivo) {
        this.iIdentifierDipositivo = iIdentifierDipositivo;
    }

    public void setOperation(String sOperation) {
        this.sOperation = sOperation;
    }
    public void setTimestamp(String sTimestamp) {
        this.sTimestamp = sTimestamp;
    }
}
