package in.co.appinventor.services_api.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/* renamed from: in.co.appinventor.services_api.widget.UISquareImageView */
public class UISquareImageView extends AppCompatImageView {
    public UISquareImageView(Context context) {
        super(context);
    }

    public UISquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UISquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        UISquareImageView.super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
