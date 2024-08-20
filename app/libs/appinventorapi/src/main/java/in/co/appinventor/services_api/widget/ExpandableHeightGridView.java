package in.co.appinventor.services_api.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/* renamed from: in.co.appinventor.services_api.widget.ExpandableHeightGridView */
public class ExpandableHeightGridView extends GridView {
    boolean expanded = true;

    public ExpandableHeightGridView(Context context) {
        super(context);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded2) {
        this.expanded = expanded2;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isExpanded()) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(16777215, Integer.MIN_VALUE));
            getLayoutParams().height = getMeasuredHeight();
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
