package com.krzkrusz.volunteer.client.model;

import java.io.Serializable;

public class KernelDTO implements Serializable {
    private long id;
    private String kernel;
    private Object[] args;

    public KernelDTO() {
    }

    public KernelDTO(long id, String kernel, Object[] args) {
        this.id = id;
        this.kernel = kernel;
        this.args = args;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
