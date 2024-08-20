package in.co.appinventor.services_api.util;

/* renamed from: in.co.appinventor.services_api.util.OrientationHelper */
public class OrientationHelper {
    public static final int LANDSCAPE = 2;
    public static final int NOTHING = -100;
    public static final int PORTRAIT = 1;

    public static Integer userTending(int orientation, int previous) {
        if (previous == 1) {
            if (orientation > 85 && orientation < 115) {
                return 2;
            }
            if (orientation > 285 && orientation < 300) {
                return 2;
            }
            if (orientation > 160 && orientation < 210) {
                return 2;
            }
        } else if (previous == 2) {
            if (orientation > 0 && orientation < 30) {
                return 1;
            }
            if (orientation > 330 && orientation < 360) {
                return 1;
            }
        }
        return -100;
    }
}
