package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.MailSender;

import java.util.concurrent.atomic.AtomicBoolean;

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
                SignUpFragment.fullname = usernameInput.getText().toString();

                if (SignUpFragment.email.equals("") || SignUpFragment.username.equals("") || SignUpFragment.password.equals("") || SignUpFragment.reenter.equals("")) {
                    MainActivity.toast.setText("Hãy nhập đủ thông tin");
                    MainActivity.toast.show();
                    return;
                }

                if (SignUpFragment.username.length() < 6) {
                    MainActivity.toast.setText("Tên đăng nhập phải có ít nhất 6 ký tự");
                    MainActivity.toast.show();
                    return;
                }

                if (SignUpFragment.password.length() < 8) {
                    MainActivity.toast.setText("Mật khẩu phải có ít nhất 8 ký tự");
                    MainActivity.toast.show();
                    return;
                }

                if (!SignUpFragment.reenter.equals(SignUpFragment.password)) {
                    MainActivity.toast.setText("Mật khẩu không trùng khớp");
                    MainActivity.toast.show();
                    return;
                }

                String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
                if (!SignUpFragment.email.matches(regex)) {
                    MainActivity.toast.setText("Email sai định dạng. Hãy kiểm tra lại");
                    MainActivity.toast.show();
                    return;
                }

                AtomicBoolean available = new AtomicBoolean(false);
                MainActivity.runTask(() -> {
                    available.set(MainActivity.sqlConnection.checkUsernameAvailability(SignUpFragment.username));
                }, () -> {
                    if (available.get()) {
                        MainActivity.runTask(() -> {
                            SignUpFragment.code = MailSender.sendVerificationMail(SignUpFragment.email);
                        }, null, null);
                        SignUpFragment.viewPager.setCurrentItem(SignUpFragment.viewPager.getCurrentItem() + 1, false);
                    } else {
                        MainActivity.toast.setText("Tên đăng nhập đã tồn tại! Vui lòng chọn tên đăng nhập khác");
                        MainActivity.toast.show();
                    }
                }, MainActivity.progressDialog);
            }
        });


    }
}