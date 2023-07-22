package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

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
import com.example.test.utils.DatabaseHelper;
import com.example.test.databinding.FragmentHomeBinding;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SearchView searchView;
    HashMap<String, TextView> sortButtons = new HashMap<>();

    static RecipeListAdapter recipeListAdapter;
    static RecommendRecipeAdapter recommendRecipeAdapter;
    RecyclerView recipeListView, recommendRecipeView;
    LinearLayoutManager rcmLLayoutManager;
    Timer timer;
    TimerTask timerTask;
    Handler timerHandler = new Handler();
    int position = Integer.MAX_VALUE / 2;
    DatabaseHelper dbHelper;
    List<Article> articlesList, recommendRecipeList;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //if (articlesList == null) {
            dbHelper = new DatabaseHelper(getActivity());
//            articlesList = dbHelper.getNArticlesFromIndex(0, 10);
            articlesList = dbHelper.getAllArticles();
            recommendRecipeList = articlesList.subList(0, 5);

            recommendRecipeAdapter = new RecommendRecipeAdapter();
            recommendRecipeAdapter.setRecommendRecipeList(recommendRecipeList);

            recipeListAdapter = new RecipeListAdapter();
            recipeListAdapter.setArticleList(articlesList);
        //}

        //Recycler display recommend recipe
        recommendRecipeView = view.findViewById(R.id.recommend_recipe_list);
        recommendRecipeView.setHasFixedSize(true);
        rcmLLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recommendRecipeView.setLayoutManager(rcmLLayoutManager);
        recommendRecipeView.setAdapter(recommendRecipeAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recommendRecipeView);
        recommendRecipeView.scrollToPosition(position);
        recommendRecipeView.smoothScrollBy(200, 0);

        //Recycler display grid recipe
        recipeListView = view.findViewById(R.id.recipe_list);
        recipeListView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recipeListView.setLayoutManager(gridLayoutManager);

        recipeListAdapter.setContext(getActivity());
        recipeListView.setAdapter(recipeListAdapter);


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

//        recipeListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (gridLayoutManager.findLastVisibleItemPosition() ==  recipeListAdapter.getArticleList().size() - 1) {
//                    recipeListAdapter.addToArticleList(dbHelper.getNArticlesFromIndex(recipeListAdapter.getArticleList().size(), 10));
//                    articlesList = recipeListAdapter.getArticleList();
//                }
//            }
//        });

        //Search bar
        searchView = view.findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchArticle(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recipeListAdapter.setArticleList(articlesList);
                return true;
            }
        });

        //Sort button
        sortButtons.put("all", view.findViewById(R.id.all));
        sortButtons.put("most_follow", view.findViewById(R.id.most_follow));
        sortButtons.put("most_react", view.findViewById(R.id.most_react));
        sortButtons.put("most_recent", view.findViewById(R.id.most_recent));

        setSortButtonBehavior();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoScroll();
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
                            if (position == Integer.MAX_VALUE || position == -1) {
                                position = Integer.MAX_VALUE / 2;
                                recommendRecipeView.scrollToPosition(position);
                            } else {
                                position++;
                                recommendRecipeView.smoothScrollToPosition(position);
                            }
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
            position = rcmLLayoutManager.findFirstCompletelyVisibleItemPosition();
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

    @SuppressLint("ClickableViewAccessibility")
    public void setSortButtonBehavior(){
        Objects.requireNonNull(sortButtons.get("all")).getBackground()
                .setColorFilter(Color.rgb(220, 220, 220), PorterDuff.Mode.SRC);
        for (TextView sortButton: sortButtons.values()) {
            sortButton.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        for (TextView sortButton: sortButtons.values()) {
                            sortButton.getBackground().clearColorFilter();
                        }

                        v.getBackground().setColorFilter(Color.rgb(220, 220, 220), PorterDuff.Mode.SRC);

                        if (v == sortButtons.get("all")) {
                            recipeListAdapter.setArticleList(articlesList);
                        } else if (v == sortButtons.get("most_follow")) {
                            recipeListAdapter.sortByFollow(dbHelper);
                        } else if (v == sortButtons.get("most_react")) {
                            recipeListAdapter.sortByReact();
                        } else {
                            recipeListAdapter.sortByPublishedTime();
                        }
                    }
                    return true;
                }
            });
        }
    }
}