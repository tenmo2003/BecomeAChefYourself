package com.example.test.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    Context context;
    List<String> dish_names;

    public RecipeListAdapter(Context context) {
        this.context = context;
        this.dish_names = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.dish_name.setText(dish_names.get(position));
        holder.dish_img.setImageResource(R.drawable.beefsteak);
    }

    @Override
    public int getItemCount() {
        return dish_names.size();
    }
}

class RecipeViewHolder extends RecyclerView.ViewHolder {
    CardView recipe_post;
    TextView dish_name;
    ImageView dish_img;
    public RecipeViewHolder(View itemView) {
        super(itemView);
        recipe_post = itemView.findViewById(R.id.recipe_post);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_img = itemView.findViewById(R.id.dish_img);
    }
}