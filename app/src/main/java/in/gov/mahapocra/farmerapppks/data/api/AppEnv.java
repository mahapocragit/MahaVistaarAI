/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 29/12/18 11:26 AM
 */

package in.gov.mahapocra.farmerapppks.data.api;

public enum AppEnv {

    DBT {
        public String instance() {
            return "https://dbt-api.mahapocra.gov.in/SharedAPI/";
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
