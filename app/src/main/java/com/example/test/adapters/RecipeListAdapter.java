package com.example.test.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.Article;
import com.example.test.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private List<Article> articleList;
    private Context context;

    public void setArticleList(List<Article> articleList) {
        this.articleList = new ArrayList<>();
        this.articleList.addAll(articleList);
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void sortByFollow(DatabaseHelper dbHelper) {
        articleList.sort(new Comparator<Article>() {
            @Override
            public int compare(Article article1, Article article2) {
                int follow1 = dbHelper.getTotalFollowCount(article1.getPublisher());
                int follow2 = dbHelper.getTotalFollowCount(article2.getPublisher());
                if (follow1 > follow2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        notifyDataSetChanged();
    }

    public void sortByReact() {
        articleList.sort(new Comparator<Article>() {
            @Override
            public int compare(Article article1, Article article2) {
                if (article1.getLikes() < article2.getLikes()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        notifyDataSetChanged();
    }

    public void sortByPublishedTime() {
        articleList.sort(new Comparator<Article>() {
            @Override
            public int compare(Article article1, Article article2) {
                String[] a1 = article1.getPublishedTime().split(" ");
                String[] a2 = article2.getPublishedTime().split(" ");
                String date1 = a1[0], time1 = a1[1], date2 = a2[0], time2 = a2[0];
                if (date1.compareTo(date2) < 0) {
                    return 1;
                } else if (date1.compareTo(date2) > 0) {
                    return -1;
                } else {
                    if (time1.compareTo(time2) < 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });
        notifyDataSetChanged();
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
        holder.dish_img.setImageResource(R.drawable.beefsteak);
        holder.react_count.setText(String.valueOf(articleList.get(position).getLikes()));
        holder.cmt_count.setText(String.valueOf(articleList.get(position).getComments()));
        DatabaseHelper dbHelper = new DatabaseHelper(context);
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
}

class RecipeViewHolder extends RecyclerView.ViewHolder {
    Article article;
    LinearLayout recipe_post;
    TextView dish_name, user_name, cmt_count, react_count;
    ImageView dish_img, user_avatar, bookmark;
    boolean isBookmark;
    Context context;
    DatabaseHelper dbHelper;

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

        //default value
        user_avatar.setImageResource(R.drawable.user_avatar);
        
        recipe_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("articleID", String.valueOf(article.getId()));
                args.putString("dish_name", article.getDishName());
                args.putString("recipe_content", article.getRecipe());
                args.putString("ingredients", article.getIngredients());
                args.putString("publisher", article.getPublisher());
                args.putString("publishedDate", article.getPublishedTime());
                args.putString("time_to_make", article.getTimeToMake());
                args.putString("reacts", String.valueOf(article.getLikes()));
                args.putString("comments", String.valueOf(article.getComments()));
                args.putString("imageURL", article.getImgURL());

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