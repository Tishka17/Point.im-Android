package org.itishka.pointim.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.pnikosis.materialishprogress.ProgressWheel;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.UserViewActivity;
import org.itishka.pointim.utils.Utils;
import org.itishka.pointim.api.ImageSearchHelper;
import org.itishka.pointim.api.data.Post;
import org.itishka.pointim.api.data.PostList;
import org.itishka.pointim.widgets.ImageList;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Tishka17 on 20.10.2014.
 */
public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_ITEM = 0;
    private PostList mPostList = null;
    private final WeakReference<Context> mContext;
    private ImageSearchTask mTask;

    public Post getItem(int pos) {
        if (pos == mPostList.posts.size())
            return null;
        return mPostList.posts.get(pos);
    }

    public PostList getPostList() {
        return mPostList;
    }

    protected class FooterHolder extends RecyclerView.ViewHolder {
        ProgressWheel progressWheel;

        public FooterHolder(View itemView) {
            super(itemView);
            progressWheel = (ProgressWheel) itemView.findViewById(R.id.progress_wheel);
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewGroup tags;
        ImageView avatar;
        ImageView recommender_avatar;
        TextView recommend_text;
        View quote_mark;
        View quote_mark_top;
        TextView recommend_author;
        TextView author;
        TextView post_id;
        View recommend_info;
        TextView recommend_id;
        TextView comments;
        TextView date;
        ImageView webLink;
        CheckBox favourite;
        ImageList imageList;
        View mainConent;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            tags = (ViewGroup) itemView.findViewById(R.id.tags);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            recommender_avatar = (ImageView) itemView.findViewById(R.id.recommend_avatar);
            recommend_text = (TextView) itemView.findViewById(R.id.recommend_text);
            quote_mark = itemView.findViewById(R.id.quote_mark);
            quote_mark_top = itemView.findViewById(R.id.quote_mark_top);
            recommend_author = (TextView) itemView.findViewById(R.id.recommend_author);
            author = (TextView) itemView.findViewById(R.id.author);
            post_id = (TextView) itemView.findViewById(R.id.post_id);
            recommend_info = itemView.findViewById(R.id.recommend_info);
            recommend_id = (TextView) itemView.findViewById(R.id.recommend_id);
            comments = (TextView) itemView.findViewById(R.id.comments);
            date = (TextView) itemView.findViewById(R.id.date);
            webLink = (ImageView) itemView.findViewById(R.id.weblink);
            favourite = (CheckBox) itemView.findViewById(R.id.favourite);
            mainConent = itemView.findViewById(R.id.main_content);
            imageList = (ImageList) itemView.findViewById(R.id.imageList);
        }
    }

    public void setData(PostList postList) {
        mPostList = postList;
        notifyDataSetChanged();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
        }
        mTask = new ImageSearchTask();
        mTask.execute(mPostList);
    }

    public void appendData(PostList postList) {
        int oldLength = mPostList.posts.size();
        mPostList.append(postList);
        notifyItemRangeInserted(oldLength, postList.posts.size());
        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTask = new ImageSearchTask();
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostList);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mPostList.posts.size())
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    public PostListAdapter(Context context) {
        super();
        mContext = new WeakReference<>(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewTYpe) {
        if (viewTYpe == TYPE_FOOTER) {
            final View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_footer, viewGroup, false);
            return new FooterHolder(v);
        } else {
            final View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_post, viewGroup, false);
            final ViewHolder holder = new ViewHolder(v);
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnPostClickListener != null) {
                        mOnPostClickListener.onPostClicked(v, view.getTag(R.id.post_id).toString());
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
        if (i == mPostList.posts.size()) {
            FooterHolder footerHolder = (FooterHolder) holder;
            if (mPostList.has_next) {
                footerHolder.progressWheel.setVisibility(View.VISIBLE);
                if (mOnLoadMoreRequestListener != null) {
                    if (!mOnLoadMoreRequestListener.onLoadMoreRequested()) {
                        footerHolder.progressWheel.setVisibility(View.GONE);
                    }
                }
            } else {
                footerHolder.progressWheel.setVisibility(View.GONE);
            }
        } else {
            onBindItemViewHolder((ViewHolder) holder, i);
        }
    }

    public void onBindItemViewHolder(PostListAdapter.ViewHolder holder, int i) {
        Post post = mPostList.posts.get(i);
        holder.author.setText("@" + post.post.author.login);
        holder.itemView.setTag(R.id.post_id, post.post.id);

        holder.imageList.setImageUrls(post.post.text.images);
        holder.text.setText(post.post.text.text);
        Utils.showAvatar(getContext(), post.post.author.login, post.post.author.avatar, holder.avatar);
        holder.date.setText(Utils.formatDate(post.post.created));

        if (post.rec != null) {
            holder.mainConent.setBackgroundColor(getContext().getResources().getColor(R.color.quote_background));
            holder.recommend_info.setVisibility(View.VISIBLE);
            if (post.rec.text != null) {
                holder.recommend_text.setVisibility(View.VISIBLE);
                holder.recommend_text.setText(post.rec.text.text);
            } else {
                holder.recommend_text.setVisibility(View.GONE);
            }
            holder.quote_mark.setVisibility(View.VISIBLE);
            holder.quote_mark_top.setVisibility(View.VISIBLE);
            holder.recommend_author.setText("@" + post.rec.author.login);
            holder.recommend_id.setText("");
            Utils.showAvatar(getContext(), post.post.author.login, post.rec.author.avatar, holder.recommender_avatar);
        } else {
            holder.mainConent.setBackgroundColor(Color.TRANSPARENT);
            holder.recommend_info.setVisibility(View.GONE);
            holder.recommend_text.setVisibility(View.GONE);
            holder.quote_mark.setVisibility(View.INVISIBLE);
            holder.quote_mark_top.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(post.comment_id)) {
            holder.post_id.setText("#" + post.post.id);
        } else {
            holder.post_id.setText("#" + post.post.id + "/" + post.comment_id);
        }
        holder.post_id.setTag(post.post.id);
        holder.webLink.setTag(Utils.getnerateSiteUri(post.post.id));
        holder.favourite.setChecked(post.bookmarked);
        holder.favourite.setTag(post.post.id);

        if (post.post.comments_count > 0) {
            holder.comments.setText(String.valueOf(post.post.comments_count));
            //holder.comments.setVisibility(View.VISIBLE);
        } else {
            holder.comments.setText("");
            // holder.comments.setVisibility(View.GONE);
        }
        LayoutInflater li;
        li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder.tags.removeAllViews();
        if (post.post.tags==null || post.post.tags.size() == 0) {
            holder.tags.setVisibility(View.GONE);
        } else {
            holder.tags.setVisibility(View.VISIBLE);

            int n = 0;
            for (String tag : post.post.tags) {
                final TextView v = (TextView) li.inflate(R.layout.tag, null);
                v.setText(tag);
                holder.tags.addView(v, n++, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                v.setOnClickListener(mOnTagClickListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mPostList == null) return 0;
        else return mPostList.posts.size() + 1;
    }

    View.OnClickListener mOnTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOnPostClickListener != null)
                mOnPostClickListener.onTagClicked(view, ((TextView) view).getText().toString());
        }
    };

    public interface OnLoadMoreRequestListener {
        public boolean onLoadMoreRequested();//return false if cannot load more
    }

    private OnLoadMoreRequestListener mOnLoadMoreRequestListener = null;

    public void setOnLoadMoreRequestListener(OnLoadMoreRequestListener onLoadMoreRequestListener) {
        mOnLoadMoreRequestListener = onLoadMoreRequestListener;
    }

    public interface OnPostClickListener {
        public void onPostClicked(View view, String post);

        public void onTagClicked(View view, String tag);
    }

    private OnPostClickListener mOnPostClickListener = null;

    public void setOnPostClickListener(OnPostClickListener onPostClickListener) {
        mOnPostClickListener = onPostClickListener;
    }

    private class ImageSearchTask extends AsyncTask<PostList, Integer, Void> {

        @Override
        protected Void doInBackground(PostList... postLists) {
            List<Post> posts = postLists[0].posts;
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                if (post.post.text.images == null) {
                    post.post.text.images = ImageSearchHelper.checkImageLinks(getContext(), ImageSearchHelper.getAllLinks(post.post.text.text));
                    publishProgress(i);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            notifyItemChanged(values[0]);
            super.onProgressUpdate(values);
        }
    }
}
