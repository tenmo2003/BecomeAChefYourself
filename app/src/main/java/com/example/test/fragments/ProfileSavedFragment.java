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
import android.widget.FrameLayout;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.RecipeListAdapter;
import com.example.test.components.Article;
import com.example.test.components.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileSavedFragment extends Fragment {
    RecyclerView userSavedList;
    private final User profileUser;
    FrameLayout parent;

    public ProfileSavedFragment(User profileUser) {
        this.profileUser = profileUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_saved, container, false);

        parent = view.findViewById(R.id.parent);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userSavedList = view.findViewById(R.id.user_saved_list);
        userSavedList.setHasFixedSize(true);
        userSavedList.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        RecipeListAdapter adapter = new RecipeListAdapter();
//        adapter.setInHome(false);
        AtomicReference<List<Article>> articleList = new AtomicReference<>();
        MainActivity.runTask(() -> {
            articleList.set(MainActivity.sqlConnection.getUserSavedArticles(profileUser.getUsername()));
        }, () -> {
            adapter.setArticleList(articleList.get());
            adapter.setContext(getActivity());
            userSavedList.setAdapter(adapter);
        }, MainActivity.progressDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
//        parent.requestLayout();
    }
}