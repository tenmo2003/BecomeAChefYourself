package com.example.test.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.databinding.FragmentSearchBinding;
import com.example.test.databinding.FragmentShareBinding;
import com.example.test.ui.search.SearchViewModel;

public class ShareFragment extends Fragment {
    private FragmentShareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ShareViewModel shareViewModel =
//                new ViewModelProvider(this).get(ShareViewModel.class);
//
//        binding = FragmentShareBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textShare;
//        shareViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;

        View view = inflater.inflate(R.layout.fragment_share, container, false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
