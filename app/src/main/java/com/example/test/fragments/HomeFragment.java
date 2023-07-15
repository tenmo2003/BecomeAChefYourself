package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.test.R;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.adapters.RecommendRecipeAdapter;
import com.example.test.components.Article;
import com.example.test.database.DatabaseHelper;
import com.example.test.databinding.FragmentHomeBinding;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SearchView searchView;
    TextView all, most_follow, most_react, most_recent;
    RecipeListAdapter recipeListAdapter;
    RecommendRecipeAdapter recommendRecipeAdapter;
    RecyclerView recipeListView, recommendRecipeView;
    LinearLayoutManager rcmLLayoutManager;
    Timer timer;
    TimerTask timerTask;
    Handler timerHandler = new Handler();
    int position = Integer.MAX_VALUE / 2;
    List<Article> articlesList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        articlesList = dbHelper.getAllArticles();

        //Recycler display recommend recipe
        recommendRecipeView = view.findViewById(R.id.recommend_recipe_list);
        recommendRecipeView.setHasFixedSize(true);
        rcmLLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recommendRecipeView.setLayoutManager(rcmLLayoutManager);

        recommendRecipeAdapter = new RecommendRecipeAdapter();
        recommendRecipeView.setAdapter(recommendRecipeAdapter);

        recommendRecipeView.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recommendRecipeView);
        recommendRecipeView.smoothScrollBy(500, 0);

        recommendRecipeView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    stopAutoScroll();
                } else if (newState == 0) {
                    position = rcmLLayoutManager.findFirstCompletelyVisibleItemPosition();
                    autoScroll();
                }
            }
        });

        //Recycler display grid recipe
        recipeListView = view.findViewById(R.id.recipe_list);
        recipeListView.setHasFixedSize(true);
        recipeListView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recipeListAdapter = new RecipeListAdapter();
        recipeListAdapter.setArticleList(articlesList);
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
                searchArticle(s);
                return true;
            }
        });

        //Sort button
        all = view.findViewById(R.id.all);
        most_follow = view.findViewById(R.id.most_follow);
        most_react = view.findViewById(R.id.most_react);
        most_recent = view.findViewById(R.id.most_recent);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeListAdapter.setArticleList(articlesList);
            }
        });

        most_react.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeListAdapter.sortByReact();
            }
        });

        most_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeListAdapter.sortByPublishedTime();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        position = rcmLLayoutManager.findFirstCompletelyVisibleItemPosition();
        recommendRecipeView.scrollToPosition(position);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void autoScroll() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    timerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (position == -1) {
                                position = 1;
                            }
                            recommendRecipeView.smoothScrollToPosition(position);
                            position++;
                        }
                    });
                }
            };
            timer.schedule(timerTask, 5000, 5000);
        }
    }

    private void stopAutoScroll() {
        if (timer != null || timerTask != null) {
            if (timer != null)
                timer.cancel();
            if (timerTask != null)
                timerTask.cancel();
            timer = null;
            timerTask = null;
//            position = rcmLLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void searchArticle(String text) {
        List<Article> filteredList = new ArrayList<>();
        for (Article article: articlesList) {

            String dish_name = article.getDishName();
            //Remove accents from string
            dish_name = dish_name.toLowerCase();
            dish_name = Normalizer.normalize(dish_name, Normalizer.Form.NFD);
            dish_name = dish_name.replaceAll("[^\\p{ASCII}]", "");
            text = text.toLowerCase();
            text = Normalizer.normalize(text, Normalizer.Form.NFD);
            text = text.replaceAll("[^\\p{ASCII}]", "");

            if (dish_name.contains(text)) {
                filteredList.add(article);
            }
        }
        recipeListAdapter.setArticleList(filteredList);
    }
}