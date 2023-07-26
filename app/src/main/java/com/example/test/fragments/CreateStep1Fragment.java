package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;

public class CreateStep1Fragment extends Fragment {

    public static String choice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post_1, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareFragment.mealChoice == null) {
                    Toast.makeText(getActivity(), "Vui lòng chọn một ô", Toast.LENGTH_SHORT).show();
                } else {

                    ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() + 1);
                }
            }
        });

        FrameLayout selection1 = view.findViewById(R.id.appetizer_frame);
        FrameLayout selection2 = view.findViewById(R.id.main_frame);
        FrameLayout selection3 = view.findViewById(R.id.dessert_frame);
        FrameLayout selection4 = view.findViewById(R.id.flex_frame);

        View selectionBackground1 = view.findViewById(R.id.background_breakfast);
        View selectionBackground2 = view.findViewById(R.id.background_lunch);
        View selectionBackground3 = view.findViewById(R.id.background_dinner);
        View selectionBackground4 = view.findViewById(R.id.background_flex);

        if (ShareFragment.editing) {
            if (ShareFragment.mealChoice.equals("Bữa sáng")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            } else if (ShareFragment.mealChoice.equals("Bữa sáng")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            } else if (ShareFragment.mealChoice.equals("Bữa sáng")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            } else {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner_selected);
            }
        }

        // Set onClickListener to each FrameLayout
        selection1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.mealChoice = "Bữa sáng";

                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            }
        });

        selection2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.mealChoice = "Bữa trưa";
                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            }
        });

        selection3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.mealChoice = "Bữa tối";
                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner);
            }
        });

        selection4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.mealChoice = "Linh hoạt";
                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
                selectionBackground4.setBackgroundResource(R.drawable.make_corner_selected);
            }
        });

    }
}
