package com.krzkrusz.volunteer.client.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class ComputationResult implements Serializable {

    private long id;
    private String result;

    public ComputationResult() {
    }

    public ComputationResult(long id, String result) {
        this.id = id;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
