

package in.gov.mahapocra.mahavistaarai.chms;

public enum ImageTypes {

    ATTENDANCE {
        public String id() {
            return "attendance";
        }
    },
    OBSERVATION {
        public String id() {
            return "observation";
        }
    },

    TECHNOLOGY {
        public String id() {
            return "techdemo";
        }
    },

    SOIL_TEST_REPORT {
        public String id() {
            return "soil";
        }
    },
    CHMSIMG {
        public String id() {
            return "chmsimg";
        }
    },
    VISIT {
        public String id() {
            return "visit";
        }
    };
    public abstract String id();
}
