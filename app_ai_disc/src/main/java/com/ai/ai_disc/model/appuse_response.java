package com.ai.ai_disc.model;

public class appuse_response {

    public appuusemodel model;
    private Throwable error;

    public appuse_response(appuusemodel model) {
        this.model = model;
        this.error = null;
    }

    public appuse_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public appuusemodel getModel() {
        return model;
    }

    public void setModel(appuusemodel model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
