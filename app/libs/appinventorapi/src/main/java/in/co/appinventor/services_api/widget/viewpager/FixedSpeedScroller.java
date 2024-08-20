package in.co.appinventor.services_api.widget.viewpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/* renamed from: in.co.appinventor.services_api.widget.viewpager.FixedSpeedScroller */
public class FixedSpeedScroller extends Scroller {
    private int mDuration = 5000;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }

    public void setFixedDuration(int duration) {
        this.mDuration = duration;
    }
}
