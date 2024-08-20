package in.co.appinventor.services_api.helper;

import android.text.InputFilter;
import android.text.Spanned;

/* renamed from: in.co.appinventor.services_api.helper.AIInputFilterDoubleMinMax */
public class AIInputFilterDoubleMinMax implements InputFilter {
    private double max;
    private double min;

    public AIInputFilterDoubleMinMax(double min2, double max2) {
        this.min = min2;
        this.max = max2;
    }

    public AIInputFilterDoubleMinMax(String min2, String max2) {
        this.min = Double.parseDouble(min2);
        this.max = Double.parseDouble(max2);
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            double input = Double.parseDouble(dest.toString() + source.toString());
            if (this.isInRange(this.min, this.max, input)) {
                return null;
            }
        } catch (NumberFormatException var9) {
        }

        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
