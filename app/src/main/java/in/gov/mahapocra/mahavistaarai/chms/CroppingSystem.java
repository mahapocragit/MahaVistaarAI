/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 1/12/18 3:21 PM
 */

package in.gov.mahapocra.mahavistaarai.chms;

public enum CroppingSystem {

    SOLE {
        public int id() {
            return 1; // Sole unique id
        }
    },
    INTER_CROP {
        public int id() {
            return 2; // Intercrop unique id
        }
    };

    public abstract int id();

}
