package org.itishka.pointim.api.data;

import java.util.Date;

public class Comment {
	public AuthorData author;
	public String text;
	public Date created;
	public String id;
	public String post_id;
	public String to_comment_id;
	public boolean is_rec;
}
