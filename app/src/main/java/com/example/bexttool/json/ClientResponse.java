package com.example.bexttool.json;

public class ClientResponse {
    private String idsessione;
    private long timestamp;
    private String result;

    public ClientResponse() {}

    public String getIdsessione() {
        return idsessione;
    }
    public void setIdsessione(String idsessione) {
        this.idsessione = idsessione;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}
