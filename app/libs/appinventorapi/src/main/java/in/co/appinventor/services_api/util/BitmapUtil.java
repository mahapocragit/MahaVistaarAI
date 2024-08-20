package in.co.appinventor.services_api.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/* renamed from: in.co.appinventor.services_api.util.BitmapUtil */
public class BitmapUtil {
    public static Bitmap decodeSampledBitmapFromURI(String filePath, int w, int h) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        options.inSampleSize = calculateInSampleSize(options, w, h);
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        if (height <= reqHeight && width <= reqWidth) {
            return 1;
        }
        int heightRatio = Math.round(((float) height) / ((float) reqHeight));
        int widthRatio = Math.round(((float) width) / ((float) reqWidth));
        if (heightRatio < widthRatio) {
            return heightRatio;
        }
        return widthRatio;
    }

    public static Bitmap changeBitmapColor(Bitmap bm, int srcColor, int destColor) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bm.getPixel(x, y) == srcColor) {
                    bm.setPixel(x, y, destColor);
                }
            }
        }
        return bm;
    }

    public static Bitmap changeBitmapColorNonTrasnparent(Bitmap src, int destColor) {
        if (src == null) {
            return null;
        }
        Bitmap bm = src.copy(Bitmap.Config.ARGB_8888, true);
        int width = bm.getWidth();
        int height = bm.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bm.getPixel(x, y) != Color.argb(0, 0, 0, 0)) {
                    bm.setPixel(x, y, destColor);
                }
            }
        }
        return bm;
    }
}
