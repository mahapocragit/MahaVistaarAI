package in.co.appinventor.services_api.swipe;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/* renamed from: in.co.appinventor.services_api.swipe.OnSwipeTouchListener */
public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx) {
        this.gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    public boolean onTouch(View v, MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    /* renamed from: in.co.appinventor.services_api.swipe.OnSwipeTouchListener$GestureListener */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        private GestureListener() {
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > 100.0f && Math.abs(velocityX) > 100.0f) {
                        if (diffX > 0.0f) {
                            OnSwipeTouchListener.this.onSwipeRight();
                        } else {
                            OnSwipeTouchListener.this.onSwipeLeft();
                        }
                    }
                } else if (Math.abs(diffY) > 100.0f && Math.abs(velocityY) > 100.0f) {
                    if (diffY > 0.0f) {
                        OnSwipeTouchListener.this.onSwipeBottom();
                    } else {
                        OnSwipeTouchListener.this.onSwipeTop();
                    }
                }
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
    }
}
