package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.databinding.FragmentBookmarksBinding;

public class BookmarksFragment extends Fragment {
    private FragmentBookmarksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        BookmarksViewModel bookmarksViewModel =
//                new ViewModelProvider(this).get(BookmarksViewModel.class);
//
//        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textBookmarks;
//        bookmarksViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;

        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
