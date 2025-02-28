package in.gov.mahapocra.farmerapppks.util.app_util;

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
    private static final String IMAGE_DIRECTORY = "/PoCRA_MLP";

    // constructor
    public ApUtil(Context context) {
        this._context = context;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }



    public static JSONArray getSpinnerSelectedItemArray(List<Integer> spinnerView, List<String> arrayList) {
        JSONArray j = new JSONArray();
        if (arrayList.size() > 0) {
            for (int id = 0; id < spinnerView.size(); id++) {
                if (id < arrayList.size()) {
                    try {
                        JSONObject itemJson = new JSONObject();
                        String name = arrayList.get(spinnerView.get(id));
                        itemJson.put("id", id);
                        itemJson.put("name", name);
                        j.put(itemJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return j;
    }


    /***To get and set spinner data*/
//
//    public static void setSelectedSpinnerData(String sItem, MultipleSelectionSpinner spinnerView, List<String> list) {
//        spinnerView.setItems(list);
//        if (!sItem.equalsIgnoreCase("")) {
//            if (sItem.length() > 1) {
//                String[] kharifId = sItem.split(",");
//                List<String> lS = new ArrayList<>(Arrays.asList(kharifId));
//                spinnerView.setSelection(lS);
//            } else {
//                int i = Integer.valueOf(sItem);
//                spinnerView.setSelection(i);
//            }
//        }
//    }

//    public static boolean checkInternetConnection(Context mContext) {
//        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//
//        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            System.out.println("true wifi");
//            return true;
//        }
//
//        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//            System.out.println("true edge");
//            return true;
//        }
//        if (networkInfo != null && networkInfo.isConnected()) {
//            System.out.println("true net");
//            return true;
//        }
//        System.out.println("false");
//        return false;
//    }


    public static String getDateByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
    }

    public static String getDateYMDByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
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


    public static String getYearByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        long time = Long.parseLong(timeStamp) * 1000L;
        cal.setTimeInMillis(time);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }


    public static Boolean isValidDate(String date){
        Pattern p = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})");
        Matcher m = p.matcher(date);
        return (m.find() && m.group().equals(date));
    }



    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    public static void showDatePickerBtnTwoDates(Context context, Date sDate, Date eDate, final TextView textView, final DatePickerCallbackListener callbackListener) {
        int mYear = Calendar.YEAR;
        int mMonth = Calendar.MONTH;
        int mDay = Calendar.DAY_OF_MONTH;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        long sMillis = sDate.getTime();
        long eMillis = eDate.getTime();
        datePickerDialog1.getDatePicker().setMinDate(sMillis);
        datePickerDialog1.getDatePicker().setMaxDate(eMillis);
        datePickerDialog1.show();
    }


    public static Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

//
//    public static void showCustomListPicker(final TextView v, JSONArray ja, String title, String type, String typeId, Activity act, final AlertListCallbackEventListener callBackListener) {
//        final CharSequence[] items = new CharSequence[ja.length()];
//
//        final ArrayList<String> name = new ArrayList<String>();
//
//        final List<String> selectedIndexArray = new ArrayList();
//
//        for (int i = 0; i < ja.length(); ++i) {
//            try {
//                items[i] = ja.getJSONObject(i).getString(type);
//                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
//                name.add(ja.getJSONObject(i).getString(type));
//            } catch (JSONException var13) {
//                var13.printStackTrace();
//            }
//        }
//
//        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
//        listPicker.setTitle(title);
//        LayoutInflater inflater = act.getLayoutInflater();
//        View convertView = (View) inflater.inflate(R.layout.custom_list_picker, null);
//        listPicker.setView(convertView);
//        ListView lv = (ListView) convertView.findViewById(R.id.listView);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, name);
//        lv.setAdapter(adapter);
//        final AlertDialog alertDialog = listPicker.show();
//        listPicker.setCancelable(true);
//
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                v.setText(items[position].toString());
//                callBackListener.didSelectAlertViewListItem(v, (String) selectedIndexArray.get(position));
//                alertDialog.dismiss();
//            }
//        });
//
//    }


//    public static void loadImage(ImageView imageView, String image_url, Context context) {
//        try {
//            Picasso.get().invalidate(image_url);
//            Picasso.get()
//                    .load(image_url)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                    .placeholder(R.drawable.ic_thumbnail)
//                    .resize(100, 100)
//                    .centerCrop()
//                    .error(R.drawable.ic_thumbnail)
//                    .into(imageView);
//        } catch (Exception ex) {
//            ex.toString();
//        }
//
//    }


    // To check all String Null or empty
    public static boolean isAllStringNullOrEmpty(String... strings) {
        for (String s : strings)
            if (s == null || s.isEmpty())
                return true;
        return false;
    }

    // To check any String Null or empty
    public static boolean isAnyOneStringNotNullOrEmpty(String... strings) {
        boolean result = false;
        System.out.println("arr length" + strings.length);

        for (String s : strings) {

            if (s == null || s.isEmpty()) {
                result = false;
            } else {
                result = true;
                break;
            }
        }
        return result;
    }

//    public static boolean isAllStringNotNullOrEmptyview(ValidationView... val) {
//        boolean result = false;
//        for (ValidationView s : val) {
//            if (s.getString() == null || s.getString().isEmpty()) {
//                s.getView().setFocusable(true);
//                s.getView().setFocusableInTouchMode(true);
//                s.getView().requestFocus();
//                result = true;
//            }
//        }
//        return result;
//    }

//    public static boolean isAllStringNotNullOrEmptyOrZeroView(ValidationView... val) {
//        boolean result = false;
//        for (ValidationView s : val) {
//            if (s.getString() == null || s.getString().isEmpty()) {
//                s.getView().setFocusable(true);
//                s.getView().setFocusableInTouchMode(true);
//                s.getView().requestFocus();
//                result = true;
//            }else if (s.getString() != null && !s.getString().isEmpty() && s.getString().equalsIgnoreCase("")){
//                result = true;
//            }
//        }
//        return result;
//    }

//    public static boolean isAllStringNotNullOrEmptyview(ArrayList<ValidationView> validationlist) {
//        boolean result = false;
//        for (int i = 0;i<validationlist.size();i++) {
//            if (validationlist.get(i).getString() == null || validationlist.get(i).getString().isEmpty()) {
//                validationlist.get(i).getView().setFocusable(true);
//                validationlist.get(i).getView().setFocusableInTouchMode(true);
//                validationlist.get(i).getView().requestFocus();
//                result =  true;
//            }
//        }
//
//        return result;
//    }


    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isPDFReportExist(String extension, String fileName, String fileDirectory) {
        boolean result = false;

        String fileNameWithExt = fileName + extension;
        File directory = new File(Environment.getExternalStorageDirectory() + fileDirectory);
        if (directory.exists()) {
            File file = new File(directory + "/" + fileNameWithExt /* what you want to load in SD card */);
            if (file.isFile()) {
                result = true;
            }
        }
        return result;

    }


    public static String getFilePathWithExt(String extension, String fileName, String fileDirectory) {
        String path = "";
        String fileNameWithExt = fileName + extension;
        File directory = new File(Environment.getExternalStorageDirectory() + fileDirectory);
        if (directory.exists()) {
            File file = new File(directory + "/" + fileNameWithExt /* what you want to load in SD card */);
            if (file.isFile()) {
                path = file.toString();
            }
        }
        return path;
    }


//    public static void sharePdfFile(Context context, String path) {
//        String TAG = "Debug";
//
//        if (context == null)
//            return;
//
//        File file = new File(path);
//        if (file.exists()) {
//            Uri uri;
//            if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
//                uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", file);
//            } else {
//                uri = Uri.fromFile(file);
//            }
//
//            Intent share = new Intent();
//            share.setAction(Intent.ACTION_SEND);
//            share.setType("application/pdf");
//            share.putExtra(Intent.EXTRA_STREAM, uri);
//            context.startActivity(Intent.createChooser(share, "Share PDF"));
//        } else {
//            UIToastMessage.show(context, "File doesn't exist");
//            // Log.i(TAG, "Fatal Error: File doesn't exist -> " + path);
//        }
//    }


//    public static void viewLocalPdfFile(Context context, String path) {
//        String TAG = "Debug";
//
//        if (context == null)
//            return;
//
//        File file = new File(path);
//
//        if (file.exists()) {
//            Intent intent;
//            Uri uri;
//            if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.N)) {
//                uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", file);
//
//                intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(uri);
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                context.startActivity(intent);
//
//            } else {
//                uri = Uri.fromFile(file);
//
//                intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri, "application/pdf");
//                intent = Intent.createChooser(intent, "Open File");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        }
//    }

    public static boolean isValidMobileNumber(String s) {
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }


    public static String getStringFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            // Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            // Log.e("File Path", "Path " + file.getPath());

        } catch (Exception e) {
            // Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static File getFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            // Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            // Log.e("File Path", "Path " + file.getPath());

        } catch (Exception e) {
            // Log.e("Exception", e.getMessage());
        }
        return file;
    }

}



