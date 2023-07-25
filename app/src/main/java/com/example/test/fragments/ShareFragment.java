package com.example.test.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.SectionsPagerAdapter;
import com.example.test.databinding.FragmentShareBinding;

public class ShareFragment extends Fragment {

    public static String mealChoice;
    public static String typeChoice;
    public static String serveOrderChoice;
    public static String dishName;
    public static String ingredients;
    public static String recipe;
    public static String timeToMake;
    public static Uri imageURI;
    public static String imageURL;
    public static boolean imageChanged;
    public static boolean editing;
    public static int articleID;

    public static ViewPager2 viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        editing = false;
        imageChanged = false;
        Bundle args = getArguments();

        if (args != null) {
            articleID = args.getInt("articleID");
            mealChoice = args.getString("mealChoice");
            typeChoice = args.getString("typeChoice");
            serveOrderChoice = args.getString("serveOrderChoice");
            dishName = args.getString("dishName");
            ingredients = args.getString("ingredients");
            recipe = args.getString("recipe");
            timeToMake = args.getString("timeToMake");
            imageURL = args.getString("imageURL");
            editing = args.getBoolean("editing");
        }


        viewPager = view.findViewById(R.id.view_pager);
        setUpViewPager(viewPager);
        viewPager.setUserInputEnabled(false);

        return view;
    }

    private void setUpViewPager(ViewPager2 viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new CreateStep1Fragment());
        adapter.addFragment(new CreateStep2Fragment());
        adapter.addFragment(new CreateStep3Fragment());
        adapter.addFragment(new CreateStep4Fragment());

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
