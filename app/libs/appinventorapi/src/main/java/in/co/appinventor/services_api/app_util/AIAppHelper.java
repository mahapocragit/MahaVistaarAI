package in.co.appinventor.services_api.app_util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* renamed from: in.co.appinventor.services_api.app_util.AIAppHelper */
public class AIAppHelper {
    public static final String ASSET_PATH = "file:///android_asset/";
    private static final AIAppHelper ourInstance = new AIAppHelper();

    public static AIAppHelper getInstance() {
        return ourInstance;
    }

    private AIAppHelper() {
    }

    public byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                return out.toByteArray();
            }
            out.write(buffer, 0, count);
        }
    }

    public Bitmap takeScreenshot(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public String saveBitmap(Bitmap bitmap, File imagePath) {
        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e2) {
            Log.e("GREC", e2.getMessage(), e2);
        }
        return imagePath.getAbsolutePath();
    }

    public Bitmap getScreenshotBitmap(View recyclerView) {
        recyclerView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        Bitmap bitmap = Bitmap.createBitmap(recyclerView.getWidth(), recyclerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        recyclerView.draw(new Canvas(bitmap));
        return bitmap;
    }

    public File createImageFile(File storageDir) throws IOException {
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile("_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_", ".jpg", storageDir);
    }
}
