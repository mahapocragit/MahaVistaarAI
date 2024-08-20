package in.co.appinventor.services_api.app_util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* renamed from: in.co.appinventor.services_api.app_util.AndroidPermissions */
public class AndroidPermissions {
    private Activity mContext;
    private List<String> mPermissionsToRequest = new ArrayList();
    private String[] mRequiredPermissions;

    public AndroidPermissions(Activity context, String... requiredPermissions) {
        this.mContext = context;
        this.mRequiredPermissions = requiredPermissions;
    }

    public boolean checkPermissions() {
        if (Build.VERSION.SDK_INT <= 22) {
            return true;
        }
        for (String permission : this.mRequiredPermissions) {
            if (ContextCompat.checkSelfPermission(this.mContext, permission) != 0) {
                this.mPermissionsToRequest.add(permission);
            }
        }
        if (!this.mPermissionsToRequest.isEmpty()) {
            return false;
        }
        return true;
    }

    public void requestPermissions(int requestCode) {
        String[] request = (String[]) this.mPermissionsToRequest.toArray(new String[this.mPermissionsToRequest.size()]);
        StringBuilder log = new StringBuilder();
        log.append("Requesting permissions:\n");
        for (String permission : request) {
            log.append(permission).append("\n");
        }
        Log.i(getClass().getSimpleName(), log.toString());
        ActivityCompat.requestPermissions(this.mContext, request, requestCode);
    }

    public boolean areAllRequiredPermissionsGranted(String[] permissions, int[] grantResults) {
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return false;
        }
        LinkedHashMap<String, Integer> perms = new LinkedHashMap<>();
        for (int i = 0; i < permissions.length; i++) {
            if (!perms.containsKey(permissions[i]) || (perms.containsKey(permissions[i]) && perms.get(permissions[i]).intValue() == -1)) {
                perms.put(permissions[i], Integer.valueOf(grantResults[i]));
            }
        }
        for (Map.Entry<String, Integer> entry : perms.entrySet()) {
            if (entry.getValue().intValue() != 0) {
                return false;
            }
        }
        return true;
    }
}
