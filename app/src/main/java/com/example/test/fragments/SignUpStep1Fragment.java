package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.MailSender;

public class SignUpStep1Fragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText emailInput = view.findViewById(R.id.sign_up_email_input);
        EditText usernameInput = view.findViewById(R.id.sign_up_username_input);
        EditText passwordInput = view.findViewById(R.id.sign_up_password_input);
        EditText reenterInput = view.findViewById(R.id.sign_up_re_enter_password);

        Button signUpBtn = view.findViewById(R.id.sign_up_button);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment.email = emailInput.getText().toString();
                SignUpFragment.username = usernameInput.getText().toString();
                SignUpFragment.password = passwordInput.getText().toString();
                SignUpFragment.reenter = reenterInput.getText().toString();

                if (SignUpFragment.email.equals("") || SignUpFragment.username.equals("") || SignUpFragment.password.equals("") || SignUpFragment.reenter.equals("")) {
                    Toast.makeText(getActivity(), "Hãy nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!SignUpFragment.reenter.equals(SignUpFragment.password)) {
                    Toast.makeText(getActivity(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                if (dbHelper.checkUsernameAvailability(SignUpFragment.username)) {
                    MainActivity.runTask(() -> {
                        SignUpFragment.code = MailSender.sendVerificationMail(SignUpFragment.email);
                    }, null, null);
                    SignUpFragment.viewPager.setCurrentItem(SignUpFragment.viewPager.getCurrentItem() + 1, false);
                } else {
                    Toast.makeText(getActivity(), "Tên đăng nhập đã tồn tại! Vui lòng chọn tên đăng nhập khác", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}