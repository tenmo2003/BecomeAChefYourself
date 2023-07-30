package com.example.test.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.test.components.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecommendRecipeAdapter extends RecyclerView.Adapter<RecommendRecipeViewHolder> {
    List<Article> recommendRecipeList;
    Context context;

    public void setRecommendRecipeList(List<Article> recommendRecipeList) {
        this.recommendRecipeList = new ArrayList<>(recommendRecipeList);
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
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
        if (_article.getImgURL().equals("")) {
            holder.rcm_img.setImageResource(R.drawable.no_preview);
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(_article.getImgURL()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.rcm_img.setImageResource(R.drawable.no_preview);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.rcm_img);
        }
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
    ProgressBar progressBar;

    public RecommendRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        rcm_img = itemView.findViewById(R.id.recommend_recipe_img);
        rcm_recipe_title = itemView.findViewById(R.id.recommend_recipe_title);
        progressBar = itemView.findViewById(R.id.progressbar);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("articleID", article.getId());

                Navigation.findNavController(itemView).navigate(R.id.navigation_article, args);
            }
        });
    }
}