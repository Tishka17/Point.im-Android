package org.itishka.pointim.listeners;

import org.itishka.pointim.model.point.PostData;

/**
 * Created by Tishka17 on 04.05.2016.
 */
public interface OnPostChangedListener {
    void onChanged(PostData post);

    void onDeleted(PostData post);
}
