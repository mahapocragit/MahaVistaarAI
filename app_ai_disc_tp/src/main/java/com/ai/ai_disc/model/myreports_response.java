package com.ai.ai_disc.model;

public class myreports_response {

    public myreport_model model;
    private Throwable error;

    public myreports_response(myreport_model model) {
        this.model = model;
        this.error = null;
    }

    public myreports_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public myreport_model getModel() {
        return model;
    }

    public void setModel(myreport_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
