package com.example.test.ui.home;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.test.R;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.adapters.RecommendRecipeAdapter;
import com.example.test.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecipeListAdapter recipeListAdapter;
    RecommendRecipeAdapter recommendRecipeAdapter;
    RecyclerView recipeListView, recommendRecipeView;
    SearchView searchView;
    Timer timer;
    TimerTask timerTask;
    int position = Integer.MAX_VALUE / 2;
    List<String> list = Arrays.asList("Thịt bò", "Thịt gà", "Cơm rang",
            "Xôi", "Bánh cuốn", "Chả cá", "Canh rau", "Khoai tây", "Chuối", "Pizza");

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Recycler display recommend recipe
        recommendRecipeView = view.findViewById(R.id.recommend_recipe_list);
        recommendRecipeView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recommendRecipeAdapter = new RecommendRecipeAdapter();
        recommendRecipeView.setAdapter(recommendRecipeAdapter);

        recommendRecipeView.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recommendRecipeView);
        recommendRecipeView.smoothScrollBy(100, 0);

        //Recycler display grid recipe
        recipeListView = view.findViewById(R.id.recipe_list);
        recipeListView.setHasFixedSize(true);
        recipeListView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recipeListAdapter = new RecipeListAdapter();
        recipeListAdapter.setDish_names(list);
        recipeListView.setAdapter(recipeListAdapter);

        //Search bar
        searchView = view.findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });

        return view;
    }

    private void autoScroll() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    recommendRecipeView.scrollToPosition(position++);
                }
            };
            timer.schedule(timerTask, 3000, 3000);
        }
    }

    private void filterList(String text) {
        System.out.print(text);
        List<String> filteredList = new ArrayList<>();
        for (String dish_name: list) {
            if (dish_name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(dish_name);
            }
        }
        if (!filteredList.isEmpty()) {
            recipeListAdapter.setDish_names(filteredList);
        } else {
            recipeListAdapter.setDish_names(list);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}