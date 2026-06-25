package in.gov.mahapocra.mahavistaarai.util.app_util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.co.appinventor.services_api.listener.AlertListEventListener;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.ui.adapters.CustomListAdapter;

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
        return capitalizeStrName;
    }

    private static ApUtil appUtility = new ApUtil();

    private ApUtil() {
    }

    public static ApUtil getInstance() {
        return appUtility;
    }

    public void showListDialogIndex(JSONArray ja, int requestCode, String title,
                                    String type, String typeId, Activity act,
                                    AlertListEventListener callBackListener) {

        List<String> selectedIndexArray = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CustomListAdapter adapter = new CustomListAdapter(act, ja, type);

        AlertDialog.Builder dialog = new AlertDialog.Builder(act);

        TextView titleView = new TextView(act);
        titleView.setText(title);
        titleView.setPadding(20, 20, 20, 20);
        titleView.setTextSize(20);
//        titleView.setTextColor(Color.BLUE);   // <-- Title text color blue
        titleView.setTextColor(act.getResources().getColor(R.color.design_default_color_secondary_variant));
        titleView.setTypeface(null, Typeface.BOLD);
        dialog.setCustomTitle(titleView);

        dialog.setAdapter(adapter, (dialogInterface, which) -> {
            try {
                String name = ja.getJSONObject(which).getString(type);
                String id = selectedIndexArray.get(which);
                callBackListener.didSelectListItem(requestCode, name, id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        dialog.show();
    }

}



