package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.adapters.SectionsPagerAdapter;
import com.example.test.utils.DatabaseHelper;

public class SignUpFragment extends Fragment {

    public static String email;
    public static String username;
    public static String password;
    public static String reenter;

    public static int code;
    public static ViewPager2 viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        viewPager = view.findViewById(R.id.view_pager);

        setUpViewPager(viewPager);
        viewPager.setUserInputEnabled(false);

        Button toLoginBtn = view.findViewById(R.id.to_login_button);

        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });

        return view;
    }

    private void setUpViewPager(ViewPager2 viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new SignUpStep1Fragment());
        adapter.addFragment(new SignUpStep2Fragment());

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
