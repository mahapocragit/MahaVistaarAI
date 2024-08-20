package com.ai.ai_disc.model;

public class create_report_response {

    public createreport_model model;
    private Throwable error;

    public create_report_response(createreport_model model) {
        this.model = model;
        this.error = null;
    }

    public create_report_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public createreport_model getModel() {
        return model;
    }

    public void setModel(createreport_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
