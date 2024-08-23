package in.co.appinventor.services_api.app_util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.co.appinventor.R;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.AlertListEventListener;
import in.co.appinventor.services_api.listener.DatePickerCallbackListener;
import in.co.appinventor.services_api.listener.DatePickerRequestListener;
import in.co.appinventor.services_api.util.FileUtils;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;


/* renamed from: in.co.appinventor.services_api.app_util.AppUtility */
public class AppUtility {
    private static AppUtility appUtility = new AppUtility();

    private AppUtility() {
    }

    public static AppUtility getInstance() {
        return appUtility;
    }

    public static String getFileExtensionNameFromUrl(String url) {
        return url.substring(url.lastIndexOf(FileUtils.HIDDEN_PREFIX));
    }

    public void isInternetConnected(Context mContext) {
        if (!Utility.checkConnection(mContext)) {
            UIToastMessage.show(mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
        }
    }

    public boolean isConnected(Context mContext) {
        NetworkInfo[] info;
        @SuppressLint("WrongConstant") ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService("connectivity");
        if (!(connectivityManager == null || (info = connectivityManager.getAllNetworkInfo()) == null)) {
            for (NetworkInfo state : info) {
                if (state.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        UIToastMessage.show(mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
        return false;
    }

    @SuppressLint("WrongConstant")
    public String getDeviceCountryName(Context mContext) {
        @SuppressLint("WrongConstant") TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService("phone");
        if (telephonyManager.getSimState() == 1) {
            String countryName = telephonyManager.getSimCountryIso();
            DebugLog.getInstance().d("getSimCountryIso=" + countryName);
            return countryName;
        }
        String countryName2 = telephonyManager.getNetworkCountryIso();
        DebugLog.getInstance().d("getNetworkCountryIso=" + countryName2);
        return countryName2;
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean checkSpecialCharsAndNumbers(String value, String type) {
        String invalidChars = "`~!@#$%^&*()_->+{}[]|\\;:',<.</?0123456789";
        if (type.equals("names")) {
            invalidChars = "`~!@#$%^&*()_-+>{}[]|\\;:,<.</?0123456789";
        }
        for (int i = 0; i < value.length(); i++) {
            if (invalidChars.indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidPersonName(String value) {
        if (checkSpecialCharsAndNumbers(value, "names") && value.length() >= 2) {
            return true;
        }
        return false;
    }

    public boolean checkSpecialChars(String value) {
        for (int i = 0; i < value.length(); i++) {
            if ("`~!@#$%^>&*()_-+{}[]|\\;:',<.</?".indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidatePincode(String value) {
        int counter0 = 0;
        for (int j = 0; j < value.length(); j++) {
            if ((value.charAt(j) + "").equals("0")) {
                counter0++;
            }
            if (counter0 > 5) {
                return false;
            }
        }
        for (int i = 0; i < value.length(); i++) {
            if ("`~!@#$%^&*()_->+{}[]|\\;:',<.</?abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM".indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        if (value.length() >= 6) {
            return true;
        }
        return false;
    }

    public boolean checkNumbers(String value) {
        for (int i = 0; i < value.length(); i++) {
            if ("0123456789".indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidCallingNumber(String value) {
        String alphas = "abcdefghijklmnopqrstuvwxyz".toUpperCase() + "" + "abcdefghijklmnopqrstuvwxyz";
        int counter0 = 0;
        for (int j = 0; j < value.length(); j++) {
            if ((value.charAt(j) + "").equals("0")) {
                counter0++;
            }
            if (counter0 > 8) {
                return false;
            }
        }
        for (int i = 0; i < value.length(); i++) {
            if ("`~!@#$%^&*(>)_-{}[]|\\;:',<.</?".indexOf(value.charAt(i)) != -1 || alphas.indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        if (value.length() < 10 || value.contains("1234567890") || value.contains("0000000000") || value.contains("1111111111") || value.contains("2222222222") || value.contains("3333333333") || value.contains("4444444444") || value.contains("5555555555") || value.contains("6666666666") || value.contains("7777777777") || value.contains("8888888888")) {
            return false;
        }
        return true;
    }

    public String isValidMobileNumber(String paramString) {
        String str1 = paramString.replace("-", "").trim().replace(" ", "").trim();
        String localObject = "";
        try {
            if (str1.length() > 10 && str1.contains("+") && str1.length() == 13) {
                localObject = str1.substring(3, 13);
            }
            if (str1.length() > 10 && str1.length() == 12) {
                localObject = str1.substring(2, 12);
            }
            if (str1.length() <= 10 || str1.length() > 11) {
                return localObject;
            }
            return str1.substring(1);
        } catch (Exception localException) {
            localException.printStackTrace();
            return localObject;
        }
    }

    public String encodeURLEncoder(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String encodeStringWithURLEncoder(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String decodeStringWithURLDecoder(String encodedString) {
        try {
            if (!encodedString.isEmpty()) {
                return URLDecoder.decode(encodedString, "UTF-8");
            }
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("WrongConstant")
    public void printHashKey(Context context) {
        try {
            for (Signature signature : context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                DebugLog.getInstance().d("Appinventor KeyHash:" + Base64.encodeToString(md.digest(), 0));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
    }

    public Bitmap getVideoFrame(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString(), new HashMap());
            Bitmap frameAtTime = retriever.getFrameAtTime();
            try {
                retriever.release();
                return frameAtTime;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                return frameAtTime;
            }
        } catch (IllegalArgumentException ex2) {
            ex2.printStackTrace();
            try {
                retriever.release();
            } catch (RuntimeException | IOException ex3) {
                ex3.printStackTrace();
            }
        } catch (RuntimeException ex4) {
            ex4.printStackTrace();
            try {
                retriever.release();
            } catch (RuntimeException | IOException ex5) {
                ex5.printStackTrace();
            }
        } catch (Throwable th) {
            try {
                retriever.release();
            } catch (RuntimeException | IOException ex6) {
                ex6.printStackTrace();
            }
            try {
                throw th;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf(47) + 1);
    }

    public String getRealPathFromURIs(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow("_data");
            }
            if (cursor != null) {
                cursor.moveToFirst();
            }
            if (cursor != null) {
                String string = cursor.getString(column_index);
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void createColoredText(TextView tv, int r, int g, int b) {
        String value = tv.getText().toString();
        TextPaint tp = new TextPaint();
        tp.linkColor = Color.rgb(r, g, b);
        UnderlineSpan us = new UnderlineSpan();
        us.updateDrawState(tp);
        SpannableString str = new SpannableString(value);
        str.setSpan(us, 0, str.length(), 0);
        tv.setText(str);
    }

    public void makeAPhoneCall(Activity act, String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + phoneNumber.trim()));
        if (ActivityCompat.checkSelfPermission(act, "android.permission.CALL_PHONE") == 0) {
            act.startActivity(intent);
        }
    }

    public void openExternalBrowserLink(Activity act, String url) {
        Intent webintent = new Intent("android.intent.action.VIEW");
        webintent.setData(Uri.parse(url));
        act.startActivity(webintent);
    }

    public void openAddressOnGoogleMapIntent(Activity act, double latitude, double longitude, String label) {
        act.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + label + ")&z=19")));
    }

    public void showListPicker(TextView v, JSONArray ja, String title, String type, String typeId, Activity act, AlertListCallbackEventListener callBackListener) {
        final TextView tv = v;
        final CharSequence[] items = new CharSequence[ja.length()];
        final List<String> selectedIndexArray = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        final AlertListCallbackEventListener alertListCallbackEventListener = callBackListener;
        listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(items[which].toString());
                alertListCallbackEventListener.didSelectAlertViewListItem(tv, (String) selectedIndexArray.get(which));
            }
        });
        listPicker.show();
    }

    public void showListDialog(JSONArray ja, String title, String type, String typeId, Activity act, final AlertListCallbackEventListener callBackListener) {
        CharSequence[] items = new CharSequence[ja.length()];
        final List<String> selectedIndexArray = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBackListener.didSelectAlertViewListItem(null, (String) selectedIndexArray.get(which));
            }
        });
        listPicker.show();
    }

    public void showListDialogIndex(JSONArray ja, int requestCode, String title, String type, String typeId, Activity act, AlertListEventListener callBackListener) {
        final CharSequence[] items = new CharSequence[ja.length()];
        final List<String> selectedIndexArray = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        final AlertListEventListener alertListEventListener = callBackListener;
        final int i2 = requestCode;
        listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertListEventListener.didSelectListItem(i2, items[which].toString(), (String) selectedIndexArray.get(which));
            }
        });
        listPicker.show();
    }

    public void showListDialogMarketIndex(JSONArray ja, int requestCode, String title, String type, Activity act, AlertListEventListener callBackListener) {
        final CharSequence[] items = new CharSequence[ja.length()];
        final List<String> selectedIndexArray = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(type));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        final AlertListEventListener alertListEventListener = callBackListener;
        final int i2 = requestCode;
        listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertListEventListener.didSelectListItem(i2, items[which].toString(), (String) selectedIndexArray.get(which));
            }
        });
        listPicker.show();
    }

    public void showDatePicker(Context context, final TextView textView, final DatePickerCallbackListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        Context context2 = context;
        new DatePickerDialog(context2, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, calender.get(1), calender.get(2), calender.get(5)).show();
    }

    public void showDisabledPastDatePicker(Context context, final TextView textView, final DatePickerCallbackListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        Context context2 = context;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context2, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, calender.get(1), calender.get(2), calender.get(5));
        datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog1.show();
    }

    public void showDOBDatePicker(Context context, final TextView textView, final DatePickerCallbackListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        Context context2 = context;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context2, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, calender.get(1), calender.get(2), calender.get(5));
        datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog1.show();
    }

    public void showDisabledFutureDatePicker(Context context, Date date, final int requestCode, final DatePickerRequestListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        Context context2 = context;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context2, (view, year, monthOfYear, dayOfMonth) -> callbackListener.onDateSelected(requestCode, dayOfMonth, monthOfYear + 1, year), calender.get(1), calender.get(2), calender.get(5));
        datePickerDialog1.getDatePicker().setMaxDate(date.getTime());
        datePickerDialog1.show();
    }

    public void showPastDatePicker(Context context, Date date, int requestCode, DatePickerRequestListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        final DatePickerRequestListener datePickerRequestListener = callbackListener;
        final int i = requestCode;
        Context context2 = context;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context2, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePickerRequestListener.onDateSelected(i, dayOfMonth, monthOfYear + 1, year);
            }
        }, calender.get(1), calender.get(2), calender.get(5));
        datePickerDialog1.getDatePicker().setMaxDate(System.currentTimeMillis() - 86400000);
        datePickerDialog1.show();
    }

    public void showFutureDatesPicker(Context context, Date date, int requestCode, DatePickerRequestListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        int mYear = calender.get(1);
        int mMonth = calender.get(2);
        int mDay = calender.get(5);
        if (date != null) {
            calender.setTime(date);
        }
        final DatePickerRequestListener datePickerRequestListener = callbackListener;
        final int i = requestCode;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> datePickerRequestListener.onDateSelected(i, dayOfMonth, monthOfYear + 1, year), mYear, mMonth, mDay);
        datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog1.show();
    }

    public void showFutureDatePicker(Context context, Date date, final TextView textView, final DatePickerCallbackListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        Context context2 = context;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context2, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, calender.get(1), calender.get(2), calender.get(5));
        datePickerDialog1.getDatePicker().setMinDate(date.getTime());
        datePickerDialog1.show();
    }

    public Boolean isValidCallingNumber2(String value, String type) {
        boolean z = false;
        int counter0 = 0;
        for (int j = 0; j < value.length(); j++) {
            if ((value.charAt(j) + "").equals("0")) {
                counter0++;
            }
            if (counter0 > 8) {
                return false;
            }
        }
        if (type.equals("mobile")) {
            if (value.length() <= 0 || value.length() >= 10) {
                z = true;
            }
            return Boolean.valueOf(z);
        }
        if (value.length() <= 0 || value.length() >= 11) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean isValidAddressField(String value, int minLen) {
        boolean z = false;
        for (int i = 0; i < value.length(); i++) {
            if ("<>".indexOf(value.charAt(i)) != -1) {
                return false;
            }
        }
        if (value.length() >= minLen) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Date addDays(Date date, long diff) {
        int days = (int) (diff / 86400000);
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
        String startDate = format.format(date);
        String[] sDateParts = startDate.split("/");
        int sD = Integer.parseInt(sDateParts[0]);
        int sM = Integer.parseInt(sDateParts[1]);
        int sY = Integer.parseInt(sDateParts[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(sY, sM - 1, sD);
        calendar.add(5, days);
        try {
            date = format.parse(getDate(calendar));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("DateTime", "Start Date : " + startDate);
        Log.v("DateTime", "Date Diff MS: " + diff + ", Days: " + days);
        Log.v("DateTime", "End Date : " + format.format(date) + " Cal Date : " + getDate(calendar));
        return date;
    }

    public String getTimeStampReadable(String format, String dateTimeStamp) {
        if (format.isEmpty() && dateTimeStamp.isEmpty()) {
            return "";
        }
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(Long.parseLong(dateTimeStamp)));
    }

    public String getDate(Calendar cal) {
        return "" + cal.get(5) + "/" + (cal.get(2) + 1) + "/" + cal.get(1);
    }

    public long getDateDiff(Date sDate, Date eDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
        String startDate = format.format(sDate);
        String[] sDateParts = startDate.split("/");
        String endDate = format.format(eDate);
        String[] eDateParts = endDate.split("/");
        int sD = Integer.parseInt(sDateParts[0]);
        int sM = Integer.parseInt(sDateParts[1]);
        int sY = Integer.parseInt(sDateParts[2]);
        int eD = Integer.parseInt(eDateParts[0]);
        int eM = Integer.parseInt(eDateParts[1]);
        int eY = Integer.parseInt(eDateParts[2]);
        GregorianCalendar oldDate = new GregorianCalendar();
        oldDate.set(14, 0);
        oldDate.set(sY, sM - 1, sD);
        GregorianCalendar newDate = new GregorianCalendar();
        newDate.set(14, 0);
        newDate.set(eY, eM - 1, eD);
        long diff = newDate.getTimeInMillis() - oldDate.getTimeInMillis();
        Log.v("DateTime", "Start Date : " + startDate + " ,  MS:" + oldDate.getTimeInMillis());
        Log.v("DateTime", "Date Diff: " + diff);
        Log.v("DateTime", "End Date : " + endDate + " ,  MS:" + newDate.getTimeInMillis());
        return diff;
    }


    public String appVersionNumber(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String componentSeparatedByComma(List<String> array) {
        StringBuilder sb = new StringBuilder();
        for (String data : array) {
            if (sb.length() > 0 && !data.isEmpty()) {
                sb.append(',');
            }
            if (!data.isEmpty()) {
                sb.append(data);
            }
        }
        return sb.toString();
    }

    public String componentSeparatedByCommaWithJosnData(List<Object> array) {
        StringBuilder sb = new StringBuilder();
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            try {
                String data = ((JSONObject) it.next()).getString("UserId");
                if (sb.length() > 0 && !data.isEmpty()) {
                    sb.append(',');
                }
                if (!data.isEmpty()) {
                    sb.append(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String componentSeparatedByCommaWithSingleQuote(List<String> array) {
        StringBuilder sb = new StringBuilder();
        for (String data : array) {
            if (sb.length() > 0 && !data.isEmpty()) {
                sb.append("', '");
            }
            if (!data.isEmpty()) {
                sb.append("'");
                sb.append(data);
                sb.append("'");
            }
        }
        return sb.toString();
    }

    public String componentAddedByNextLine(JSONArray dataArray) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < dataArray.length()) {
            try {
                String data = dataArray.getJSONObject(i).getString("name");
                if (sb.length() > 0 && !data.isEmpty()) {
                    sb.append(10);
                }
                if (!data.isEmpty()) {
                    sb.append(data);
                }
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String componentSeparatedByCommaJSONArray(JSONArray dataArray, String key) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < dataArray.length()) {
            try {
                String data = dataArray.getJSONObject(i).getString(key);
                if (sb.length() > 0 && !data.isEmpty()) {
                    sb.append(',');
                }
                if (!data.isEmpty()) {
                    sb.append(data);
                }
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String hyphenSeparatedString(List<String> array) {
        StringBuilder sb = new StringBuilder();
        for (String data : array) {
            if (sb.length() > 0 && !data.isEmpty()) {
                sb.append('-');
            }
            if (!data.isEmpty()) {
                sb.append(data);
            }
        }
        return sb.toString();
    }

    public String convertArrayListIntoString(List<String> array) {
        StringBuilder sb = new StringBuilder();
        for (String data : array) {
            if (sb.length() > 0 && !data.isEmpty()) {
                sb.append(',');
            }
            if (!data.isEmpty()) {
                sb.append(data);
            }
        }
        return sb.toString();
    }

    public String getCheckedItemValues(GridView gv) {
        String values = "";
        if (gv == null) {
            String str = values;
            return values;
        }
        for (int i = 0; i < gv.getCount(); i++) {
            CheckBox item = (CheckBox) gv.getChildAt(i);
            if (item.isChecked()) {
                values = values + item.getText() + ",";
            }
        }
        if (values.length() > 1) {
            values = values.substring(0, values.length() - 1);
        }
        String str2 = values;
        return values.replace("&", "@");
    }

    public String ymdDateFormate(String date) {
        String[] datesArray = date.split("/");
        return (datesArray[2].trim() + "/" + datesArray[1].trim() + "/" + datesArray[0].trim()).trim();
    }

    public String ymdCalendarDateFormate(String date) {
        String[] datesArray = date.split("/");
        return (datesArray[2].trim() + "-" + datesArray[1].trim() + "-" + datesArray[0].trim()).trim();
    }

    public void showKeyboard(Context mContext, EditText editText) {
        @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(editText, 0);
        }
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(2, 1);
        }
        editText.requestFocus();
    }

    @SuppressLint("WrongConstant")
    public void hideKeyboard(Context mContext, EditText editText) {
        InputMethodManager imm;
        if (editText != null && (imm = (InputMethodManager) mContext.getSystemService("input_method")) != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @SuppressLint("WrongConstant")
    public void forceHideKeyboard(Context mContext, Activity activity) {
        InputMethodManager imm;
        View view = activity.getCurrentFocus();
        if (view != null && (imm = (InputMethodManager) mContext.getSystemService("input_method")) != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void createAppExternalDirectoryInSdCard(String dirName) {
        File mkDir = new File(Environment.getExternalStorageDirectory(), dirName);
        try {
            if (!mkDir.exists()) {
                mkDir.mkdirs();
                DebugLog.getInstance().d("SDCard root directory created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean deleteFileFromDirectory(String filePath) {
        File file = new File(filePath);
        boolean deleted = false;
        if (file.exists()) {
            deleted = file.delete();
        }
        return Boolean.valueOf(deleted);
    }

    public void deleteAllFileFromDirectory(File storageDir) {
        for (File tempFile : storageDir.listFiles()) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public void createExternalDirectoryInSdCard(String folderName) {
        try {
            File mkDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + folderName);
            if (!mkDir.exists()) {
                mkDir.mkdir();
                DebugLog.getInstance().d("SDCard " + folderName + " directory created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createSubExternalDirectoryInSdCard(String mainDir, String subFolderName) {
        try {
            File mkDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + mainDir + "/" + subFolderName);
            if (!mkDir.exists()) {
                mkDir.mkdir();
                DebugLog.getInstance().d("SD sub directory created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createStickerExternalDirectoryInSdCard(String subFolderName) {
        try {
            File mkDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + AppConstants.MAIN_FOLDER + "/" + subFolderName);
            if (!mkDir.exists()) {
                mkDir.mkdir();
                DebugLog.getInstance().d("SD sub directory created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEmojiFilterText(String text) {
        String utf8tweet = "";
        try {
            utf8tweet = new String(text.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Matcher unicodeOutlierMatcher = Pattern.compile("[^\\x00-\\x7F]", 66).matcher(utf8tweet);
            System.out.println("Before: " + utf8tweet);
            String utf8tweet2 = unicodeOutlierMatcher.replaceAll("");
            System.out.println("After: " + utf8tweet2);
            return utf8tweet2;
        } catch (Exception e2) {
            e2.printStackTrace();
            return utf8tweet;
        }
    }

    public int getDeviceHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getDeviceWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void sendMail(Activity act, String recepientEmail, String mailID, String mailSubject, String mailBody) {
        Intent i = new Intent("android.intent.action.SENDTO");
        i.setType("message/rfc822");
        i.putExtra("android.intent.extra.SUBJECT", mailSubject.replaceAll("<br>", "\n"));
        i.putExtra("android.intent.extra.EMAIL", "");
        i.putExtra("android.intent.extra.EMAIL", new String[]{mailID});
        i.putExtra("android.intent.extra.TEXT", mailBody.replaceAll("<br>", "\n"));
        i.setData(Uri.parse("mailto:" + recepientEmail));
        act.startActivity(Intent.createChooser(i, "Send mail.."));
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager.getDefault().sendTextMessage(phoneNo, (String) null, msg, (PendingIntent) null, (PendingIntent) null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendLongSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendMultipartTextMessage(msg, (String) null, smsManager.divideMessage(phoneNo), (ArrayList) null, (ArrayList) null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public String getDeviceUniqueID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    public String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        return !Character.isUpperCase(first) ? Character.toUpperCase(first) + s.substring(1) : s;
    }

    public String capitalizeString(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        return !Character.isUpperCase(first) ? Character.toUpperCase(first) + s.substring(1) : s;
    }

    public void rateBhojpuriAppApplicationOnPlayStore(Context context, String pck) {
        String PACKAGE_NAME = context.getPackageName();
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + pck.trim())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME.trim())));
        }
    }

    public void rateApplicationOnPlayStore(Context context) {
        String PACKAGE_NAME = context.getPackageName();
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + PACKAGE_NAME.trim())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME.trim())));
        }
    }

    public void shareOurBhojpuriApplication(Context context, String pck) {
        String packageName = context.getPackageName();
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", context.getResources().getString(R.string.app_name));
        sharingIntent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + pck);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void shareOurApplication(Context context) {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void shareDialog(Context context, String shareDialogTitle, String url) {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", shareDialogTitle);
        sharingIntent.putExtra("android.intent.extra.TEXT", url);
        context.startActivity(Intent.createChooser(sharingIntent, shareDialogTitle + "Share via"));
    }

    public void shareDialogVia(Context context, String url) {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.TEXT", url);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @SuppressLint("WrongConstant")
    public void shareImageFromSDCard(Context mContext, String fullPah, String message) {
        String msg = "";
        if (!TextUtils.isEmpty(message)) {
            msg = message;
        }
        Uri imageUri = Uri.parse(fullPah);
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        shareIntent.setFlags(268435456);
        shareIntent.putExtra("android.intent.extra.TEXT", msg);
        shareIntent.putExtra("android.intent.extra.STREAM", imageUri);
        shareIntent.setType("image/jpg");
        shareIntent.addFlags(1);
        shareIntent.addFlags(2);
        try {
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void shareImageFromSDCardURI(Context mContext, Uri imageUri, String message) {
        String msg = "";
        if (!TextUtils.isEmpty(message)) {
            msg = message;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        shareIntent.setFlags(268435456);
        shareIntent.putExtra("android.intent.extra.TEXT", msg);
        shareIntent.putExtra("android.intent.extra.STREAM", imageUri);
        shareIntent.setType("image/jpg");
        shareIntent.setFlags(1073741825);
        shareIntent.addFlags(1);
        shareIntent.addFlags(2);
        try {
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void shareImageOnWhatsApp(Context mContext, String fullPah, String message) {
        String msg = "";
        if (!TextUtils.isEmpty(message)) {
            msg = message;
        }
        Uri imageUri = Uri.parse(fullPah);
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        shareIntent.setPackage("com.whatsapp");
        shareIntent.putExtra("android.intent.extra.TEXT", msg);
        shareIntent.putExtra("android.intent.extra.STREAM", imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(1);
        try {
            mContext.startActivity(shareIntent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        boolean isURL = Patterns.WEB_URL.matcher(input).matches();
        if (!isURL) {
            return isURL;
        }
        String urlString = input + "";
        if (!URLUtil.isNetworkUrl(urlString)) {
            return isURL;
        }
        try {
            new URL(urlString);
            return true;
        } catch (Exception e) {
            return isURL;
        }
    }

    public boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (!(Build.VERSION.SDK_INT < 23 || context == null || permissions == null)) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getVideoDuration(String duration) {
        String mRemoved = duration.replace("PT", "").replace("H", ":").replace("M", ":");
        if (mRemoved.contains("S")) {
            return mRemoved.replace("S", "");
        }
        return mRemoved + "0";
    }

    public String changeDateFormat(String dateInString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        try {
            return new SimpleDateFormat("dd-MMM-yyyy  HH:mm:ss.SS  aa", Locale.ENGLISH).format(inputFormat.parse(dateInString));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String sanitizeText(String text) {
        if (TextUtils.isEmpty(text) || text.equals(AppConstants.NULL_STRING)) {
            return "";
        }
        return text;
    }

    /* renamed from: i */
    public String mo331i(Object text) {
        if (text == null) {
            return "";
        }
        return text.toString();
    }

    public String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(FileUtils.HIDDEN_PREFIX) + 1, fileName.length());
    }

    public String getFileExtension(File fileName) {
        return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(fileName).toString());
    }

    public String getFileName(Context mContext, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex("_display_name"));
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        if (result == null) {
            return uri.getLastPathSegment();
        }
        return result;
    }

    public boolean isExternalDirectoryExists(String dirName) {
        return new File(Environment.getExternalStorageDirectory() + "/" + dirName).exists();
    }

    public String getExternalDirectoryPath(String dirName) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    public void downloadFile(Context context, String uRl, String fileName) {
        String fileNameWithExt = "";
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + AppConstants.MAIN_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!getFileExt(fileName).equalsIgnoreCase("jpg") || !getFileExt(fileName).equalsIgnoreCase("png")) {
            fileNameWithExt = fileName + ".jpg";
        }
        File file = new File(dir + fileNameWithExt);
        if (file.exists()) {
            file.delete();
        }
        @SuppressLint("WrongConstant") DownloadManager mgr = (DownloadManager) context.getSystemService("download");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uRl));
        request.setAllowedNetworkTypes(3).setAllowedOverRoaming(false).setTitle(AppConstants.MAIN_FOLDER).setDescription("Profile picture downloading.").setDestinationInExternalPublicDir("/appinventor", fileName);
        if (mgr != null) {
            mgr.enqueue(request);
        }
    }

    public String getSalesforceDateFromat() {
        Date today = new Date();
        Calendar.getInstance().add(5, 1);
        String salesforceDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(today);
        DebugLog.getInstance().d("salesforceDate" + salesforceDate);
        return salesforceDate;
    }

    public String getDDMMYYYYFormatDate() {
        Date today = new Date();
        Calendar.getInstance().add(5, 1);
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH).format(today);
    }

    public String getOfflineName() {
        Date today = new Date();
        Calendar.getInstance().add(5, 1);
        String salesforceDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(today);
        String offline = AppConstants.kOFFLINE + salesforceDate;
        DebugLog.getInstance().d("offline salesforceDate" + salesforceDate);
        return offline;
    }

    public String getValidDate(String stringDate) {
        if (stringDate == null) {
            return "";
        }
        try {
            if (stringDate.length() <= 0) {
                return "";
            }
            Calendar.getInstance().add(5, 1);
            Date oldDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringDate);
            DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            String validDate = sdf.format(oldDate);
            System.out.println("SimpleDateFormat=" + sdf.format(oldDate));
            return validDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getCurrentSyncDateTime() {
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
        String validDate = sdf.format(date);
        System.out.println("getCurrentSyncDateTime=" + sdf.format(date));
        return validDate;
    }

    @SuppressLint("WrongConstant")
    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isTwoDigits(String value) {
        return value.length() == 2;
    }

    public String getCurrencyFormatted(String amount) {
        if (amount == null || amount.length() <= 0) {
            return "";
        }
        return String.format(Locale.getDefault(), "%,.2f", new Object[]{Double.valueOf(Double.parseDouble(amount))});
    }

    public long setSyncFrequencyOnInternetConnection(Context mContext) {
        if (!Utility.checkConnection(mContext)) {
            return AppConstants.SYNC_FREQUENCY_ONE_HOUR;
        }
        return 60;
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String decodeString(String encoded) {
        try {
            String decodedString = new String(Base64.decode(encoded, 2), "UTF-8");
            String str = decodedString;
            return decodedString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Object obj = "";
            return "";
        } catch (Throwable th) {
            Object obj2 = "";
            return "";
        }
    }

    public String convertByteArrayToString(byte[] byteArray) {
        return Base64.encodeToString(byteArray, 0);
    }

    public byte[] convertStringToByteArray(String data) {
        return Base64.decode(data, 0);
    }

    public Bitmap getBitmapFromByteStringDefaultBase64(String byteString) {
        byte[] decodedString = Base64.decode(byteString, 0);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap getBitmapFromByteString(String byteString) {
        byte[] decodedString = Base64.decode(byteString, 2);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap getBitmapFromByteStringWithUnsafe(String byteString) {
        byte[] decodedString = Base64.decode(byteString, 8);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public String getSingleQuoteValue(String value) {
        return "'" + value + "'";
    }

    public String getAppendDataValue(String value) {
        return value;
    }

    public String getDoubleQuoteValue(String value) {
        return "\"" + value + "\"";
    }

    public String getStringLeftPadding(String value) {
        return "\t" + value;
    }

    public String convertJSONArrayToStringValue(String jsonarray) {
        return jsonarray;
    }

    public void clearAppData(Context context) {
        try {
            Runtime.getRuntime().exec("pm clear " + context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAppSharedPrefData(Context mContext, String sharedPref) {
        mContext.getSharedPreferences(sharedPref, 0).edit().clear().apply();
    }

    public void clearApplicationData(Context context) {
        File cacheDirectory = context.getCacheDir();
        if (cacheDirectory != null) {
            File applicationDirectory = new File(cacheDirectory.getParent());
            if (applicationDirectory.exists()) {
                for (String fileName : applicationDirectory.list()) {
                    if (!fileName.equals("lib")) {
                        deleteFile(new File(applicationDirectory, fileName));
                    }
                }
            }
        }
    }

    public boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file == null) {
            return true;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        for (String child : file.list()) {
            if (!deleteFile(new File(file, child)) || !deletedAll) {
                deletedAll = false;
            } else {
                deletedAll = true;
            }
        }
        return deletedAll;
    }

    public void clearAppPreference(Context context, String preferenceName) {
        context.getSharedPreferences(preferenceName, 0).edit().clear().apply();
    }

    public void removePreferenceByKey(Context context, String preferenceName, String key) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        if (key != null) {
            settings.edit().remove(key).apply();
        }
    }

    public String bitmapEncodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), 2);
    }

    public Bitmap decodeBase64BitmapString(String bitmapString) {
        byte[] decodedBytes = Base64.decode(bitmapString, 2);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String getValidData(String saved, String key) {
        return saved.equalsIgnoreCase(key) ? getCurrentSyncDateTime() : saved;
    }

    public Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public JSONArray convertStringToJSONArray(String json) {
        if (json != null) {
            try {
                return new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public JSONObject convertStringToJSONObject(String jsonObj) {
        if (jsonObj != null) {
            try {
                return new JSONObject(jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, 0);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int height;
        int width;
        float bitmapRatio = ((float) image.getWidth()) / ((float) image.getHeight());
        if (bitmapRatio > 1.0f) {
            width = maxSize;
            height = (int) (((float) width) / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (((float) height) * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Bitmap getBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public Uri getBitmapToUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", (String) null));
    }

    @SuppressLint("WrongConstant")
    public void sendMail(Activity activity, String to, String cc, String subject, String msg, List<Uri> photoUris, int requestCode) {
        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(activity).setType("text/html").setChooserTitle("Choose application to send email").addEmailTo(to).addEmailCc(cc).setSubject(subject).setHtmlText(msg);
        if (photoUris != null) {
            for (Uri photoUri : photoUris) {
                intentBuilder.addStream(photoUri);
            }
        }
        if (activity.getPackageManager().resolveActivity(intentBuilder.getIntent(), 0) != null) {
            Intent intent = intentBuilder.createChooserIntent();
            intent.addFlags(1);
            intent.addFlags(2);
            activity.startActivityForResult(intent, requestCode);
            return;
        }
        Toast.makeText(activity, "Email app not found", 1).show();
    }

    public void hideSearchViewKeyboard(Context context, SearchView searchView) {
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) context.getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    @SuppressLint("WrongConstant")
    public void openApplicationSystemSetting(Activity context) {
        if (context != null) {
            Intent i = new Intent();
            i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            i.addCategory("android.intent.category.DEFAULT");
            i.setData(Uri.parse("package:" + context.getPackageName()));
            i.addFlags(268435456);
            i.addFlags(1073741824);
            i.addFlags(8388608);
            context.startActivity(i);
        }
    }

    public String sanitizeJSONObj(JSONObject jsonObject, String key) {
        String value = "";
        try {
            if (jsonObject.isNull(key)) {
                String str = value;
                return "";
            }
            value = jsonObject.getString(key);
            if (TextUtils.isEmpty(value) || value.equals(AppConstants.NULL_STRING)) {
                String str2 = value;
                return "";
            }
            String str3 = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public boolean sanitizeBoolJSONObj(JSONObject jsonObject, String key) {
        boolean value = false;
        try {
            if (jsonObject.isNull(key)) {
                return false;
            }
            value = jsonObject.getBoolean(key);
            boolean z = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public int sanitizeIntJSONObj(JSONObject jsonObject, String key) {
        int value = 0;
        try {
            if (jsonObject.isNull(key)) {
                return 0;
            }
            value = jsonObject.getInt(key);
            int i = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public double sanitizeDoubleJSONObj(JSONObject jsonObject, String key) {
        double value = 0.0d;
        try {
            if (jsonObject.isNull(key)) {
                return 0.0d;
            }
            value = jsonObject.getDouble(key);
            double d = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public long sanitizeFloatJSONObj(JSONObject jsonObject, String key) {
        long value = 0;
        try {
            if (jsonObject.isNull(key)) {
                return 0;
            }
            value = jsonObject.getLong(key);
            long j = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public JSONObject sanitizeJSONObjJSONObj(JSONObject jsonObject, String key) {
        JSONObject value = null;
        try {
            if (jsonObject.isNull(key)) {
                return null;
            }
            value = jsonObject.getJSONObject(key);
            JSONObject jSONObject = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public JSONArray sanitizeArrayJSONObj(JSONObject jsonObject, String key) {
        JSONArray value = null;
        try {
            if (jsonObject.isNull(key)) {
                return null;
            }
            value = jsonObject.getJSONArray(key);
            JSONArray jSONArray = value;
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public String capitalizeName(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = new CursorLoader(context, contentUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null).loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    public RequestBody getRequestBody(String params) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params);
    }

    public boolean isValidPhoneNumber(String mobile) {
        if (!mobile.contains("1234567890") && !mobile.contains("0000000000") && !mobile.contains("1111111111") && !mobile.contains("2222222222") && !mobile.contains("3333333333") && !mobile.contains("4444444444") && !mobile.contains("5555555555") && !mobile.contains("6666666666") && !mobile.contains("7777777777") && !mobile.contains("8888888888")) {
            return mobile.matches("^[0-9]{10}$");
        }
        return false;
    }

    public String getMacAddress() {
        try {
            for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    int length = macBytes.length;
                    for (int i = 0; i < length; i++) {
                        res1.append(String.format("%02X:", new Object[]{Byte.valueOf(macBytes[i])}));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public Boolean isDeviceRooted(Context context) {
        return Boolean.valueOf(isRooted());
    }

    private boolean isRooted() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        try {
            if (new File("/system/app/Superuser.apk").exists()) {
                return true;
            }
        } catch (Exception e) {
        }
        if (canExecuteCommand("/system/xbin/which su") || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su")) {
            return true;
        }
        return false;
    }

    private static boolean canExecuteCommand(String command) {
        try {
            Runtime.getRuntime().exec(command);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String encodeBase64Param(String param) {
        return Base64.encodeToString(param.getBytes(Charset.forName("UTF-8")), 0);
    }

    public String decodeBase64Param(String param) {
        return new String(Base64.decode(param.getBytes(), 0), Charset.forName("UTF-8"));
    }

    public String readJSONFromAsset(Context mContext, String fileName) {
        try {
            InputStream is = mContext.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String saveImageToDir(Bitmap capturedBitmap, String dirPath, String fileName) {
        OutputStream fOutputStream = null;
        File filePath = new File(dirPath, fileName);

        try {
            fOutputStream = new FileOutputStream(filePath);
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return filePath.getAbsolutePath();
    }

    public String saveImageToDirAndSystemGallery(Context context, Bitmap capturedBitmap, String dirPath, String fileName) {
        OutputStream fOutputStream = null;
        File filePath = new File(dirPath, fileName);

        try {
            fOutputStream = new FileOutputStream(filePath);
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath.getAbsolutePath(), filePath.getName(), filePath.getName());
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return filePath.getAbsolutePath();
    }


    public int getFileSizeMB(String selectedPath) {
        return (int) ((new File(selectedPath).length() / 1024) / 1024);
    }

    public int getFileSizeKB(String selectedPath) {
        return (int) (new File(selectedPath).length() / 1024);
    }

    public JSONArray sortJSONArrayAlphabetically(JSONArray jsonArr, final String key) throws JSONException {
        if (jsonArr == null) {
            return null;
        }
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private final String KEY_NAME = key;

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.String} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: java.lang.Object} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.String} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int compare(JSONObject r5, JSONObject r6) {
                /*
                    r4 = this;
                    java.lang.String r1 = new java.lang.String
                    r1.<init>()
                    java.lang.String r2 = new java.lang.String
                    r2.<init>()
                    java.lang.String r3 = r4.KEY_NAME     // Catch:{ JSONException -> 0x0023 }
                    java.lang.Object r3 = r5.get(r3)     // Catch:{ JSONException -> 0x0023 }
                    r0 = r3
                    java.lang.String r0 = (java.lang.String) r0     // Catch:{ JSONException -> 0x0023 }
                    r1 = r0
                    java.lang.String r3 = r4.KEY_NAME     // Catch:{ JSONException -> 0x0023 }
                    java.lang.Object r3 = r6.get(r3)     // Catch:{ JSONException -> 0x0023 }
                    r0 = r3
                    java.lang.String r0 = (java.lang.String) r0     // Catch:{ JSONException -> 0x0023 }
                    r2 = r0
                L_0x001e:
                    int r3 = r1.compareTo(r2)
                    return r3
                L_0x0023:
                    r3 = move-exception
                    goto L_0x001e
                */
                throw new UnsupportedOperationException("Method not decompiled: p000in.p001co.appinventor.services_api.app_util.AppUtility.C006212.compare(org.json.JSONObject, org.json.JSONObject):int");
            }
        });
        for (int i2 = 0; i2 < jsonArr.length(); i2++) {
            sortedJsonArray.put(jsonValues.get(i2));
        }
        return sortedJsonArray;
    }

    public boolean isDoubleDigit(int number) {
        return (number > 9 && number < 100) || (number < -9 && number > -100);
    }

    public boolean isSingleDigit(int number) {
        return number <= 9 || number < -9;
    }

    public long getTimeStampFromDate(String fromDate) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fromDate).getTime();
            DebugLog.getInstance().d("timestamp=" + timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp / 1000;
    }

    public long getTimeStampInMilliSecFromDate(String fromDate) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fromDate).getTime();
            DebugLog.getInstance().d("timestamp=" + timestamp);
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    public boolean validateFromToDate(String fromDate, String toDate) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (!dfDate.parse(fromDate).before(dfDate.parse(toDate)) && !dfDate.parse(fromDate).equals(dfDate.parse(toDate))) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String capitaliseString(String name) {
        StringBuilder sb = new StringBuilder(name.toLowerCase());
        if (sb.length() < 1) {
            System.out.println("next line is empty");
        } else {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();

    }

    public JSONArray addIsSelected(JSONArray jsonArray) {
        int i = 0;
        JSONArray dataArray = new JSONArray();
        while (i < jsonArray.length()) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("is_selected", 0);
                dataArray.put(jsonObject);
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataArray;
    }

    public String toTitleCase(String data) {
        if (data == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(data);
        int len = builder.length();
        for (int i = 0; i < len; i++) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }
}
