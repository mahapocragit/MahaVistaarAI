package in.co.appinventor.services_api.views;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: in.co.appinventor.services_api.views.AILinearLayoutManager */
public class AILinearLayoutManager extends LinearLayoutManager {
    public AILinearLayoutManager(Context context) {
        super(context);
    }

    public AILinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AILinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            private static final float MILLISECONDS_PER_INCH = 200.0f;

            public PointF computeScrollVectorForPosition(int targetPosition) {
                return AILinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            /* access modifiers changed from: protected */
            public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / ((float) displayMetrics.densityDpi);
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }
}
