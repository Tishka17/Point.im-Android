package org.itishka.pointim.model;

import java.util.Date;
import java.util.List;

public class Comment {
    public User author;
    public TextWithImages text;
    public Date created;
    public String id;
    public String post_id;
    public String to_comment_id;
    public List<String> files;
    public boolean is_rec;
}
