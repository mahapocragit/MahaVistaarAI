package in.co.appinventor.services_api.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* renamed from: in.co.appinventor.services_api.api.AppinventorGetAsyncTask */
public class AppinventorGetAsyncTask extends AsyncTask<String, Void, String> {
    /* access modifiers changed from: protected */
    public String doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(((HttpURLConnection) url.openConnection()).getInputStream()));
            String s = bufferedReader.readLine();
            bufferedReader.close();
            return s;
        } catch (IOException e2) {
            Log.e("Error: ", e2.getMessage(), e2);
            return null;
        }
    }
}
