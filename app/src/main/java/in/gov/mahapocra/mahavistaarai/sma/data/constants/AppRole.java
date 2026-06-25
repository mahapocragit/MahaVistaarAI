/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 31/12/18 4:30 PM
 */

package in.gov.mahapocra.mahavistaarai.sma.data.constants;

public enum AppRole {

    CA {
        public int id() {
            return 22; //  unique id
        }
    },
    KT {
        public int id() {
            return 45; //  unique id
        }
    },

    PMU {
        public int id() {
            return 3; //  unique id
        }
    },
    SDAO {
        public int id() {
            return 19; //  unique id
        }
    },
    TAO {
        public int id() {
            return 23; //  unique id
        }
    },
    Tech_Cordinator {
        public int id() {
            return 30; //  unique id
        }
    },
    DSAO {
        public int id() {
            return 5; //  unique id
        }
    },
    AG_ASST {
        public int id() {
            return 9; //  unique id
        }
    };
    public abstract int id();

}
