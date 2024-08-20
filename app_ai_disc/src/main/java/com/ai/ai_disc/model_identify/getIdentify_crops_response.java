package com.ai.ai_disc.model_identify;

import com.ai.ai_disc.model.identifier_model_croplist;

public class getIdentify_crops_response {

        public identify_model_croplist model;
        private Throwable error;

        public getIdentify_crops_response(identify_model_croplist model) {
            this.model = model;
            this.error = null;
        }

        public getIdentify_crops_response(Throwable error) {
            this.model = null;
            this.error = error;
        }

        public identify_model_croplist getModel() {
            return model;
        }

        public void setModel(identify_model_croplist model) {
            this.model = model;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }
}