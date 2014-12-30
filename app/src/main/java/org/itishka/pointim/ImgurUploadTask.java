package org.itishka.pointim;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.CountingTypedFile;
import org.itishka.pointim.api.data.ImgurUploadResult;
import org.itishka.pointim.api.data.ImgurImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit.RetrofitError;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public abstract class ImgurUploadTask extends AsyncTask<String, Integer, ImgurUploadResult> {
    private CountingTypedFile.ProgressListener listener;
    private final Uri mUri;
    private File mFile;
    private Context mContext;


    public ImgurUploadTask(Context context, Uri uri) {
        mUri = uri;
        try {
            mFile = File.createTempFile("upload_","", context.getCacheDir());
        } catch (IOException e) {
            mFile = null;
            e.printStackTrace();
        }
        mContext = context;
    }


    @Override
    protected ImgurUploadResult doInBackground(String... params) {
        String[] filePathColumn = {MediaStore.Images.Media.MIME_TYPE};
        Cursor cursor = mContext.getContentResolver().query(mUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        String imageMime = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
        cursor.close();

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
        listener = new CountingTypedFile.ProgressListener() {
            @Override
            public void transferred(long num) {
                publishProgress((int) ((num / (float) totalSize) * 100));
            }
        };
        try {
            return ConnectionManager.getInstance().imgurService.uploadFile(new CountingTypedFile(imageMime, mFile, listener));
        } catch (RetrofitError e) {
            e.printStackTrace();
            return null;
        }
    }
}