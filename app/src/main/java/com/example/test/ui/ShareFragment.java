package com.example.test.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.database.DatabaseHelper;
import com.example.test.databinding.FragmentShareBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText dishNameInput = view.findViewById(R.id.dish_name_input);

        EditText contentInput = view.findViewById(R.id.content_input);

        Button previewArticleBtn = view.findViewById(R.id.preview_article_button);

        Button shareArticleBtn = view.findViewById(R.id.share_article_button);

        Spinner typeChoice = view.findViewById(R.id.type_choice);

        List<String> types = new ArrayList<>(Arrays.asList("Món thịt", "Món hải sản", "Món chay", "Món canh", "Món rau", "Mì", "Bún", "Món cuốn", "Món xôi", "Món cơm", "Món bánh mặn", "Món bánh ngọt"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, types);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        typeChoice.setAdapter(adapter);
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

        shareArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getActivity(), "Please log in first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioGroup mealChoiceGroup = view.findViewById(R.id.meal_choice_group);
                int selectedMealChoiceId = mealChoiceGroup.getCheckedRadioButtonId();
                RadioButton selectedMealChoice = view.findViewById(selectedMealChoiceId);
                String mealChoice = selectedMealChoice.getText().toString();

                RadioGroup serveOrderGroup = view.findViewById(R.id.serve_order_group);
                int selectedServeOrderId = serveOrderGroup.getCheckedRadioButtonId();
                RadioButton selectedServeOrder = view.findViewById(selectedServeOrderId);
                String serveOrder = selectedServeOrder.getText().toString();

                String type = typeChoice.getSelectedItem().toString();

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                boolean result = dbHelper.addArticle(dishNameInput.getText().toString(), MainActivity.loggedInUser.getUsername(), mealChoice, serveOrder, type, contentInput.getText().toString());
                if (result) {
                    Toast.makeText(getActivity(), "Shared successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Shared failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
