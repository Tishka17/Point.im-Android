package org.itishka.pointim;

import android.os.AsyncTask;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.CountingTypedFile;
import org.itishka.pointim.api.data.ImgurUploadResult;
import org.itishka.pointim.api.data.ImgurImage;

import java.io.File;
import java.net.SocketTimeoutException;

import retrofit.RetrofitError;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public abstract class ImgurUploadTask extends AsyncTask<String, Integer, ImgurUploadResult> {
    private CountingTypedFile.ProgressListener listener;
    private String filePath;
    private final String fileType;


    public ImgurUploadTask(String filePath, String fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
    }


    @Override
    protected ImgurUploadResult doInBackground(String... params) {
        File file = new File(filePath);
        final long totalSize = file.length();
        listener = new CountingTypedFile.ProgressListener() {
            @Override
            public void transferred(long num) {
                publishProgress((int) ((num / (float) totalSize) * 100));
            }
        };
        try {
            return ConnectionManager.getInstance().imgurService.uploadFile(new CountingTypedFile(fileType, file, listener));
        } catch (RetrofitError e) {
            e.printStackTrace();
            return null;
        }
    }
}