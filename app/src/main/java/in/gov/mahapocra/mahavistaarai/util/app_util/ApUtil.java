package in.gov.mahapocra.mahavistaarai.util.app_util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.co.appinventor.services_api.listener.DatePickerCallbackListener;

public class ApUtil {

    private Context _context;
    public ApUtil(Context context) {
        this._context = context;
    }

    public static String getCamelCaseStreing(String workString) {

        String str = workString;
        String words[]=str.split("\\s");
        String capitalizeStrName="";
        try {
            for(String word:words){
                // Capitalize first letter
                String firstLetter=word.substring(0,1);
                // Get remaining letter
                String remainingLetters=word.substring(1);
                capitalizeStrName+=firstLetter.toUpperCase()+remainingLetters+" ";
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d("capitalizeStrName=", capitalizeStrName);
        return capitalizeStrName;
    }
}



