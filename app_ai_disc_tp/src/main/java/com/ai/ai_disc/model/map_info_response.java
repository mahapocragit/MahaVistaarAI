package com.ai.ai_disc.model;

public class map_info_response {

    public report_info_model model;
    private Throwable error;

    public map_info_response(report_info_model model) {
        this.model = model;
        this.error = null;
    }

    public map_info_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public report_info_model getModel() {
        return model;
    }

    public void setModel(report_info_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
