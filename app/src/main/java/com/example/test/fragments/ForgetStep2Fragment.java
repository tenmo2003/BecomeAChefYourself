package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.MailSender;


public class ForgetStep2Fragment extends Fragment {

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

        notifyTv.setText("Một mã xác thực đã được gửi về email " + formatEmail(ForgetPasswordFragment.forgotten.getEmail()));

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
                    ForgetPasswordFragment.code = MailSender.sendResetPasswordMail(ForgetPasswordFragment.forgotten.getEmail());
                }, null, null);
            }

            private void startCountDown(int timeInSeconds) {
                countDownTimer[0] = new CountDownTimer(timeInSeconds * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long secondsLeft = millisUntilFinished / 1000;
                        resendBtn.setText("Gửi lại (" + secondsLeft + ")");
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
                if (codeInput.getText().toString().length() < 6) {
                    MainActivity.toast.setText("Mã xác thực phải bao gồm 6 số");
                    MainActivity.toast.show();
                    return;
                }

                if (Integer.parseInt(codeInput.getText().toString()) == ForgetPasswordFragment.code) {
                    ForgetPasswordFragment.viewPager.setCurrentItem(ForgetPasswordFragment.viewPager.getCurrentItem() + 1, false);
                } else {
                    MainActivity.toast.setText("Mã xác thực sai! Vui lòng kiểm tra lại");
                    MainActivity.toast.show();
                }
            }
        });
    }

    public String formatEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf("@");
        int dotIndex = email.lastIndexOf(".");
        if (atIndex == -1 || dotIndex == -1 || dotIndex < atIndex) {
            return email;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1, dotIndex);
        String extension = email.substring(dotIndex);

        StringBuilder formattedEmail = new StringBuilder();
        formattedEmail.append(username.substring(0, Math.min(3, username.length()))); // Take the first 3 characters of username
        formattedEmail.append("***@***"); // Append the asterisks and "@" symbol followed by "***"
        formattedEmail.append(extension); // Append the extension

        return formattedEmail.toString();
    }
}