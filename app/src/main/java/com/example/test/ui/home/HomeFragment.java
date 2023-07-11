package com.example.test.ui.home;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecipeListAdapter recipeListAdapter;
    RecyclerView recyclerView;
    SearchView searchView;
    List<String> list = Arrays.asList("Thịt bò", "Thịt gà", "Cơm rang",
            "Xôi", "Bánh cuốn", "Chả cá", "Canh rau", "Khoai tây", "Chuối", "Pizza");

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recipe_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recipeListAdapter = new RecipeListAdapter();
        recipeListAdapter.setDish_names(list);
        recyclerView.setAdapter(recipeListAdapter);

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

    public void filterList(String text) {
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