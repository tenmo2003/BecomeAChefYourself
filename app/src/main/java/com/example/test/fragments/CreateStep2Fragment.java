package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.activities.MainActivity;

public class CreateStep2Fragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post_2, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareFragment.serveOrderChoice == null) {
                    MainActivity.toast.setText("Vui lòng chọn một ô");
                    MainActivity.toast.show();

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

        FrameLayout selection1 = view.findViewById(R.id.appetizer_frame);
        FrameLayout selection2 = view.findViewById(R.id.main_frame);
        FrameLayout selection3 = view.findViewById(R.id.dessert_frame);

        View selectionBackground1 = view.findViewById(R.id.background_appetizer);
        View selectionBackground2 = view.findViewById(R.id.background_main);
        View selectionBackground3 = view.findViewById(R.id.background_dessert);

        if (ShareFragment.editing) {
            if (ShareFragment.serveOrderChoice.equals("Món khai vị")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
            } else if (ShareFragment.serveOrderChoice.equals("Món chính")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
            } else if (ShareFragment.serveOrderChoice.equals("Món tráng miệng")) {
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner_selected);
            }
        }

        // Set onClickListener to each FrameLayout
        selection1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.serveOrderChoice = "Món khai vị";

                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
            }
        });

        selection2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.serveOrderChoice = "Món chính";
                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner_selected);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner);
            }
        });

        selection3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFragment.serveOrderChoice = "Món tráng miệng";
                // Change the background of the selected FrameLayout
                selectionBackground1.setBackgroundResource(R.drawable.make_corner);
                selectionBackground2.setBackgroundResource(R.drawable.make_corner);
                selectionBackground3.setBackgroundResource(R.drawable.make_corner_selected);
            }
        });
    }
}
