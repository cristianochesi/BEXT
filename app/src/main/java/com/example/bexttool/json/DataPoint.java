package com.example.bexttool.json;

public class DataPoint {
    private long timestamp;
    private int value;

    public DataPoint(long timestamp, int value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
