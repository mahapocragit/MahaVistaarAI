package in.co.appinventor.services_api.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/* renamed from: in.co.appinventor.services_api.widget.ExpandableHeightListView */
public class ExpandableHeightListView extends ListView {
    boolean expanded = true;

    public ExpandableHeightListView(Context context) {
        super(context);
    }

    public ExpandableHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeightListView(Context context, AttributeSet attrs, int defStyle) {
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
