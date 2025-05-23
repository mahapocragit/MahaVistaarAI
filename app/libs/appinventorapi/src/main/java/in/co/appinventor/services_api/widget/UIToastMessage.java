package in.co.appinventor.services_api.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/* renamed from: in.co.appinventor.services_api.widget.UIToastMessage */
public class UIToastMessage {
    public static void show(final Context mContext, final String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on main thread
            showToast(mContext, message, Toast.LENGTH_LONG, 17);
        } else {
            new Handler(Looper.getMainLooper()).post(() ->
                    showToast(mContext, message, Toast.LENGTH_LONG, 17));
        }
    }

    private static void showToast(Context mContext, String message, int duration, int gravity) {
        Toast toast = Toast.makeText(mContext, message, duration);
        View view = toast.getView();

        if (view != null) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(0xFF000000); // black
            shape.setCornerRadius(50f);
            view.setBackground(shape);

            // Optional: adjust TextView inside Toast
            TextView messageTextView = view.findViewById(android.R.id.message);
            if (messageTextView != null) {
                messageTextView.setPadding(15, 2, 15, 2);
                messageTextView.setTextColor(0xFFFFFFFF); // white
                messageTextView.setTextSize(16f);
            }
        }

        toast.setGravity(gravity, 0, 0);
        toast.show();
    }


    @SuppressLint("WrongConstant")
    public static void showShortDuration(Context mContext, String message) {
        Toast toast = Toast.makeText(mContext, message, 0);
        View view = toast.getView();
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(0);
        shape.setColor(-16777216);
        shape.setCornerRadius(50.0f);
        view.setBackgroundDrawable(shape);
        toast.setGravity(17, 0, 0);
        @SuppressLint("ResourceType") TextView messageTextView = (TextView) toast.getView().findViewById(16908299);
        messageTextView.setPadding(15, 2, 15, 2);
        messageTextView.setTextColor(-1);
        messageTextView.setTextSize(16.0f);
        toast.show();
    }

    @SuppressLint("WrongConstant")
    public static void showOnTop(Context mContext, String message) {
        Toast toast = Toast.makeText(mContext, message, 1);
        View view = toast.getView();
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(0);
        shape.setColor(-16777216);
        shape.setCornerRadius(50.0f);
        view.setBackgroundDrawable(shape);
        toast.setGravity(49, 0, 64);
        @SuppressLint("ResourceType") TextView messageTextView = (TextView) toast.getView().findViewById(16908299);
        messageTextView.setPadding(15, 2, 15, 2);
        messageTextView.setTextColor(-1);
        messageTextView.setTextSize(16.0f);
        toast.show();
    }

    @SuppressLint("WrongConstant")
    public static void showOnBottom(Context mContext, String message) {
        @SuppressLint("WrongConstant") Toast toast = Toast.makeText(mContext, message, 0);
        View view = toast.getView();
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(0);
        shape.setColor(-16777216);
        shape.setCornerRadius(50.0f);
        view.setBackgroundDrawable(shape);
        toast.setGravity(81, 0, 30);
        @SuppressLint("ResourceType") TextView messageTextView = (TextView) toast.getView().findViewById(16908299);
        messageTextView.setPadding(15, 2, 15, 2);
        messageTextView.setTextColor(-1);
        messageTextView.setTextSize(16.0f);
        toast.show();
    }
}
