package com.example.test.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateStep3Fragment extends Fragment {


    private Spinner typeChoice;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

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
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imgInput.setImageURI(uri);
                        ShareFragment.imageURI = uri;
                        Log.i("path", ShareFragment.imageURI.getPath());

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, types);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeChoice.setAdapter(adapter);


        EditText dishNameInput = view.findViewById(R.id.dish_name_input);
        EditText ingredientsInput = view.findViewById(R.id.ingredients_input);
        EditText recipeInput = view.findViewById(R.id.recipe_input);
        EditText timeToMakeInput = view.findViewById(R.id.time_to_make_input);

        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

        imgInput = view.findViewById(R.id.img_input);

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
                ShareFragment.typeChoice = typeChoice.getSelectedItem().toString();
                ShareFragment.dishName = dishNameInput.getText().toString();
                ShareFragment.ingredients = ingredientsInput.getText().toString();
                ShareFragment.recipe = recipeInput.getText().toString();
                ShareFragment.timeToMake = timeToMakeInput.getText().toString();

                if (ShareFragment.typeChoice == null || ShareFragment.dishName == null || ShareFragment.ingredients == null || ShareFragment.recipe == null || ShareFragment.timeToMake == null) {
                    Toast.makeText(getActivity(), "Vui lòng điền hết các thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() + 1);
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
