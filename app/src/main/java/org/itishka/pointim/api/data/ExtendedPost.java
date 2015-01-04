package org.itishka.pointim.api.data;

/**
 * Created by atikhonov on 28.04.2014.
 */
public class ExtendedPost extends PointResult {
    public Comment[] comments;
    public PostData post;
    public boolean recommended; //is comming from server?
}
