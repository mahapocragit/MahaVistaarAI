/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 8/6/19 3:58 PM
 */

package in.gov.mahapocra.mahavistaarai.sma;

public enum APIKeys {


    SSO_DEV {

        public String key() {
            return "32KcHokMBEpPgXT8QQ";
        }
    },

    SSO_PROD {

        public String key() {
            return "32KcHokMBEpPgXT8QQ";
        }
    };

    public abstract String key();

}
