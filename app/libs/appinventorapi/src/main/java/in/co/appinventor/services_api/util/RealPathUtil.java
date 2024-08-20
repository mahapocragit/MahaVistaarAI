package in.co.appinventor.services_api.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/* renamed from: in.co.appinventor.services_api.util.RealPathUtil */
public class RealPathUtil {
    @SuppressLint({"NewApi"})
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        try {
            String id = DocumentsContract.getDocumentId(uri).split(":")[1];
            String[] column = {"_data"};
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, "_id=?", new String[]{id}, (String) null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } catch (IllegalArgumentException e) {
            Cursor cursor2 = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor2 == null) {
                return uri.getPath();
            }
            cursor2.moveToFirst();
            return cursor2.getString(cursor2.getColumnIndex("_data"));
        }
    }

    @SuppressLint({"NewApi"})
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        Cursor cursor = new CursorLoader(context, contentUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null).loadInBackground();
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getRealPath(Context mContext, Intent data) {
        if (Build.VERSION.SDK_INT < 11) {
            return getRealPathFromURI_BelowAPI11(mContext, data.getData());
        }
        if (Build.VERSION.SDK_INT < 19) {
            return getRealPathFromURI_API11to18(mContext, data.getData());
        }
        return getRealPathFromURI_API19(mContext, data.getData());
    }
}
