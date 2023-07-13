package com.example.test.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;

public class ArticleFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        SearchViewModel searchViewModel =
//                new ViewModelProvider(this).get(SearchViewModel.class);
//
//        binding = FragmentSearchBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textSearch;
//        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dishNameTextView = view.findViewById(R.id.dish_name_text);
        TextView recipeContentTextView = view.findViewById(R.id.content_text);
        TextView publisherTextView = view.findViewById(R.id.publisher_text);
        TextView publishedDateTextView = view.findViewById(R.id.published_date_text);

        Bundle args = getArguments();
        if (args != null) {
            String dishName = args.getString("dish_name");
            String recipeContent = args.getString("recipe_content");
            String publisher = args.getString("publisher");
            String publishedDate = args.getString("publishedDate");

            dishNameTextView.setText(dishName);
            recipeContentTextView.setText(Html.fromHtml(recipeContent, Html.FROM_HTML_MODE_COMPACT));
            publishedDateTextView.setText(publishedDate);
            publisherTextView.setText("Published by " + publisher);
        }


//        Html test text
//        <h1>Spaghetti Bolognese Recipe</h1>
//	<ul>
//		<li><strong>Prep Time:</strong> 15 minutes</li>
//		<li><strong>Cook Time:</strong> 1 hour</li>
//		<li><strong>Total Time:</strong> 1 hour 15 minutes</li>
//	</ul>
//	<h2>Ingredients:</h2>
//	<ul>
//		<li>1 pound spaghetti</li>
//		<li>1 pound ground beef</li>
//		<li>1 onion, chopped</li>
//		<li>2 cloves garlic, minced</li>
//		<li>1 can (28 ounces) crushed tomatoes</li>
//		<li>1 teaspoon dried basil</li>
//		<li>1 teaspoon dried oregano</li>
//		<li>1/2 teaspoon salt</li>
//		<li>1/4 teaspoon black pepper</li>
//		<li>Grated Parmesan cheese, for serving</li>
//	</ul>
//	<h2>Directions:</h2>
//	<ol>
//		<li>Bring a large pot of salted water to a boil. Add spaghetti and cook according to package directions.</li>
//		<li>Meanwhile, in a large skillet over medium heat, cook ground beef, onion, and garlic until beef is no longer pink. Drain any excess fat.</li>
//		<li>Add crushed tomatoes, basil, oregano, salt, and black pepper to the skillet. Bring to a simmer and cook for 20-30 minutes, stirring occasionally.</li>
//		<li>Drain spaghetti and return to pot. Add sauce and toss to coat.</li>
//		<li>Serve hot with grated Parmesan cheese.</li>
//	</ol>

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
