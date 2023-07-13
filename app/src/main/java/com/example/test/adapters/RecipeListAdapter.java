package com.example.test.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.components.Article;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    List<String> dish_names;
    List<Article> articleList;

    public void setDish_names(List<String> _dish_names) {
        dish_names = _dish_names;
        notifyDataSetChanged();
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
        notifyDataSetChanged();
    }

    public List<String> getDish_names() {
        return dish_names;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
//        holder.dish_name.setText(dish_names.get(position));
        holder.article = articleList.get(position);
        holder.dish_name.setText(articleList.get(position).getDishName());
        holder.dish_img.setImageResource(R.drawable.beefsteak);
    }

    @Override
    public int getItemCount() {
        return dish_names.size();
    }
}

class RecipeViewHolder extends RecyclerView.ViewHolder {
    Article article;
    LinearLayout recipe_post;
    TextView dish_name;
    ImageView dish_img;
    public RecipeViewHolder(View itemView) {
        super(itemView);
        recipe_post = itemView.findViewById(R.id.recipe_post);
        recipe_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
//                args.putString("dish_name", (String) dish_name.getText());
//                args.putString("recipe_content", "recipeContent");
                args.putString("dish_name", article.getDishName());
                args.putString("recipe_content", article.getContent());
                args.putString("publisher", article.getPublisher());
                args.putString("publishedDate", article.getPublishedTime());

                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_img = itemView.findViewById(R.id.dish_img);
    }
}