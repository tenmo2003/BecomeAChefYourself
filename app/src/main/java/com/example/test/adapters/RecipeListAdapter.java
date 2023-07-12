package com.example.test.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.ui.ArticleFragment;
import com.example.test.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    List<String> dish_names;

    public void setDish_names(List<String> _dish_names) {
        dish_names = _dish_names;
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
        holder.dish_name.setText(dish_names.get(position));
        holder.dish_img.setImageResource(R.drawable.beefsteak);
    }

    @Override
    public int getItemCount() {
        return dish_names.size();
    }
}

class RecipeViewHolder extends RecyclerView.ViewHolder {
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
                args.putString("dish_name", (String) dish_name.getText());
                args.putString("recipe_content", "recipeContent");
                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_img = itemView.findViewById(R.id.dish_img);
    }
}