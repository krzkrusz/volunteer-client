package com.krzkrusz.volunteer.client.model;

public class Result {
    private Object result;
    private long timeInMillis;

    public Result(Object result, long timeInMillis) {
        this.result = result;
        this.timeInMillis = timeInMillis;
    }

    public Object getResult() {
        return result;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }
}
