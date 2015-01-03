package org.itishka.pointim.api.data;

import java.util.List;

/**
 * Created by atikhonov on 28.04.2014.
 */
public class PostList extends PointResult {
    public boolean has_next;
    public List<Post> posts;

    public void append(PostList newPosts) {
        has_next = newPosts.has_next;
        posts.addAll(newPosts.posts);
    }
}
