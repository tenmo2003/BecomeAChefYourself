package com.example.test.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapters.RecipeListAdapter;

public class RecipeListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecipeListAdapter recipeListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        recyclerView = findViewById(R.id.recipe_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(RecipeListActivity.this, 2));
        recipeListAdapter = new RecipeListAdapter(getApplicationContext());
        recyclerView.setAdapter(recipeListAdapter);
    }
}