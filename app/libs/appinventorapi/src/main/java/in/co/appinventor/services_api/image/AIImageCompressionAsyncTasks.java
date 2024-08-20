/*
 * Copyright (c) 2021. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 4/4/21 6:57 PM
 */

package in.co.appinventor.services_api.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import in.co.appinventor.services_api.listener.AIImgAsyncResponse;

public class AIImageCompressionAsyncTasks extends AsyncTask<String, Void, String> {
    private AIImageLoadingUtils mAIImageLoadingUtils;
    public AIImgAsyncResponse delegate = null;
    private File tempFilePath;

    public AIImageCompressionAsyncTasks(AIImageLoadingUtils mAIImageLoadingUtils, File tempFilePath, AIImgAsyncResponse asyncResponse) {
        this.mAIImageLoadingUtils = mAIImageLoadingUtils;
        this.tempFilePath = tempFilePath;
        this.delegate = asyncResponse;
    }

    protected String doInBackground(String... params) {
        String filePath = this.compressImage(params[0]);
        return filePath;
    }

    private String compressImage(String filePaths) {
        String filePath = this.tempFilePath.getAbsolutePath();
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 1280.0F;
        float maxWidth = 960.0F;
        float imgRatio = (float)(actualWidth / actualHeight);
        float maxRatio = maxWidth / maxHeight;
        if ((float)actualHeight > maxHeight || (float)actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / (float)actualHeight;
                actualWidth = (int)(imgRatio * (float)actualWidth);
                actualHeight = (int)maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / (float)actualWidth;
                actualHeight = (int)(imgRatio * (float)actualHeight);
                actualWidth = (int)maxWidth;
            } else {
                actualHeight = (int)maxHeight;
                actualWidth = (int)maxWidth;
            }
        }

        options.inSampleSize = this.mAIImageLoadingUtils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16384];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError var24) {
            var24.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError var23) {
            var23.printStackTrace();
        }

        float ratioX = (float)actualWidth / (float)options.outWidth;
        float ratioY = (float)actualHeight / (float)options.outHeight;
        float middleX = (float)actualWidth / 2.0F;
        float middleY = (float)actualHeight / 2.0F;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - (float)(bmp.getWidth() / 2), middleY - (float)(bmp.getHeight() / 2), new Paint(2));

        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt("Orientation", 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90.0F);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180.0F);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270.0F);
                Log.d("EXIF", "Exif: " + orientation);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException var22) {
            var22.printStackTrace();
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(filePath);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException var21) {
            var21.printStackTrace();
        }

        return filePath;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.delegate.asyncImgProcessFinish(result);
    }
}

