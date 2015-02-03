package org.itishka.pointim.utils;

import org.itishka.pointim.model.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tishka17 on 12.01.2015.
 */
public class PointHelper {
    public static List<Tag> removeDublicates(Collection<Tag> tags) {
        List<Tag> filteredTags = new ArrayList<>(tags.size());
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
}
