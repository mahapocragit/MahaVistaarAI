package in.co.appinventor.services_api.helper;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: in.co.appinventor.services_api.helper.AIDigitsMaxFilter */
public class AIDigitsMaxFilter implements InputFilter {
    private Pattern mPattern;
    private double max;

    public AIDigitsMaxFilter(double max2, int digitsBeforeZero, int digitsAfterZero) {
        this.max = max2;
        this.mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = this.mPattern.matcher(dest);
        if (!matcher.matches()) {
            return "";
        } else {
            try {
                String replacement = source.subSequence(start, end).toString();
                String newVal = dest.toString().substring(0, dstart) + replacement + dest.toString().substring(dend, dest.toString().length());
                double input = Double.parseDouble(newVal);
                if (input <= this.max) {
                    return null;
                }
            } catch (NumberFormatException var12) {
            }

            return "";
        }
    }
}
