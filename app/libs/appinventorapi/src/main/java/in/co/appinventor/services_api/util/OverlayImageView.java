package in.co.appinventor.services_api.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/* renamed from: in.co.appinventor.services_api.util.OverlayImageView */
public class OverlayImageView extends AppCompatImageView {
    public OverlayImageView(Context context) {
        super(context);
    }

    public OverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPressed(boolean pressed) {
        OverlayImageView.super.setPressed(pressed);
        if (getDrawable() != null) {
            if (pressed) {
                getDrawable().setColorFilter(1140850688, PorterDuff.Mode.SRC_ATOP);
                invalidate();
                return;
            }
            getDrawable().clearColorFilter();
            invalidate();
        }
    }
}
