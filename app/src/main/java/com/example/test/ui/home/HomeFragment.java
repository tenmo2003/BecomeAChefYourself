package com.example.test.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.test.R;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.adapters.RecommendRecipeAdapter;
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
    RecipeListAdapter recipeListAdapter;
    RecommendRecipeAdapter recommendRecipeAdapter;
    RecyclerView recipeListView, recommendRecipeView;
    LinearLayoutManager rcmLLayoutManager;
    Timer timer;
    TimerTask timerTask;
    Handler timerHandler = new Handler();
    int position = Integer.MAX_VALUE / 2;
    List<String> list = Arrays.asList("Thịt bò", "Thịt gà", "Cơm rang",
            "Xôi", "Bánh cuốn", "Chả cá", "Canh rau", "Khoai tây", "Chuối", "Pizza");

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
        if (timer != null && timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
            position = rcmLLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void filterList(String text) {
        List<String> filteredList = new ArrayList<>();
        for (String dish_name: list) {
            String dish_name_copy = dish_name;

            //Remove accents from string
            dish_name_copy = dish_name_copy.toLowerCase();
            dish_name_copy = Normalizer.normalize(dish_name_copy, Normalizer.Form.NFD);
            dish_name_copy = dish_name_copy.replaceAll("[^\\p{ASCII}]", "");
            text = text.toLowerCase();
            text = Normalizer.normalize(text, Normalizer.Form.NFD);
            text = text.replaceAll("[^\\p{ASCII}]", "");

            if (dish_name_copy.contains(text)) {
                filteredList.add(dish_name);
            }
        }
        recipeListAdapter.setDish_names(filteredList);
    }
}