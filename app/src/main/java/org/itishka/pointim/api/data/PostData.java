package org.itishka.pointim.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PostData {
    enum Type {
        post,
        comment;
    }

    public String[] tags;
    public int comments_count;
    public AuthorData author;
    public TextWithImages text;
    public Date created;
    public Type type;
    public String id;

    @SerializedName("private")
    public boolean isPrivate;
}
