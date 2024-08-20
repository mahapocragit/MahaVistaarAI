package in.co.appinventor.services_api.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

/* renamed from: in.co.appinventor.services_api.widget.UIInstantAutoComplete */
public class UIInstantAutoComplete extends AppCompatAutoCompleteTextView {
    public UIInstantAutoComplete(Context context) {
        super(context);
    }

    public UIInstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public UIInstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public boolean enoughToFilter() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        UIInstantAutoComplete.super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }
}
