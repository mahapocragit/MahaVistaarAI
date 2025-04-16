package in.gov.mahapocra.farmerapp.data.api;

public enum AppEnv {

    DBT {
        public String instance() {
            return "https://dbt-api.mahapocra.gov.in/SharedAPI/";
        }
    },

    FARMER {
        public String instance() {
            return "https://farmers-app-api.mahapocra.gov.in/";
        }
    },

    UAT_DBT {
        public String instance() {
            return "https://dbt-api.mahapocra.gov.in/SharedAPI/";

        }
    },

    WOTR {
        public String instance() {
            return "https://kisan.wotr.org.in/";

        }
    },

    GIS {
        public String instance() {
            return "https://gis.mahapocra.gov.in/";

        }
    },

    SSO {
        public String instance() {

            return "https://sso.mahapocra.gov.in/";  // getting by Mayuri SK 22-11-21 for staging
        }
    },

    UAT_SSO {
        public String instance() {
            return "https://ilab-sso.mahapocra.gov.in/";
           // return "http://sso.mahapocra.gov.in/";
        }
    },

    TMS{
        public String instance() {
            return "http://api-buildup.mahapocra.gov.in/v10/";
        }
    },

    UAT_TMS {
        public String instance() {
            return "https://ilab-training-api.mahapocra.gov.in/v10/";
           // return "http://api-buildup.mahapocra.gov.in/v10/";
        }
    };

    public abstract String instance();

}
