package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.components.Article;
import com.example.test.components.User;
import com.example.test.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostFragment extends Fragment {
    RecyclerView userRecipeList;
    private final User profileUser;
    List<Article> articleList;

    public ProfilePostFragment(User profileUser) {
        this.profileUser = profileUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userRecipeList = view.findViewById(R.id.user_recipe_list);
        userRecipeList.setHasFixedSize(true);
        userRecipeList.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        RecipeListAdapter adapter = new RecipeListAdapter();
        adapter.setInHome(false);

        MainActivity.runTask(() -> {
            articleList = getProfilePost();
        }, () -> {
            adapter.setArticleList(articleList);
            adapter.setContext(getActivity());
            userRecipeList.setAdapter(adapter);
        }, MainActivity.progressDialog);

    }

    private List<Article> getProfilePost() {
        List<Article> tmp = new ArrayList<>();
        for (Article article : MainActivity.articleList) {
            if (article.getPublisher().equals(profileUser.getUsername())) {
                tmp.add(article);
            }
        }
        return tmp;
    }
}