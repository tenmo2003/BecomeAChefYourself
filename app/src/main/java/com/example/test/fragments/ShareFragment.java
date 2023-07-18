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
import com.example.test.databinding.FragmentShareBinding;

public class ShareFragment extends Fragment {
    private FragmentShareBinding binding;

    public static ViewPager2 viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);


        viewPager = view.findViewById(R.id.view_pager);
        setUpViewPager(viewPager);

        return view;
    }

    private void setUpViewPager(ViewPager2 viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new CreateStep1Fragment());
        adapter.addFragment(new CreateStep2Fragment());
        adapter.addFragment(new CreateStep3Fragment());

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
