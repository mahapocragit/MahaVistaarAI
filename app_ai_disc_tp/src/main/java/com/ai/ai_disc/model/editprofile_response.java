package com.ai.ai_disc.model;

public class editprofile_response {

    public editprofile_model model;
    private Throwable error;

    public editprofile_response(editprofile_model model) {
        this.model = model;
        this.error = null;
    }

    public editprofile_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public editprofile_model getModel() {
        return model;
    }

    public void setModel(editprofile_model model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
