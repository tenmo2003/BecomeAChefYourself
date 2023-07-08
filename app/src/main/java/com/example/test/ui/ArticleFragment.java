package com.example.test.ui;

import android.os.Build;
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

import java.time.LocalDate;

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

        TextView contentTextView = view.findViewById(R.id.content_text);

        contentTextView.setText(Html.fromHtml("\t<img src=\"https://i.imgur.com/5e2qPZH.jpeg\" alt=\"Chocolate chip cookies\">\n" +
                "\t<p>Nothing beats the classic chocolate chip cookie. This recipe yields a dozen perfectly chewy and golden-brown cookies, with just the right balance of sweetness and crunch.</p>\n" +
                "\t<h2>Ingredients</h2>\n" +
                "\t<ul>\n" +
                "\t\t<li>1/2 cup unsalted butter, at room temperature</li>\n" +
                "\t\t<li>1/2 cup granulated sugar</li>\n" +
                "\t\t<li>1/2 cup brown sugar</li>\n" +
                "\t\t<li>1 large egg</li>\n" +
                "\t\t<li>1 teaspoon vanilla extract</li>\n" +
                "\t\t<li>1 1/2 cups all-purpose flour</li>\n" +
                "\t\t<li>1/2 teaspoon baking soda</li>\n" +
                "\t\t<li>1/4 teaspoon salt</li>\n" +
                "\t\t<li>1 cup semisweet chocolate chips</li>\n" +
                "\t</ul>\n" +
                "\t<br>\n" +
                "\t<h2>Instructions</h2>\n" +
                "\t<ol>\n" +
                "\t\t<li>Preheat the oven to 350°F (180°C) and line a baking sheet with parchment paper.</li>\n" +
                "\t\t<li>In a large mixing bowl, cream together the butter, granulated sugar, and brown sugar until light and fluffy.</li>\n" +
                "\t\t<li>Add the egg and vanilla extract to the bowl and mix until well combined.</li>\n" +
                "\t\t<li>In a separate mixing bowl, whisk together the flour, baking soda, and salt.</li>\n" +
                "\t\t<li>Gradually add the dry ingredients to the wet mixture, mixing until just combined.</li>\n" +
                "\t\t<li>Fold in the chocolate chips.</li>\n" +
                "\t\t<li>Using a cookie scoop or spoon, drop balls of dough onto the prepared baking sheet, spacing them about 2 inches apart.</li>\n" +
                "\t\t<li>Bake the cookies for 12-15 minutes, or until the edges are golden brown and the centers are set.</li>\n" +
                "\t\t<li>Remove the cookies from the oven and let them cool on the baking sheet for 5 minutes before transferring them to a wire rack to cool completely.</li>\n" +
                "\t</ol>\n" +
                "\t<br>\n" +
                "\t<p>Enjoy your freshly baked chocolate chip cookies with a glass of cold milk or a hot cup of coffee. These cookies can be stored in an airtight container at room temperature for up to 3 days.</p>", Html.FROM_HTML_MODE_LEGACY));

        TextView dishName = view.findViewById(R.id.dish_name_text);

        dishName.setText("Classic Chocolate Chip Cookies");

        TextView publisher = view.findViewById(R.id.publisher_text);

        publisher.setText("Published by admin");

        TextView publishedDate = view.findViewById(R.id.published_date_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            publishedDate.setText(LocalDate.now().toString());
        }

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
