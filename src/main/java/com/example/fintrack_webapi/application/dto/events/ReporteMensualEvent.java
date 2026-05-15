package com.example.fintrack_webapi.application.dto.events;

import java.io.Serializable;

public class ReporteMensualEvent implements Serializable {

    private int mes;

    private String requestId;

    public ReporteMensualEvent() {
    }

    public ReporteMensualEvent(int mes, String requestId) {
        this.mes = mes;
        this.requestId = requestId;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
