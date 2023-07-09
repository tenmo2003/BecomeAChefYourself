package com.example.test.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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

        EditText dishNameInput = view.findViewById(R.id.dish_name_input);

        EditText contentInput = view.findViewById(R.id.recipe_input);

        Button previewArticleBtn = view.findViewById(R.id.preview_article_button);

        previewArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dishName = dishNameInput.getText().toString();
                String recipeContent = contentInput.getText().toString();

                Bundle args = new Bundle();
                args.putString("dish_name", dishName);
                args.putString("recipe_content", recipeContent);

                Navigation.findNavController(view).navigate(R.id.navigation_article, args);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
