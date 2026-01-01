/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 29/12/18 11:26 AM
 */

package in.gov.mahapocra.mahavistaarai.sma;

public enum AppEnv {

    UAT_SSO {
        public String instance() {
            return "https://stage-sso.mahapocra.gov.in/"; //by mayu

        }
    },
    SSO {
        public String instance() {
            return "https://sso-ndksp.mahapocra.gov.in/";//by mayu
        }
    },

    UAT {
        public String instance() {

            return "https://stage-sma-api.mahapocra.gov.in/";  //by mayu
        }
    },
    PROD {
        public String instance() {
            return "https://sma-ndksp-api.mahapocra.gov.in/"; //by mayu
        }
    };

    public abstract String instance();

}
