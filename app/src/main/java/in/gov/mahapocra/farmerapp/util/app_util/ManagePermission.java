package in.gov.mahapocra.farmerapp.util.app_util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ManagePermission {

    private Activity activity;
    private ArrayList<String> arrayList;
    private int code;

    public ManagePermission(Activity mActivity, ArrayList<String> mList, int mCode) {
        this.activity = mActivity;
        this.arrayList = mList;
        this.code = mCode;
    }

    public void checkPermission(){
        if (isPermissionGranted() != PackageManager.PERMISSION_DENIED) {
            //showAlert();
        }else {
            Toast.makeText(activity,"Permissions already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public int isPermissionGranted(){
        int counter = 0;
        for (String permission: arrayList){
            counter += ContextCompat.checkSelfPermission(activity, permission);
        }
        return counter;
    }


    public String deniedPermission(){
        for (String permission: arrayList){
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED){
                return permission;
            }
        }
        return "";
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need permission(s)");
        builder.setMessage("Some permissions are required to do the task.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        // requestPermissions();
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton("Cancel", null);
        builder.create();
        builder.show();
    }


    public boolean processPermissionsResult(int code, String[] permission, int[] grantResults){
        int result = 0;
        if (grantResults.length != 0) {
            for (int item : grantResults) {
                result += item;
            }
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }


////////////////////////////////////////////////
    public void requestPermissions() {

        String permission = deniedPermission();
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation asynchronously
            Toast.makeText(activity,"Should show an explanation.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[arrayList.size()] , code);
        }
    }
}
