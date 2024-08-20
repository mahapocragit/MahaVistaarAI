package com.ai.ai_disc.model;

public class getIdentifier_crops_response {

    public identifier_model_croplist model;
    private Throwable error;

    public getIdentifier_crops_response(identifier_model_croplist model) {
        this.model = model;
        this.error = null;
    }

    public getIdentifier_crops_response(Throwable error) {
        this.model = null;
        this.error = error;
    }

    public identifier_model_croplist getModel() {
        return model;
    }

    public void setModel(identifier_model_croplist model) {
        this.model = model;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
