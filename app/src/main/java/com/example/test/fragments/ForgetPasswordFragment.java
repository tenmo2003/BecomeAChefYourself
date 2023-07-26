package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.adapters.SectionsPagerAdapter;
import com.example.test.components.User;

public class ForgetPasswordFragment extends Fragment {

    public static User forgotten;
    public static int code;
    public static ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        setUpViewPager(viewPager);
        viewPager.setUserInputEnabled(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setUpViewPager(ViewPager2 viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new ForgetStep1Fragment());
        adapter.addFragment(new ForgetStep2Fragment());
        adapter.addFragment(new ForgetStep3Fragment());

        viewPager.setAdapter(adapter);
    }
}
