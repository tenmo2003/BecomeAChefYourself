package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.CustomSpinner;
import com.example.test.R;
import com.example.test.SelectAdapter;
import com.example.test.inventory.Data;

public class CreateStep3Fragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener {

    private CustomSpinner spinner_select;
    private SelectAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post_3, container, false);


        spinner_select = view.findViewById(R.id.spinner_select);

        spinner_select.setSpinnerEventsListener(this);

        adapter = new SelectAdapter(getActivity(), Data.getSelectList());
        spinner_select.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout nextStepBtn = view.findViewById(R.id.next_steps_btn);

        ImageView backBtn = view.findViewById(R.id.back_button);

//        nextStepBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() + 1);
//            }
//        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareFragment.viewPager.setCurrentItem(ShareFragment.viewPager.getCurrentItem() - 1);
            }
        });

    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinner_select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinner_select.setBackground(getResources().getDrawable(R.drawable.bg_spinner));
    }
}
