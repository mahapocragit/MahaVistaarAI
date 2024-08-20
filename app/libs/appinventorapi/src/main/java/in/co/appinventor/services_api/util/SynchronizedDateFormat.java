package in.co.appinventor.services_api.util;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* renamed from: in.co.appinventor.services_api.util.SynchronizedDateFormat */
public class SynchronizedDateFormat extends SimpleDateFormat {
    private static final long serialVersionUID = 6612933786679648650L;

    public SynchronizedDateFormat() {
    }

    public SynchronizedDateFormat(String paramString, Locale paramLocale) {
        super(paramString, paramLocale);
    }

    public void applyPattern(String paramString) {
        super.applyPattern(paramString);
    }

    public Date parse(String paramString) throws ParseException {
        try {
            return super.parse(paramString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
