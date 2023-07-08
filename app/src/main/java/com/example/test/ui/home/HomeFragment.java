package com.example.test.ui.home;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView textView = view.findViewById(R.id.text_home);

        textView.setText(Html.fromHtml("<h1 style=\"height: 50px; line-height: 50px; text-align: center;\">Classic Chocolate Chip Cookies</h1>\n" +
                "\t<img src=\"https://i.imgur.com/5e2qPZH.jpeg\" alt=\"Chocolate chip cookies\">\n" +
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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}