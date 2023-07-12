package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.Random;

public class RecommendRecipeAdapter extends RecyclerView.Adapter<RecommendRecipeViewHolder> {

    @NonNull
    @Override
    public RecommendRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendRecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendRecipeViewHolder holder, int position) {
        Random generator = new Random();
        if (generator.nextBoolean()) {
            holder.rcm_img.setImageResource(R.drawable.rcm_img1);
        } else {
            holder.rcm_img.setImageResource(R.drawable.rcm_img2);
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}

class RecommendRecipeViewHolder extends RecyclerView.ViewHolder {
    ImageView rcm_img;
    public RecommendRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        rcm_img = itemView.findViewById(R.id.recommend_recipe_img);
    }
}