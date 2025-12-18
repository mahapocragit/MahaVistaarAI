
package in.gov.mahapocra.mahavistaarai.chms;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.gov.mahapocra.mahavistaarai.sma.AppSession;


public class AIImageLoadingUtil {

    private Context mContext;
//    public Bitmap icon;

    public AIImageLoadingUtil(Context mContext) {
        this.mContext = mContext;
//        icon = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
    }

    public File createImageFile(String imageType)  {
       AppSession session = new AppSession(mContext);

        File storageDir;

        if (session.isOfflineMode()) {
            storageDir = session.getOfflineStorageDir();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        } else {
            storageDir = session.getOnlineStorageDir();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = imageType+"_"+ timeStamp + "_";

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  //* prefix *//*
                    ".jpg",   //* suffix *//*
                     storageDir     //* directory *//*
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    public File createImageFile2(String imageType)  {
        AppSession session = new AppSession(mContext);

        File storageDir;

        if (session.isOfflineMode()) {
            storageDir = session.getOfflineStorageDir();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        } else {
            storageDir = session.getOnlineStorageDir();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }

       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = imageType;

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  //* prefix *//*
                    ".jpg",   //* suffix *//*
                    storageDir     //* directory *//*
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

}
