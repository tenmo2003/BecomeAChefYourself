package com.example.test.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.adapters.RecommendRecipeAdapter;
import com.example.test.components.Article;
import com.example.test.utils.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    SearchView searchView;
    HashMap<String, TextView> sortButtons = new HashMap<>();

    RecipeListAdapter recipeListAdapter;
    public static int adapterPos = -1;
    public static int commentAdded = 0;
    public static int likeAdded = 0;
    public static boolean deleted = false;
    public static boolean bookmarked = false;
    RecommendRecipeAdapter recommendRecipeAdapter;
    RecyclerView recipeListView, recommendRecipeView;
    LinearLayoutManager rcmLLayoutManager;
    Timer timer;
    TimerTask timerTask;
    Handler timerHandler = new Handler();
    int position = Integer.MAX_VALUE / 2;
    DatabaseHelper dbHelper;
    List<Article> articlesList, recommendRecipeList;

    View view = null;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            NestedScrollView scrollView = view.findViewById(R.id.nested_scroll_view);

            LinearLayout filterField = view.findViewById(R.id.filter_field);
            filterField.setVisibility(View.GONE);

            dbHelper = new DatabaseHelper(getActivity());

            recommendRecipeAdapter = new RecommendRecipeAdapter();
            recipeListAdapter = new RecipeListAdapter();

            recommendRecipeView = view.findViewById(R.id.recommend_recipe_list);
            rcmLLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recommendRecipeView.setLayoutManager(rcmLLayoutManager);

            //Recycler display recommend recipe
            recommendRecipeView.setHasFixedSize(true);

            //Make recyclerview faster
            recommendRecipeView.setItemViewCacheSize(10);

            //Recycler display grid recipe
            recipeListView = view.findViewById(R.id.recipe_list);
            recipeListView.setHasFixedSize(true);

            //Make recyclerview faster
            recipeListView.setItemViewCacheSize(10);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            recipeListView.setLayoutManager(gridLayoutManager);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recommendRecipeView);

            //Sort button
            sortButtons.put("default_sort", view.findViewById(R.id.default_sort));
            sortButtons.put("most_follow", view.findViewById(R.id.most_follow));
            sortButtons.put("most_react", view.findViewById(R.id.most_react));
            sortButtons.put("most_recent", view.findViewById(R.id.most_recent));

            //Search bar
            searchView = view.findViewById(R.id.search_view);
            searchView.clearFocus();

            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.smoothScrollTo(0, 0);
                }
            });

            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    float currentAlpha = fab.getAlpha();
                    if (i3 < i1) { // scrolling down
                        if (currentAlpha > 0f) {
                            currentAlpha -= 0.05f;
                            fab.setAlpha(currentAlpha);
                        } else {
                            fab.setVisibility(View.GONE);
                        }
                    } else if (i3 > i1) { // scrolling up
                        fab.setVisibility(View.VISIBLE);
                        if (currentAlpha < 1f) {
                            currentAlpha += 0.1f;
                            fab.setAlpha(currentAlpha);
                        }
                    }
                }
            });

            loadView();

            adapterPos = -1;
        }

        ImageView reload = view.findViewById(R.id.reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadView();
            }
        });

        if (adapterPos != -1) {
            recipeListAdapter.updateViewHolder(adapterPos, commentAdded, likeAdded, bookmarked);
            adapterPos = -1;
            commentAdded = 0;
            likeAdded = 0;
        }


        return view;
    }

    private void loadView() {
        MainActivity.runTask(() -> {
            articlesList = dbHelper.getAllArticles();
            Collections.shuffle(articlesList);
        }, () -> {
            recommendRecipeList = articlesList.subList(0, 10);
            recommendRecipeAdapter.setRecommendRecipeList(recommendRecipeList);
            recommendRecipeAdapter.setContext(getActivity());

            recommendRecipeView.setAdapter(recommendRecipeAdapter);
            recommendRecipeView.scrollToPosition(position);
            recommendRecipeView.smoothScrollBy(200, 0);

            Collections.shuffle(articlesList);

            recipeListAdapter.setArticleList(articlesList);
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

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    searchArticle(s);
                    searchView.clearFocus();
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.equals("") && recipeListAdapter.getArticleList().size() != articlesList.size()) {
                        recipeListAdapter.setArticleList(articlesList);
                    }
                    return true;
                }
            });


            setSortButtonBehavior();

            //Filter
            Button filterButton = view.findViewById(R.id.filter_btn);
            RadioGroup mealFilter = view.findViewById(R.id.meal_filter);
            RadioGroup serveOrderClassFilter = view.findViewById(R.id.serve_order_class_filter);
//            RadioGroup typeFilter = view.findViewById(R.id.type_filter);
            LinearLayout filterField = view.findViewById(R.id.filter_field);
            TextView applyFilter = view.findViewById(R.id.apply_filter);
            TextView clearFilter = view.findViewById(R.id.clear_filter);
            Spinner typeChoice = view.findViewById(R.id.typeSpinner);

            List<String> types = new ArrayList<>(Arrays.asList("Chọn", "Món thịt", "Món hải sản", "Món chay", "Món canh", "Món rau", "Mì", "Bún", "Món cuốn", "Món xôi", "Món cơm", "Món bánh mặn", "Món bánh ngọt"));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, types);

            typeChoice.setAdapter(adapter);

            filterField.setVisibility(View.GONE);

            applyFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> filterList = new ArrayList<>();
                    HashMap<Integer, String> filterItems = new HashMap<>();

                    filterItems.put(-1, "");

                    filterItems.put(R.id.breakfast_item, "Bữa sáng");
                    filterItems.put(R.id.lunch_item, "Bữa trưa");
                    filterItems.put(R.id.dinner_item, "Bữa tối");
                    filterItems.put(R.id.flexible_item, "Linh hoạt");

                    filterItems.put(R.id.appetizer_item, "Món khai vị");
                    filterItems.put(R.id.main_item, "Món chính");
                    filterItems.put(R.id.dessert_item, "Món tráng miệng");

//                    filterItems.put(R.id.meat_item, "Món thịt");
//                    filterItems.put(R.id.seafood, "Món hải sản");
//                    filterItems.put(R.id.vegetarian, "Món chay");
//                    filterItems.put(R.id.soup, "Món canh");

                    filterList.add(filterItems.get(mealFilter.getCheckedRadioButtonId()));
                    filterList.add(filterItems.get(serveOrderClassFilter.getCheckedRadioButtonId()));
//                    filterList.add(filterItems.get(typeFilter.getCheckedRadioButtonId()));
                    if (typeChoice.getSelectedItemPosition() == 0) {
                        filterList.add("");
                    } else {
                        filterList.add((String) typeChoice.getSelectedItem());
                    }

                    filterArticle(filterList);
                }
            });

            clearFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mealFilter.clearCheck();
                    serveOrderClassFilter.clearCheck();
//                    typeFilter.clearCheck();
                    typeChoice.setSelection(0);
                    if (recipeListAdapter.getArticleList().size() != articlesList.size()) {
                        recipeListAdapter.setArticleList(articlesList);
                    }
                }
            });

            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (filterField.getVisibility() == View.GONE) {
                        filterField.setVisibility(View.VISIBLE);
                    } else {
                        filterField.setVisibility(View.GONE);
                    }
                }
            });
        }, MainActivity.progressDialog);
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
        for (TextView sortButton: sortButtons.values()) {
            sortButton.getBackground().clearColorFilter();
            sortButton.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        for (TextView sortButton: sortButtons.values()) {
                            sortButton.getBackground().clearColorFilter();
                        }

                        v.getBackground().setColorFilter(Color.rgb(220, 220, 220), PorterDuff.Mode.SRC);

                        if (v == sortButtons.get("default_sort")) {
                            MainActivity.runTask(() -> {
                            }, () -> {
                                recipeListAdapter.setArticleList(articlesList);
                            }, MainActivity.progressDialog);
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
        Objects.requireNonNull(sortButtons.get("default_sort")).getBackground()
                .setColorFilter(Color.rgb(220, 220, 220), PorterDuff.Mode.SRC);
    }

    private void filterArticle(List<String> filterList) {
        // No filter has passed
        if (filterList.get(0).equals("") && filterList.get(1).equals("") && filterList.get(2).equals("")) {
            if (recipeListAdapter.getArticleList().size() != articlesList.size()) {
                recipeListAdapter.setArticleList(articlesList);
            }
            return;
        }

        // Reset data when re-filter
        for (TextView sortButton: sortButtons.values()) {
            sortButton.getBackground().clearColorFilter();
        }
        Objects.requireNonNull(sortButtons.get("default_sort")).getBackground().
                setColorFilter(Color.rgb(220, 220, 220), PorterDuff.Mode.SRC);
        recipeListAdapter.setArticleList(articlesList);

        List<Article> filteredArticle = new ArrayList<>();
        for (Article article: recipeListAdapter.getArticleList()) {
            boolean matchMeal = article.getMeal().equals(filterList.get(0)) || filterList.get(0).equals("");
            boolean matchServeOrderClass = article.getServeOrderClass().equals(filterList.get(1)) || filterList.get(1).equals("");
            boolean matchType = article.getType().equals(filterList.get(2)) || filterList.get(2).equals("");

            if (matchMeal && matchServeOrderClass && matchType) {
                filteredArticle.add(article);
            }
        }
        recipeListAdapter.setArticleList(filteredArticle);
    }
}