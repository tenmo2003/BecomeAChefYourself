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

        EditText emailInput = view.findViewById(R.id.sign_up_email_input);
        EditText usernameInput = view.findViewById(R.id.sign_up_username_input);
        EditText passwordInput = view.findViewById(R.id.sign_up_password_input);
        EditText reenterInput = view.findViewById(R.id.sign_up_re_enter_password);

        Button signUpBtn = view.findViewById(R.id.sign_up_button);
        Button toLoginBtn = view.findViewById(R.id.to_login_button);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String reenter = reenterInput.getText().toString();

                if (email.equals("") || username.equals("") || password.equals("") || reenter.equals("")) {
                    Toast.makeText(getActivity(), "Hãy nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!reenter.equals(password)) {
                    Toast.makeText(getActivity(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                boolean signUpResult = dbHelper.signUpUser(email, username, password, reenter);

                if (signUpResult) {
                    Toast.makeText(getActivity(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.navigation_login);
                } else {
                    Toast.makeText(getActivity(), "Tên đăng nhập đã tồn tại, vui lòng chọn một tên khác", Toast.LENGTH_SHORT).show();
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
