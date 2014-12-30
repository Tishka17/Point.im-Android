package org.itishka.pointim;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.CountingTypedFile;
import org.itishka.pointim.api.data.ImgurBaseResponse;
import org.itishka.pointim.api.data.ImgurImage;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public abstract class ImgurUploadTask extends AsyncTask<String, Integer, ImgurImage> {
    private CountingTypedFile.ProgressListener listener;
    private String filePath;
    private final String fileType;


    public ImgurUploadTask(String filePath, String fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
    }


    @Override
    protected ImgurImage doInBackground(String... params) {
        File file = new File(filePath);
        final long totalSize = file.length();
        listener = new CountingTypedFile.ProgressListener() {
            @Override
            public void transferred(long num) {
                publishProgress((int) ((num / (float) totalSize) * 100));
            }
        };
        ImgurBaseResponse response = ConnectionManager.getInstance().imgurService.uploadFile(new CountingTypedFile(fileType, file, listener));
        return ConnectionManager.getInstance().imgurService.getImageInfo(response.data);
    }
}