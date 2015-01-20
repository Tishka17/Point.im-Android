package org.itishka.pointim.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.SinglePostActivity;
import org.itishka.pointim.activities.TagViewActivity;
import org.itishka.pointim.activities.UserViewActivity;
import org.itishka.pointim.api.data.Comment;
import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.utils.ImageSearchHelper;
import org.itishka.pointim.utils.Utils;
import org.itishka.pointim.widgets.ImageList;

import java.lang.ref.WeakReference;

/**
 * Created by Татьяна on 20.10.2014.
 */
public class SinglePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final WeakReference<Context> mContext;
    View.OnClickListener mOnTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), TagViewActivity.class);
            intent.putExtra("tag", ((TextView) view).getText());
            getContext().startActivity(intent);
        }
    };
    private ExtendedPost mPost = null;
    private ImageSearchTask mTask;

    public SinglePostAdapter(Context context) {
        super();
        mContext = new WeakReference<>(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    public Object getItem(int pos) {
        if (pos == 0)
            return mPost;
        else
            return mPost.comments.get(pos - 1);
    }

    public void setData(ExtendedPost post) {
        mPost = post;
        notifyDataSetChanged();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
        }
        mTask = new ImageSearchTask();
        mTask.execute(post);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.single_post_header, viewGroup, false);
            final PostViewHolder holder = new PostViewHolder(v);
            holder.webLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, (Uri) view.getTag());
                    getContext().startActivity(browserIntent);
                }
            });
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user = (String) view.getTag();
                    if (!TextUtils.isEmpty(user)) {
                        Intent intent = new Intent(view.getContext(), UserViewActivity.class);
                        intent.putExtra("user", user);
                        ActivityCompat.startActivity((Activity) view.getContext(), intent, null);
                    }
                }
            });
            holder.post_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(getContext(), SinglePostActivity.class);
                    browserIntent.putExtra("post", view.getTag().toString());
                    getContext().startActivity(browserIntent);
                }
            });
            return holder;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comment, viewGroup, false);
            CommentViewHolder holder = new CommentViewHolder(v);
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user = (String) view.getTag();
                    if (!TextUtils.isEmpty(user)) {
                        Intent intent = new Intent(view.getContext(), UserViewActivity.class);
                        intent.putExtra("user", user);
                        ActivityCompat.startActivity((Activity) view.getContext(), intent, null);
                    }
                }
            });
            return holder;
        }

    }

    private Context getContext() {
        return mContext.get();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (i == 0) {
            PostViewHolder postHolder = (PostViewHolder) holder;
            postHolder.author.setText("@" + mPost.post.author.login);
            postHolder.text.setText(mPost.post.text.text);
            postHolder.imageList.setImageUrls(mPost.post.text.images);
            Utils.showAvatar(getContext(), mPost.post.author.login, mPost.post.author.avatar, postHolder.avatar);
            postHolder.date.setText(Utils.formatDate(mPost.post.created));

            postHolder.post_id.setText("#" + mPost.post.id);
            postHolder.post_id.setTag(mPost.post.id);
            postHolder.webLink.setTag(Utils.getnerateSiteUri(mPost.post.id));
            //postHolder.favourite.setChecked(mPost.);
            //postHolder.favourite.setTag(mPost.post.id);

            if (mPost.post.comments_count > 0) {
                postHolder.comments.setText(String.valueOf(mPost.post.comments_count));
            } else {
                postHolder.comments.setText("");
            }
            postHolder.tags.removeAllViews();
            if (mPost.post.tags == null || mPost.post.tags.size() == 0) {
                postHolder.tags.setVisibility(View.GONE);
            } else {
                postHolder.tags.setVisibility(View.VISIBLE);
                int n = 0;
                for (String tag : mPost.post.tags) {
                    LayoutInflater li;
                    li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final TextView v = (TextView) li.inflate(R.layout.tag, null);
                    v.setText(tag);
                    postHolder.tags.addView(v, n++, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    v.setOnClickListener(mOnTagClickListener);
                }
            }
        } else {
            Comment comment = mPost.comments.get(i - 1);
            CommentViewHolder commentHolder = (CommentViewHolder) holder;
            Utils.showAvatar(getContext(), comment.author.login, comment.author.avatar, commentHolder.avatar);
            if (i == 1) {
                commentHolder.divider.setVisibility(View.INVISIBLE);
            } else {
                commentHolder.divider.setVisibility(View.VISIBLE);
            }
            commentHolder.date.setText(Utils.formatDate(comment.created));
            commentHolder.text.setText(comment.text.text);
            commentHolder.imageList.setImageUrls(comment.text.images);
            commentHolder.author.setText(comment.author.login);
            if (TextUtils.isEmpty(comment.to_comment_id))
                commentHolder.comment_id.setText("/" + comment.id);
            else
                commentHolder.comment_id.setText("/" + comment.id + " → " + "/" + comment.to_comment_id);
        }
    }

    @Override
    public int getItemCount() {
        if (mPost == null)
            return 0;
        else if (mPost.comments == null)
            return 1;
        else
            return mPost.comments.size() + 1;
    }

    protected class PostViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewGroup tags;
        ImageView avatar;
        TextView author;
        TextView post_id;
        TextView comments;
        TextView date;
        ImageView webLink;
        CheckBox favourite;
        View mainConent;
        ImageList imageList;

        public PostViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            tags = (ViewGroup) itemView.findViewById(R.id.tags);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            author = (TextView) itemView.findViewById(R.id.author);
            post_id = (TextView) itemView.findViewById(R.id.post_id);
            comments = (TextView) itemView.findViewById(R.id.comments);
            date = (TextView) itemView.findViewById(R.id.date);
            webLink = (ImageView) itemView.findViewById(R.id.weblink);
            favourite = (CheckBox) itemView.findViewById(R.id.favourite);
            mainConent = itemView.findViewById(R.id.main_content);
            imageList = (ImageList) itemView.findViewById(R.id.imageList);
        }
    }

    protected class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView avatar;
        TextView author;
        TextView date;
        View divider;
        TextView comment_id;
        ImageList imageList;

        public CommentViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            divider = itemView.findViewById(R.id.divider);
            comment_id = (TextView) itemView.findViewById(R.id.comment_id);
            imageList = (ImageList) itemView.findViewById(R.id.imageList);
        }
    }

    private class ImageSearchTask extends AsyncTask<ExtendedPost, Integer, Void> {

        @Override
        protected Void doInBackground(ExtendedPost... posts) {
            ExtendedPost post = posts[0];
            if (post.post.text.images == null) {
                post.post.text.images = ImageSearchHelper.checkImageLinks(getContext(), ImageSearchHelper.getAllLinks(post.post.text.text));
                publishProgress(0);
            }
            if (post.comments != null) {
                for (int i = 0; i < post.comments.size(); i++) {
                    Comment comment = post.comments.get(i);
                    if (comment.text.images == null) {
                        comment.text.images = ImageSearchHelper.checkImageLinks(getContext(), ImageSearchHelper.getAllLinks(comment.text.text));
                        publishProgress(i);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            notifyItemChanged(values[0] + 1);
            super.onProgressUpdate(values);
        }
    }
}
