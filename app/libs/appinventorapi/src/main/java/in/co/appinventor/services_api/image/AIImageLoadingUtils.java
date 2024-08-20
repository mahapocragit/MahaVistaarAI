package in.co.appinventor.services_api.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* renamed from: in.co.appinventor.services_api.image.AIImageLoadingUtils */
public class AIImageLoadingUtils {
    private Context mContext;

    public AIImageLoadingUtils(Context mContext2) {
        this.mContext = mContext2;
    }

    private int convertDipToPixels(float dips) {
        return (int) TypedValue.applyDimension(1, dips, this.mContext.getResources().getDisplayMetrics());
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(((float) height) / ((float) reqHeight));
            int widthRatio = Math.round(((float) width) / ((float) reqWidth));
            if (heightRatio < widthRatio) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }
        }
        while (((float) (width * height)) / ((float) (inSampleSize * inSampleSize)) > ((float) (reqWidth * reqHeight * 2))) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public Bitmap decodeBitmapFromPath(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150.0f), convertDipToPixels(200.0f));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public float getFileIMGWidth(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        return (float) options.outWidth;
    }

    public float getFileIMGHeight(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        return (float) options.outHeight;
    }

    public File createImageFile(File storageDir) throws IOException {
        return File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_", ".jpg", storageDir);
    }
}
