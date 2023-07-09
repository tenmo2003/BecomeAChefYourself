package com.example.test.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.database.DatabaseHelper;
import com.example.test.databinding.FragmentUserBinding;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        UserViewModel userViewModel =
//                new ViewModelProvider(this).get(UserViewModel.class);
//
//        binding = FragmentUserBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textUser;
//        userViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;


        View view = inflater.inflate(R.layout.fragment_user, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button signUpBtn = view.findViewById(R.id.signup_button);
        Button loginBtn = view.findViewById(R.id.login_button);

        EditText usernameSignUp = view.findViewById(R.id.username_signup_input);
        EditText passwordSignUp = view.findViewById(R.id.password_signup_input);
        EditText fullnameSignUp = view.findViewById(R.id.fullname_signup_input);
        EditText usernameLogin = view.findViewById(R.id.username_login_input);
        EditText passwordLogin = view.findViewById(R.id.password_login_input);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                boolean signUpResult = dbHelper.signUpUser(usernameSignUp.getText().toString(), passwordSignUp.getText().toString(), fullnameSignUp.getText().toString());

                if (signUpResult) {
                    Toast.makeText(getActivity(), "Sign up successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Sign up failed. Username already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                boolean signUpResult = dbHelper.userAuthentication(usernameLogin.getText().toString(), passwordLogin.getText().toString());

                if (signUpResult) {
                    Toast.makeText(getActivity(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Logged in failed! Check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}