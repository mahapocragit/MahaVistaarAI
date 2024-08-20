package com.ai.ai_disc.model;

public class reportmap_latlong_response {

    public reportmap_latlong_model model;
    private Throwable error;

    public reportmap_latlong_response(reportmap_latlong_model model) {
        this.model = model;
        this.error = null;
    }

    public reportmap_latlong_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public reportmap_latlong_model getModel() {
        return model;
    }

    public void setModel(reportmap_latlong_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
