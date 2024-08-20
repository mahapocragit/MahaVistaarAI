package in.co.appinventor.services_api.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

import in.co.appinventor.services_api.debug.DebugLog;

/* renamed from: in.co.appinventor.services_api.util.JSONUtil */
public class JSONUtil {

    /* renamed from: in.co.appinventor.services_api.util.JSONUtil$SortMode */
    public enum SortMode {
        ASC,
        DESC
    }

    public static JSONArray sortString(JSONArray dataArray, final String field, final SortMode sortMode) {
        JSONObject[] data = new JSONObject[dataArray.length()];
        for (int i = 0; i < dataArray.length(); i++) {
            try {
                data[i] = dataArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Arrays.sort(data, new Comparator<JSONObject>() {
            public int compare(JSONObject dataItem1, JSONObject dataItem2) {
                String value1 = "";
                String value2 = "";
                try {
                    value1 = dataItem1.getString(field);
                    value2 = dataItem2.getString(field);
                } catch (JSONException e) {
                    DebugLog.getInstance().e("JSONException in combineJSONArrays sort section" + e);
                }
                int returnVal = value1.compareToIgnoreCase(value2);
                if (sortMode == SortMode.DESC) {
                    return returnVal * -1;
                }
                return returnVal;
            }
        });
        JSONArray array = new JSONArray();
        for (JSONObject put : data) {
            array.put(put);
        }
        return array;
    }

    public static JSONArray clone(JSONArray data) {
        JSONArray clonedData = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            try {
                clonedData.put(new JSONObject(data.getJSONObject(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return clonedData;
    }
}
