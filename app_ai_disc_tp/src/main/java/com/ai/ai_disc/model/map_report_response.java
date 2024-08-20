package com.ai.ai_disc.model;

public class map_report_response {

    public map_info_model model;
    private Throwable error;

    public map_report_response(map_info_model model) {
        this.model = model;
        this.error = null;
    }

    public map_report_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public map_info_model getModel() {
        return model;
    }

    public void setModel(map_info_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
