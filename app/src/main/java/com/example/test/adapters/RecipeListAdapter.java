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
import com.example.test.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private List<Article> articleList;
    private Context context;

    public void setArticleList(List<Article> articleList) {
//        MainActivity.runTask(() -> {
//            this.articleList = new ArrayList<>();
//            this.articleList.addAll(articleList);
//            notifyDataSetChanged();
//        }, () -> {
//            notifyDataSetChanged();
//        }, MainActivity.progressDialog);
        this.articleList = new ArrayList<>();
        this.articleList.addAll(articleList);
        notifyDataSetChanged();
    }

    public void addToArticleList(List<Article> list) {
        articleList.addAll(list);
        notifyDataSetChanged();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void sortByFollow(DatabaseHelper dbHelper) {
        MainActivity.runTask(() -> {
            articleList.sort(new Comparator<Article>() {
                @Override
                public int compare(Article article1, Article article2) {
                    int follow1 = dbHelper.getTotalFollowCount(article1.getPublisher());
                    int follow2 = dbHelper.getTotalFollowCount(article2.getPublisher());
                    return Integer.compare(follow2, follow1);
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
                    String[] a1 = article1.getPublishedTime().split(" ");
                    String[] a2 = article2.getPublishedTime().split(" ");
                    String date1 = a1[0], time1 = a1[1], date2 = a2[0], time2 = a2[1];
                    if (date1.compareTo(date2) < 0) {
                        return 1;
                    } else if (date1.compareTo(date2) > 0) {
                        return -1;
                    } else {
                        return Integer.compare(0, time1.compareTo(time2));
                    }
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
//        final Bitmap[] mIcon_val = new Bitmap[1]; // declare as final array to make it modifiable inside Runnable
//
//        MainActivity.runTask(() -> {
//            URL newurl = null;
//            try {
//                newurl = new URL(articleList.get(position).getImgURL());
//            } catch (MalformedURLException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                mIcon_val[0] = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }, () -> {
//            holder.progressBar.setVisibility(View.GONE);
//            holder.dish_img.setImageBitmap(mIcon_val[0]);
//        }, null);
        if (articleList.get(position).getImgURL().equals("")) {
            holder.dish_img.setImageResource(R.drawable.no_preview);

        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(articleList.get(position).getImgURL()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.dish_img.setImageResource(R.drawable.no_preview);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.dish_img);
        }

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        User user = dbHelper.getUserWithUsername(holder.user_name.getText().toString());
        if (!user.getAvatarURL().equals("")) {
            Glide.with(context).load(user.getAvatarURL()).listener(new RequestListener<Drawable>() {
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
//        holder.dish_img.setImageResource(R.drawable.beefsteak);
        holder.react_count.setText(String.valueOf(articleList.get(position).getLikes()));
        holder.cmt_count.setText(String.valueOf(articleList.get(position).getComments()));
        holder.isBookmark = MainActivity.loggedInUser != null && dbHelper.checkBookmarked(MainActivity.loggedInUser.getUsername(), articleList.get(position).getId());
        if (holder.isBookmark) {
            holder.bookmark.setImageResource(R.drawable.bookmarked);
        } else {
            holder.bookmark.setImageResource(R.drawable.bookmark);
        }

        holder.dbHelper = dbHelper;
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
    DatabaseHelper dbHelper;
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
                Bundle args = new Bundle();
                args.putInt("articleID", article.getId());

                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isBookmark) {
                    isBookmark = true;
                    dbHelper.addBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    bookmark.setImageResource(R.drawable.bookmarked);
                } else {
                    isBookmark = false;
                    dbHelper.removeBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    bookmark.setImageResource(R.drawable.bookmark);
                }
            }
        });
    }
}