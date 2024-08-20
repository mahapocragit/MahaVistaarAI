package in.co.appinventor.services_api.helper;

import android.text.InputFilter;
import android.text.Spanned;

/* renamed from: in.co.appinventor.services_api.helper.AIInputFilterMinMax */
public class AIInputFilterMinMax implements InputFilter {
    private int max;
    private int min;

    public AIInputFilterMinMax(int min2, int max2) {
        this.min = min2;
        this.max = max2;
    }

    public AIInputFilterMinMax(String min2, String max2) {
        this.min = Integer.parseInt(min2);
        this.max = Integer.parseInt(max2);
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (this.isInRange(this.min, this.max, input)) {
                return null;
            }
        } catch (NumberFormatException var8) {
        }

        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
