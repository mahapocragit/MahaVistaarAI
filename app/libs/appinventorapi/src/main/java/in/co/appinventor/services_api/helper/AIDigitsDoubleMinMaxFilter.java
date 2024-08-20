package in.co.appinventor.services_api.helper;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: in.co.appinventor.services_api.helper.AIDigitsDoubleMinMaxFilter */
public class AIDigitsDoubleMinMaxFilter implements InputFilter {
    private Pattern mPattern;
    private double max;
    private double min;

    public AIDigitsDoubleMinMaxFilter(double min2, double max2, int digitsBeforeZero, int digitsAfterZero) {
        this.min = min2;
        this.max = max2;
        this.mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = this.mPattern.matcher(dest);
        if (!matcher.matches()) {
            return "";
        } else {
            try {
                String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
                newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
                double input = Double.parseDouble(newVal);
                if (this.isInRange(this.min, this.max, input)) {
                    return null;
                }
            } catch (NumberFormatException var11) {
            }

            return "";
        }
    }


    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
