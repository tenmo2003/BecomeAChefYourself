package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.MailSender;

import java.util.concurrent.atomic.AtomicBoolean;


public class SignUpStep2Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_step2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView notifyTv = view.findViewById(R.id.email_sent_text);

        notifyTv.setText("Một mã xác thực đã được gửi về email " + SignUpFragment.email);

        Button nextBtn = view.findViewById(R.id.next_btn);
        EditText codeInput = view.findViewById(R.id.code_input);
        TextView resendBtn = view.findViewById(R.id.resend_btn);

        final int cooldownTimeInSeconds = 60; // Thời gian chờ đếm ngược (60 giây trong ví dụ này)
        final CountDownTimer[] countDownTimer = new CountDownTimer[1];

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Vô hiệu hóa nút khi người dùng ấn gửi lại
                resendBtn.setEnabled(false);
                startCountDown(cooldownTimeInSeconds);

                // Gửi lại email xác thực và nhận mã mới
                MainActivity.runTask(() -> {
                    SignUpFragment.code = MailSender.sendVerificationMail(SignUpFragment.email);
                }, null, null);
            }

            private void startCountDown(int timeInSeconds) {
                countDownTimer[0] = new CountDownTimer(timeInSeconds * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long secondsLeft = millisUntilFinished / 1000;
                        resendBtn.setText("Gửi lại\n(" + secondsLeft + ")");
                    }

                    public void onFinish() {
                        // Khi đếm ngược kết thúc, cho phép người dùng gửi lại và đặt lại nội dung của nút
                        resendBtn.setEnabled(true);
                        resendBtn.setText("Gửi lại");
                    }
                }.start();
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(codeInput.getText().toString()) == SignUpFragment.code) {
                    AtomicBoolean signUpResult = new AtomicBoolean(false);
                    MainActivity.runTask(() -> {
                        signUpResult.set(MainActivity.sqlConnection.signUpUser(SignUpFragment.email, SignUpFragment.username, SignUpFragment.password, SignUpFragment.fullname));
                    }, () -> {
                        if (signUpResult.get()) {
                            Toast.makeText(getActivity(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.navigation_login);
                        } else {
                            Toast.makeText(getActivity(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }, MainActivity.progressDialog);
                } else {
                    Toast.makeText(getActivity(), "Mã xác thực sai! Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}