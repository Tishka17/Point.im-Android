package org.itishka.pointim.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.ImgurUploadResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public abstract class ImgurUploadTask extends AsyncTask<String, Integer, ImgurUploadResult> {
    private final Uri mUri;
    private File mFile;
    private Context mContext;
    private String mMime;


    public ImgurUploadTask(Context context, Uri uri, String mime) {
        mUri = uri;
        mMime = mime;
        try {
            mFile = File.createTempFile("upload_", "", context.getCacheDir());
        } catch (IOException e) {
            mFile = null;
            e.printStackTrace();
        }
        mContext = context;
    }


    @Override
    protected ImgurUploadResult doInBackground(String... params) {
        String[] filePathColumn = {MediaStore.Images.Media.MIME_TYPE};
        String imageMime = mMime;
        if (imageMime==null) {
            Cursor cursor = mContext.getContentResolver().query(mUri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                imageMime = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            }
            if (cursor != null) cursor.close();
        }
        if (imageMime==null) {
            imageMime = "image/other";
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
            mFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(mFile);
            if ("image/png".equals(imageMime)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        mContext = null;
        final long totalSize = mFile.length();
        try {
            return ConnectionManager.getInstance().imgurService.uploadFile(new CountingTypedFile(imageMime, mFile, new CountingTypedFile.ProgressListener() {
                @Override
                public void transferred(long num) {
                    publishProgress((int) ((num / (float) totalSize) * 100));
                }
            }));
        } catch (RetrofitError e) {
            e.printStackTrace();
            return null;
        }
    }
}