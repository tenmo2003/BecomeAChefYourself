package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateStep3Fragment extends Fragment {

//    private CustomSpinner spinner_select;
//    private SelectAdapter adapter;

    private Spinner typeChoice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post_3, container, false);


//        spinner_select = view.findViewById(R.id.spinner_select);
//
//        spinner_select.setSpinnerEventsListener(this);
//
//        adapter = new SelectAdapter(getActivity(), Data.getSelectList());
//        spinner_select.setAdapter(adapter);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeChoice = view.findViewById(R.id.spinner);

        List<String> types = new ArrayList<>(Arrays.asList("Món thịt", "Món hải sản", "Món chay", "Món canh", "Món rau", "Mì", "Bún", "Món cuốn", "Món xôi", "Món cơm", "Món bánh mặn", "Món bánh ngọt"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, types);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeChoice.setAdapter(adapter);


        EditText dishNameInput = view.findViewById(R.id.dish_name_input);
        EditText ingredientsInput = view.findViewById(R.id.ingredients_input);
        EditText recipeInput = view.findViewById(R.id.recipe_input);

        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareFragment.typeChoice = typeChoice.getSelectedItem().toString();
                ShareFragment.dishName = dishNameInput.getText().toString();
                ShareFragment.ingredients = ingredientsInput.getText().toString();
                ShareFragment.recipe = recipeInput.getText().toString();


//                ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() + 1);

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                boolean result = dbHelper.addArticle(ShareFragment.dishName, MainActivity.loggedInUser.getUsername(), ShareFragment.mealChoice, ShareFragment.serveOrderChoice, ShareFragment.typeChoice, ShareFragment.recipe, ShareFragment.ingredients);
                if (result) {
                    Toast.makeText(getActivity(), "Recipe shared successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "We have failed to share your recipe!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() - 1);
            }
        });

    }
}
