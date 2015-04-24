package org.itishka.pointim.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.squareup.picasso.Picasso;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.SinglePostActivity;
import org.itishka.pointim.activities.TagViewActivity;
import org.itishka.pointim.activities.UserViewActivity;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.Comment;
import org.itishka.pointim.model.ExtendedPost;
import org.itishka.pointim.utils.ImageSearchHelper;
import org.itishka.pointim.utils.Utils;
import org.itishka.pointim.widgets.ImageList;

import java.lang.ref.WeakReference;

/**
 * Created by Tishka17 on 20.10.2014.
 */
public class SinglePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final WeakReference<Context> mContext;
    private View.OnClickListener mOnTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), TagViewActivity.class);
            intent.putExtra("tag", ((TextView) view).getText());
            getContext().startActivity(intent);
        }
    };
    private ExtendedPost mPost = null;
    private ImageSearchTask mTask;
    private OnCommentClickListener mOnCommentClickListener;

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
            final View v = LayoutInflater.from(viewGroup.getContext())
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnCommentClickListener != null) {
                        mOnCommentClickListener.onPostClicked(v);
                    }
                }
            });
            return holder;
        } else {
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comment, viewGroup, false);
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
            holder.action_recommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnCommentClickListener != null) {
                        mOnCommentClickListener.onRecommendCommentClicked(v, view.getTag(R.id.comment_id).toString());
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnCommentClickListener != null) {
                        mOnCommentClickListener.onCommentClicked(v, view.getTag(R.id.comment_id).toString());
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
            commentHolder.itemView.setTag(R.id.comment_id, comment.id);
            if (ConnectionManager.getInstance().loginResult.login.equalsIgnoreCase(comment.author.login)) {
                commentHolder.action_recommend.setVisibility(View.GONE);
            } else {
                commentHolder.action_recommend.setTag(R.id.comment_id, comment.id);
                commentHolder.action_recommend.setVisibility(View.VISIBLE);
            }
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
        final TextView text;
        final ViewGroup tags;
        final ImageView avatar;
        final TextView author;
        final TextView post_id;
        final TextView comments;
        final TextView date;
        final ImageView webLink;
        final CheckBox favourite;
        final View mainContent;
        final ImageList imageList;

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
            mainContent = itemView.findViewById(R.id.main_content);
            imageList = (ImageList) itemView.findViewById(R.id.imageList);
        }
    }

    protected class CommentViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final ImageView avatar;
        final TextView author;
        final TextView date;
        final View divider;
        final TextView comment_id;
        final ImageList imageList;
        final View action_recommend;

        public CommentViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            divider = itemView.findViewById(R.id.divider);
            comment_id = (TextView) itemView.findViewById(R.id.comment_id);
            imageList = (ImageList) itemView.findViewById(R.id.imageList);
            action_recommend = itemView.findViewById(R.id.action_recommend);
        }
    }

    private class ImageSearchTask extends AsyncTask<ExtendedPost, Integer, Void> {
        SharedPreferences prefs;
        boolean loadImages;

        @Override
        protected Void doInBackground(ExtendedPost... posts) {
            ExtendedPost post = posts[0];
            if (post.post.text.images == null) {
                post.post.text.images = ImageSearchHelper.checkImageLinks(ImageSearchHelper.getAllLinks(post.post.text.text));
                publishProgress(0);
            }
            if (post.comments != null) {
                for (int i = 0; i < post.comments.size(); i++) {
                    Comment comment = post.comments.get(i);
                    if (comment.text.images == null) {
                        comment.text.images = ImageSearchHelper.checkImageLinks(ImageSearchHelper.getAllLinks(comment.text.text));
                        publishProgress(i);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            loadImages = prefs.getBoolean("loadImages", true);
            if (!loadImages) cancel(true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            notifyItemChanged(values[0] + 1);
            super.onProgressUpdate(values);
        }
    }

    public static interface OnCommentClickListener {
        public void onCommentClicked(View view, String commentId);

        public void onRecommendCommentClicked(View view, String commentId);

        public void onPostClicked(View view);
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }
}
