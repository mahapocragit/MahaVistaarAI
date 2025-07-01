package in.gov.mahapocra.mahavistaarai.util.app_util;

import android.content.Context;

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
}



