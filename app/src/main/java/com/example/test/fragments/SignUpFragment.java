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

import com.example.test.R;
import com.example.test.utils.DatabaseHelper;

public class SignUpFragment extends Fragment {
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


        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText usernameInput = view.findViewById(R.id.sign_up_username_input);
        EditText passwordInput = view.findViewById(R.id.sign_up_password_input);
        EditText reenterInput = view.findViewById(R.id.sign_up_re_enter_password);

        Button signUpBtn = view.findViewById(R.id.sign_up_button);
        Button toLoginBtn = view.findViewById(R.id.to_login_button);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reenterInput.getText().toString().equals(passwordInput.getText().toString())) {
                    Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                boolean signUpResult = dbHelper.signUpUser(usernameInput.getText().toString(), passwordInput.getText().toString(), usernameInput.getText().toString());

                if (signUpResult) {
                    Toast.makeText(getActivity(), "Signed up successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.navigation_login);
                } else {
                    Toast.makeText(getActivity(), "Username is taken! Please choose another one", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });
    }
}
