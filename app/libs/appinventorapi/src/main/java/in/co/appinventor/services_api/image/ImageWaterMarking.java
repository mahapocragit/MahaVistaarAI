package in.co.appinventor.services_api.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageWaterMarking {

    private Context mContext;

    public ImageWaterMarking(Context mContext2) {
        this.mContext = mContext2;
    }

    public static Bitmap mark(Bitmap src, String watermark, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        String mFormat = new SimpleDateFormat("dd-MM-yy hh:mm EEEE").format(new Date());
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(mFormat, (w * 1) / 30,(h*14)/15, paint);

        return result;
    }

}
