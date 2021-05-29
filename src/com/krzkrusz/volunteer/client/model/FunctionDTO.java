package com.krzkrusz.volunteer.client.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class FunctionDTO implements Serializable {

    private long id;
    private String function;
    private String functionName;
    private Object[] args;

    public FunctionDTO() {
    }

    public FunctionDTO(long id, String function, String functionName, Object[] args) {
        this.id = id;
        this.function = function;
        this.functionName = functionName;
        this.args = args;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
