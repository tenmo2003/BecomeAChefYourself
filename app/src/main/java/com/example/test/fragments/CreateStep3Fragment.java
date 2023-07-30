package com.example.test.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.R;
import com.example.test.adapters.IngredientListAdapter;
import com.example.test.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateStep3Fragment extends Fragment {


    private Spinner typeChoice;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private static ExpandedListView ingredientsListView;
    private static ArrayList<String> ingredientsList;
    private static IngredientListAdapter ingredientsAdapter;

    private static StringBuilder ingredientString;

    ImageView imgInput;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        ViewGroup.LayoutParams params = imgInput.getLayoutParams();
                        params.width = 700;
                        params.height = 500;
                        imgInput.setLayoutParams(params);
                        imgInput.requestLayout();
                        imgInput.setImageURI(uri);
                        ShareFragment.imageURI = uri;
                        ShareFragment.imageChanged = true;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post_3, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeChoice = view.findViewById(R.id.spinner);

        List<String> types = new ArrayList<>(Arrays.asList("Món thịt", "Món hải sản", "Món chay", "Món canh", "Món rau", "Mì", "Bún", "Món cuốn", "Món xôi", "Món cơm", "Món bánh mặn", "Món bánh ngọt"));

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, types);

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeChoice.setAdapter(typeAdapter);

        ingredientsListView = view.findViewById(R.id.ingredients_list);

        ingredientsList = new ArrayList<>();

        ingredientsAdapter = new IngredientListAdapter(getActivity(), ingredientsList);

        ingredientsListView.setAdapter(ingredientsAdapter);

        EditText dishNameInput = view.findViewById(R.id.dish_name_input);
        EditText recipeInput = view.findViewById(R.id.recipe_input);
        EditText timeToMakeInput = view.findViewById(R.id.time_to_make_input);

        TextView nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

        imgInput = view.findViewById(R.id.img_input);

        ImageButton addIngredientBtn = view.findViewById(R.id.add_btn);
        EditText ingredientInput = view.findViewById(R.id.ingredients_input);
        ingredientString = new StringBuilder();

        if (ShareFragment.editing) {
            typeChoice.setSelection(types.indexOf(ShareFragment.typeChoice));
            dishNameInput.setText(ShareFragment.dishName);
            recipeInput.setText(ShareFragment.recipe);
            ingredientString.append(ShareFragment.ingredients);

            String[] ingredients = ShareFragment.ingredients.split(";\\s*");

            for (String s : ingredients) {
                addItem(s);
            }

            timeToMakeInput.setText(ShareFragment.timeToMake);

            if (!ShareFragment.imageURL.equals("")) {
                Glide.with(getActivity()).load(ShareFragment.imageURL).into(imgInput);
            }
        }

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingredient = ingredientInput.getText().toString();

                if (ingredient.equals("")) {
                    Toast.makeText(getActivity(), "Xin hãy nhập nguyên liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                addItem(ingredient);

                if (ingredientString.toString().length() != 0) {
                    ingredientString.append(';');
                }
                ingredientString.append(ingredient);

                ingredientInput.setText("");
            }
        });

        imgInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (typeChoice.getSelectedItem() != null &&
                        dishNameInput.getText() != null && !dishNameInput.getText().toString().isEmpty() &&
                        ingredientString != null && !ingredientString.toString().isEmpty() &&
                        recipeInput.getText() != null && !recipeInput.getText().toString().isEmpty() &&
                        timeToMakeInput.getText() != null && !timeToMakeInput.getText().toString().isEmpty()) {
                    // All EditText fields have valid input
                    // Perform the desired action here
                    ShareFragment.typeChoice = typeChoice.getSelectedItem().toString();
                    ShareFragment.dishName = dishNameInput.getText().toString();
                    ShareFragment.ingredients = ingredientString.toString();
                    ShareFragment.recipe = recipeInput.getText().toString();
                    ShareFragment.timeToMake = timeToMakeInput.getText().toString();

                    ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() + 1);

                } else {
                    // One or more EditText fields are empty or null
                    // Handle the case where input is missing
                    Toast.makeText(getActivity(), "Vui lòng điền hết các thông tin", Toast.LENGTH_SHORT).show();

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

    public void addItem(String ingredient) {
        ingredientsList.add(ingredient);
        ingredientsAdapter.setIngredientList(ingredientsList);
        ingredientsListView.setAdapter(ingredientsAdapter);

        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (ingredientsList.size() + 1) * 100);
        ingredientsListView.setLayoutParams(mParam);
    }

    public static void removeItem(int position) {
        StringBuilder updatedIngredientString = new StringBuilder();

        String[] ingredientsArray = ingredientString.toString().split(";");
        for (String ingredient : ingredientsArray) {
            if (!ingredient.equals(ingredientsList.get(position))) {
                if (updatedIngredientString.length() != 0) {
                    updatedIngredientString.append(';');
                }
                updatedIngredientString.append(ingredient);
            }
        }
        ingredientString = updatedIngredientString;

        ingredientsList.remove(position);
        ingredientsAdapter.setIngredientList(ingredientsList);
        ingredientsListView.setAdapter(ingredientsAdapter);
    }
}
