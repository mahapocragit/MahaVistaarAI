package in.co.appinventor.services_api.helper;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/* renamed from: in.co.appinventor.services_api.helper.AIDecimalDigitsInputFilter */
public class AIDecimalDigitsInputFilter implements InputFilter {
    Pattern mPattern;

    public AIDecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        this.mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (!this.mPattern.matcher(dest).matches()) {
            return "";
        }
        return null;
    }
}
