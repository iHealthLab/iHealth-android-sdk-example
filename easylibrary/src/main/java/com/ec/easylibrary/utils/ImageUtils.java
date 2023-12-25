package com.ec.easylibrary.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * 类描述：  对于图片的处理类
 * 创建人：  wj
 * 创建时间：2016/11/8 16:28
 */
public class ImageUtils {

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }

        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equalsIgnoreCase(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    path = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equalsIgnoreCase(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(docId));
                    path = getImagePath(context, contentUri, null);
                }
            } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                path = getImagePath(context, uri, null);
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                path = uri.getPath();
            } else {
                path = uri.getPath();
            }
        } else {
            final String scheme = uri.getScheme();
            if (scheme == null)
                path = uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                path = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                path = getImagePath(context, uri, null);
            }
        }
        return path;
    }


    public static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getColumnIndex(MediaStore.Images.Media.DATA) != -1) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }

            }

            cursor.close();
        }
        return path;
    }
}
