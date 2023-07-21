package com.example.test.fragments;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.ImageController;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

//        TextView dishNameTextView = view.findViewById(R.id.dish_name_text);
//        TextView recipeContentTextView = view.findViewById(R.id.recipe_text);
//        TextView ingredientsTextView = view.findViewById(R.id.ingredients_text);
//        TextView publisherTextView = view.findViewById(R.id.publisher_text);
//        TextView publishedDateTextView = view.findViewById(R.id.published_date_text);
//        TextView timeToMakeTextView = view.findViewById(R.id.time_to_make_text);
//        TextView ratingTextView = view.findViewById(R.id.ratings_text);
//
//        dishNameTextView.setText(ShareFragment.dishName);
//        recipeContentTextView.setText(Html.fromHtml(ShareFragment.recipe, Html.FROM_HTML_MODE_COMPACT));
//        ingredientsTextView.setText(formatIngredients(ShareFragment.ingredients));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            publishedDateTextView.setText(LocalDate.now().toString());
//        }
//        publisherTextView.setText(MainActivity.loggedInUser.getUsername());
//        timeToMakeTextView.setText(ShareFragment.timeToMake);
//        ratingTextView.setText("0");

        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

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
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                boolean result = dbHelper.addArticle(ShareFragment.dishName, MainActivity.loggedInUser.getUsername(), ShareFragment.mealChoice, ShareFragment.serveOrderChoice, ShareFragment.typeChoice, ShareFragment.recipe, ShareFragment.ingredients, ShareFragment.timeToMake, "https://tenmo2003.000webhostapp.com/article_" + dbHelper.getNextArticleID() + ".png");
                if (ShareFragment.imageURI != null) {

                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Image Upload", "Uploading");
                            ImageController.uploadImage(ShareFragment.imageURI, "article_" + dbHelper.getNextArticleID() + ".png", getContext());
                            Log.i("Image Upload", "Uploaded");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    if (result) {
                                        Toast.makeText(getActivity(), "Recipe shared successfully!", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view).navigate(R.id.navigation_home);
                                    } else {
                                        Toast.makeText(getActivity(), "We have failed to share your recipe!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView dishNameTextView = fView.findViewById(R.id.dish_name_text);
        TextView recipeContentTextView = fView.findViewById(R.id.recipe_text);
        TextView ingredientsTextView = fView.findViewById(R.id.ingredients_text);
        TextView publisherTextView = fView.findViewById(R.id.publisher_text);
        TextView publishedDateTextView = fView.findViewById(R.id.published_date_text);
        TextView timeToMakeTextView = fView.findViewById(R.id.time_to_make_text);
        TextView ratingTextView = fView.findViewById(R.id.ratings_text);

        dishNameTextView.setText(ShareFragment.dishName);
        recipeContentTextView.setText(Html.fromHtml(ShareFragment.recipe, Html.FROM_HTML_MODE_COMPACT));
        ingredientsTextView.setText(formatIngredients(ShareFragment.ingredients));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            publishedDateTextView.setText(LocalDate.now().toString());
        }
        publisherTextView.setText(MainActivity.loggedInUser.getUsername());
        timeToMakeTextView.setText(ShareFragment.timeToMake);
        ratingTextView.setText("0");
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
