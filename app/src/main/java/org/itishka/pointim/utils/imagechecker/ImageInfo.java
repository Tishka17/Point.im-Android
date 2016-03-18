package org.itishka.pointim.utils.imagechecker;

import android.net.Uri;

/**
 * Created by Tishka17 on 18.03.2016.
 */
public class ImageInfo {
    public static final String MIME_ERROR = "^";

    public Uri image;
    public Uri thumbnail;
    public String mime;

    public boolean isImage() {
        return mime != null && mime.startsWith("image/");
    }
}
