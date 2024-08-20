package in.co.appinventor.services_api.widget;

import android.app.Dialog;
import android.content.Context;


/* renamed from: in.co.appinventor.services_api.widget.UIProgressHUD */
public class UIProgressHUD extends Dialog {
    private UIProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    private UIProgressHUD(Context context) {
        super(context);
    }

   /* @NotNull
    public static UIProgressHUD show(Context context, boolean cancelable, OnCancelListener cancelListener, boolean isVisible) {
        UIProgressHUD dialog = new UIProgressHUD(context);
        try {
            dialog.setTitle("");
            dialog.setContentView(layout.progress_hud);
            dialog.setCancelable(cancelable);
            dialog.setOnCancelListener(cancelListener);
            dialog.getWindow().getAttributes().gravity = 17;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.2f;
            dialog.getWindow().setAttributes(lp);
            if (isVisible) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        findViewById(R.id.progress_wheel).setVisibility(0);
    }*/
}
