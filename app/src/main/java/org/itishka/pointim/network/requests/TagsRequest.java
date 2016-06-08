package org.itishka.pointim.network.requests;

import org.itishka.pointim.model.point.Tag;
import org.itishka.pointim.model.point.TagList;

/**
 * Created by Tishka17 on 06.08.2015.
 */
public class TagsRequest  {

    private String mUserName;

    public static TagList removeDuplicates(TagList tags) {
        TagList filteredTags = new TagList();
        for (Tag tag : tags) {
            tag.tag = tag.tag.toLowerCase();
            boolean found = false;
            for (Tag t : filteredTags) {
                if (tag.tag.equals(t.tag)) {
                    found = true;
                    t.count += tag.count;
                    break;
                }
            }
            if (!found) {
                filteredTags.add(tag);
            }
        }
        return filteredTags;
    }

    public String getCacheName() {
        return getClass().getCanonicalName() + "-" + mUserName;
    }
}
