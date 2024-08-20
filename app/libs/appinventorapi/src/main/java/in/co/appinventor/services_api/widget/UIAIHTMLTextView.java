package in.co.appinventor.services_api.widget;

import android.content.Context;
import android.text.Html;

import androidx.appcompat.widget.AppCompatTextView;

/* renamed from: in.co.appinventor.services_api.widget.UIAIHTMLTextView */
public class UIAIHTMLTextView extends AppCompatTextView {
    public UIAIHTMLTextView(Context context) {
        super(context);
        setText(Html.fromHtml(getText().toString()));
    }
}
