package org.itishka.pointim.api.data;

import android.text.Spannable;

import java.util.Date;

public class Comment {
	public AuthorData author;
	public TextWithImages text;
	public Date created;
	public String id;
	public String post_id;
	public String to_comment_id;
	public boolean is_rec;
}
