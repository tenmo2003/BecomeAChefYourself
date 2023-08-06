package com.example.test.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.components.User;
import com.example.test.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private List<Article> articleList;
    private Context context;
    private boolean inHome;

    public void setInHome(boolean value) {
        inHome = value;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = new ArrayList<>();
        this.articleList.addAll(articleList);
        notifyDataSetChanged();
    }

    public void addToArticleList(List<Article> list) {
        articleList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateViewHolder(int position, int commentAdded, int likeAdded, boolean deleted) {
        if (deleted) {
            articleList.remove(position);
            notifyDataSetChanged();
            return;
        }
        Article article = articleList.get(position);
        article.setLikes(article.getLikes() + likeAdded);
        article.setComments(article.getComments() + commentAdded);

        notifyItemChanged(position);
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void sortByFollow() {
        MainActivity.runTask(() -> {
            articleList.sort(new Comparator<Article>() {
                @Override
                public int compare(Article article1, Article article2) {
                    AtomicInteger follow1 = new AtomicInteger();
                    AtomicInteger follow2 = new AtomicInteger();
                    MainActivity.runTask(() -> {
                        follow1.set(MainActivity.sqlConnection.getTotalFollowCount(article1.getPublisher()));
                        follow2.set(MainActivity.sqlConnection.getTotalFollowCount(article1.getPublisher()));
                    }, null, null);

                    return Integer.compare(follow2.get(), follow1.get());
                }
            });
        }, () -> {
            notifyDataSetChanged();
        }, MainActivity.progressDialog);
    }

    public void sortByReact() {
        MainActivity.runTask(() -> {
            articleList.sort(new Comparator<Article>() {
                @Override
                public int compare(Article article1, Article article2) {
                    return Integer.compare(article2.getLikes(), article1.getLikes());
                }
            });

        }, () -> {
            notifyDataSetChanged();
        }, MainActivity.progressDialog);
    }

    public void sortByPublishedTime() {
        MainActivity.runTask(() -> {
            articleList.sort(new Comparator<Article>() {
                @Override
                public int compare(Article article1, Article article2) {
                    return Integer.compare(article2.getId(), article1.getId());
                }
            });
        }, () -> {
            notifyDataSetChanged();

        }, MainActivity.progressDialog);

    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.article = articleList.get(position);
        holder.user_name.setText(articleList.get(position).getPublisher());
        holder.dish_name.setText(articleList.get(position).getDishName());

        if (articleList.get(position).getImgURL().equals("")) {
            holder.dish_img.setImageResource(R.drawable.no_preview);

        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(articleList.get(position).getImgURL()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).error(R.drawable.image_load_failed).into(holder.dish_img);
        }

        AtomicReference<User> user = new AtomicReference<>();
        MainActivity.runTask(() -> {
            user.set(MainActivity.sqlConnection.getUserWithUsername(holder.user_name.getText().toString()));
        }, () -> {
            if (!user.get().getAvatarURL().equals("")) {
                Glide.with(context).load(user.get().getAvatarURL()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.user_avatar);
            } else {
                holder.user_avatar.setImageResource(R.drawable.baseline_person_24);
            }
        }, null);


//        holder.dish_img.setImageResource(R.drawable.beefsteak);
        holder.react_count.setText(String.valueOf(articleList.get(position).getLikes()));
        holder.cmt_count.setText(String.valueOf(articleList.get(position).getComments()));
        if (inHome) {
            AtomicBoolean checkBookmarked = new AtomicBoolean(false);
            MainActivity.runTask(() -> {
                checkBookmarked.set(MainActivity.loggedInUser != null && MainActivity.sqlConnection.checkBookmarked(MainActivity.loggedInUser.getUsername(), articleList.get(position).getId()));
            }, () -> {
                holder.isBookmark = checkBookmarked.get();
                if (holder.isBookmark) {
                    holder.bookmark.setImageResource(R.drawable.bookmarked);
                } else {
                    holder.bookmark.setImageResource(R.drawable.bookmark);
                }
            }, null);
        } else {
            holder.bookmark.setVisibility(View.GONE);
        }

        holder.context = context;
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public long getItemId(int position) {
        return articleList.get(position).getId();
    }
}

class RecipeViewHolder extends RecyclerView.ViewHolder {
    Article article;
    LinearLayout recipe_post;
    TextView dish_name, user_name, cmt_count, react_count;
    ImageView dish_img, user_avatar, bookmark;
    boolean isBookmark;
    Context context;
    ProgressBar progressBar;

    public RecipeViewHolder(View itemView) {
        super(itemView);
        recipe_post = itemView.findViewById(R.id.recipe_post);
        dish_name = itemView.findViewById(R.id.dish_name);
        user_name = itemView.findViewById(R.id.user_name);
        user_avatar = itemView.findViewById(R.id.user_avatar);
        dish_img = itemView.findViewById(R.id.dish_img);
        cmt_count = itemView.findViewById(R.id.cmt_count);
        react_count = itemView.findViewById(R.id.react_count);
        bookmark = itemView.findViewById(R.id.bookmark);
        progressBar = itemView.findViewById(R.id.progressbar);

        //default value

        recipe_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.adapterPos = getAdapterPosition();
                Bundle args = new Bundle();
                args.putInt("articleID", article.getId());

                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    MainActivity.toast.setText("Please login first");
                    return;
                }

                if (!isBookmark) {
                    isBookmark = true;
                    MainActivity.runTask(() -> {
                        MainActivity.sqlConnection.addBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    });
                    bookmark.setImageResource(R.drawable.bookmarked);
                } else {
                    isBookmark = false;
                    MainActivity.runTask(() -> {
                        MainActivity.sqlConnection.removeBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    });
                    bookmark.setImageResource(R.drawable.bookmark);
                }
            }
        });
    }
}

