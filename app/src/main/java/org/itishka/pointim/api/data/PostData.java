package org.itishka.pointim.api.data;

import java.util.Date;

import android.text.Spannable;
import android.text.SpannableString;

import com.google.gson.annotations.SerializedName;

public class PostData {
	enum Type {
		post,
		comment;
	}
	public String []tags;
	public int comments_count;
	public AuthorData author;
	public Spannable text;
	public Date created;
	public Type type;
	public String id;
	
	@SerializedName("private")
	public boolean isPrivate;
}
