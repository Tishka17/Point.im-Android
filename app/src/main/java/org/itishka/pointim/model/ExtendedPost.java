package org.itishka.pointim.model;

import java.util.List;

/**
 * Created by atikhonov on 28.04.2014.
 */
public class ExtendedPost extends PointResult {
    public List<Comment> comments;
    public PostData post;
    public boolean recommended; //is comming from server?
    public boolean editable; // is comming from server?
}
