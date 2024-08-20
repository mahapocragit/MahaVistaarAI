package in.co.appinventor.services_api.util;

import java.util.TreeMap;

/* renamed from: in.co.appinventor.services_api.util.ApiUtils */
public class ApiUtils {
    public static String getAccessToken(TreeMap<String, String> map, String app_secret) {
        String toMd5 = "";
        for (String key : map.keySet()) {
            toMd5 = toMd5 + key + "=" + map.get(key) + "&";
        }
        return MD5.digest(toMd5.substring(0, toMd5.length() - 1) + app_secret);
    }
}
