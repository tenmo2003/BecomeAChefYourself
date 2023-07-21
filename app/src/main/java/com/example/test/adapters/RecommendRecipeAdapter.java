package com.example.test.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.components.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecommendRecipeAdapter extends RecyclerView.Adapter<RecommendRecipeViewHolder> {
    List<Article> recommendRecipeList;

    public void setRecommendRecipeList(List<Article> recommendRecipeList) {
        this.recommendRecipeList = new ArrayList<>(recommendRecipeList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendRecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendRecipeViewHolder holder, int position) {
        Article _article = recommendRecipeList.get(position % recommendRecipeList.size());
        holder.article = _article;
        holder.rcm_recipe_title.setText(_article.getDishName());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}

class RecommendRecipeViewHolder extends RecyclerView.ViewHolder {
    Article article;
    ImageView rcm_img;
    TextView rcm_recipe_title;

    public RecommendRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        rcm_img = itemView.findViewById(R.id.recommend_recipe_img);
        rcm_recipe_title = itemView.findViewById(R.id.recommend_recipe_title);

        Random generator = new Random();
        if (generator.nextBoolean()) {
            rcm_img.setImageResource(R.drawable.rcm_img1);
        } else {
            rcm_img.setImageResource(R.drawable.rcm_img2);
        }

        rcm_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("dish_name", article.getDishName());
                args.putString("recipe_content", article.getRecipe());
                args.putString("ingredients", article.getIngredients());
                args.putString("publisher", article.getPublisher());
                args.putString("publishedDate", article.getPublishedTime());
                args.putString("time_to_make", article.getTimeToMake());
                args.putString("reacts", String.valueOf(article.getLikes()));
                args.putString("comments", String.valueOf(article.getComments()));

                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });
    }
}