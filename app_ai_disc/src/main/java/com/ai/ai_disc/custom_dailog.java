package com.ai.ai_disc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;


public class custom_dailog extends DialogFragment {

    public static String TAG = "custom_dailog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);

                                dialog.cancel();
                            }
                        }, 10);

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {//no
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        return alertDialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Personalizamos
        //Log.d(TAG, "onStart: ");
        Resources res = getResources();

        //Buttons
        Button positive_button = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        positive_button.setBackgroundColor(getResources().getColor(R.color.black));


    }
}
