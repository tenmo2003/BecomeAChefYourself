package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.ImageController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CreateStep4Fragment extends Fragment {

    View fView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.create_post_4, container, false);

        return fView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TextView nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() - 1);
            }
        });

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ShareFragment.editing) {
                    AtomicReference<String> imageURL = new AtomicReference<>("");
                    AtomicBoolean result = new AtomicBoolean(false);
                    MainActivity.runTask(() -> {
                        if (ShareFragment.imageChanged) {
                            imageURL.set("https://tenmo2003.000webhostapp.com/article_" + MainActivity.loggedInUser.getUsername() + (MainActivity.sqlConnection.getTotalArticleCount(MainActivity.loggedInUser.getUsername()) + 1) + "_" + new Random().nextInt() + ".jpg");
                        }
                        result.set(MainActivity.sqlConnection.addArticle(ShareFragment.dishName, MainActivity.loggedInUser.getUsername(), ShareFragment.mealChoice, ShareFragment.serveOrderChoice, ShareFragment.typeChoice, ShareFragment.recipe, ShareFragment.ingredients, ShareFragment.timeToMake, imageURL.get()));
                    }, () -> {
                        if (ShareFragment.imageChanged) {
                            String finalImageURL = imageURL.get();
                            MainActivity.runTask(() -> {
                                ImageController.uploadImage(ShareFragment.imageURI, finalImageURL.substring(finalImageURL.indexOf("article")), getContext());
                            }, () -> {
                                if (result.get()) {
                                    MainActivity.toast.setText("Đăng bài thành công!");
                                    MainActivity.toast.show();
                                    Navigation.findNavController(view).navigate(R.id.navigation_home);

                                } else {
                                    MainActivity.toast.setText("Đăng bài thất bại!");
                                    MainActivity.toast.show();
                                }
                            }, MainActivity.progressDialog);
                        } else {
                            if (result.get()) {
                                MainActivity.toast.setText("Đăng bài thành công!");
                                MainActivity.toast.show();
                                Navigation.findNavController(view).navigate(R.id.navigation_home);
                            } else {
                                MainActivity.toast.setText("Đăng bài thất bại!");
                                MainActivity.toast.show();
                            }
                        }
                    }, new ProgressDialog(getActivity()));


                } else {
                    AtomicReference<String> imageURL = new AtomicReference<>(ShareFragment.imageURL);
                    AtomicBoolean result = new AtomicBoolean(false);
                    MainActivity.runTask(() -> {
                        if (ShareFragment.imageChanged) {
                            imageURL.set("https://tenmo2003.000webhostapp.com/article_" + MainActivity.loggedInUser.getUsername() + (MainActivity.sqlConnection.getTotalArticleCount(MainActivity.loggedInUser.getUsername()) + 1) + "_" + new Random().nextInt() + ".jpg");
                        }
                         result.set(MainActivity.sqlConnection.editArticle(ShareFragment.articleID, ShareFragment.dishName, MainActivity.loggedInUser.getUsername(), ShareFragment.mealChoice, ShareFragment.serveOrderChoice, ShareFragment.typeChoice, ShareFragment.recipe, ShareFragment.ingredients, ShareFragment.timeToMake, imageURL.get()));
                    }, () -> {
                        if (ShareFragment.imageChanged) {
                            String finalImageURL = imageURL.get();
                            MainActivity.runTask(() -> {
                                ImageController.uploadImage(ShareFragment.imageURI, finalImageURL.substring(finalImageURL.indexOf("article")), getContext());
                            }, () -> {
                                if (result.get()) {
                                    MainActivity.toast.setText("Sửa bài thành công!");
                                    MainActivity.toast.show();
                                    Navigation.findNavController(view).navigate(R.id.navigation_home);
                                } else {
                                    MainActivity.toast.setText("Sửa bài thất bại!");
                                    MainActivity.toast.show();
                                }
                            }, MainActivity.progressDialog);
                        } else {
                            if (result.get()) {
                                MainActivity.toast.setText("Sửa bài thành công!");
                                MainActivity.toast.show();
                                Navigation.findNavController(view).navigate(R.id.navigation_home);
                            } else {
                                MainActivity.toast.setText("Sửa bài thất bại!");
                                MainActivity.toast.show();
                            }
                        }
                    }, new ProgressDialog(getActivity()));

                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        TextView dishNameTextView = fView.findViewById(R.id.preview_title);
        TextView recipeContentTextView = fView.findViewById(R.id.recipe_text);
        TextView ingredientsTextView = fView.findViewById(R.id.ingredients_text);
        TextView ingredientCountsTextView = fView.findViewById(R.id.ingredients_count);
        TextView timeToMakeTextView = fView.findViewById(R.id.time_to_make_text);
        ImageView dishImg = fView.findViewById(R.id.preview_image);

        if (ShareFragment.imageURI != null) {
            dishImg.setImageURI(ShareFragment.imageURI);
        } else if (ShareFragment.editing) {
            Glide.with(getActivity()).load(ShareFragment.imageURL).into(dishImg);
        }

        dishNameTextView.setText(ShareFragment.dishName);
        recipeContentTextView.setText(ShareFragment.recipe);
        ingredientsTextView.setText(formatIngredients(ShareFragment.ingredients));
        ingredientCountsTextView.setText(ShareFragment.ingredients.split(";\\s*").length + " thành phần");
        timeToMakeTextView.setText(ShareFragment.timeToMake);
    }

    private String formatIngredients(String input) {

        // Split the input string into an array of individual ingredients
        String[] ingredients = input.split(";\\s*");

        // Use a StringBuilder to build the formatted string
        StringBuilder builder = new StringBuilder();
        for (String ingredient : ingredients) {
            builder.append("- ")
                    .append(ingredient.substring(0, 1).toUpperCase())
                    .append(ingredient.substring(1))
                    .append("\n");
        }

        return builder.toString();
    }
}
