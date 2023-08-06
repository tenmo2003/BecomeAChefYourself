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

import java.util.concurrent.atomic.AtomicBoolean;


public class ForgetStep3Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_step3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button nextBtn = view.findViewById(R.id.next_btn);
        EditText passwordInput = view.findViewById(R.id.password_input);
        EditText repasswordInput = view.findViewById(R.id.re_password_input);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordInput.getText().toString().length() < 8) {
                    MainActivity.toast.setText("Mật khẩu phải có ít nhất 8 ký tự");
                    MainActivity.toast.show();
                    return;
                }

                if (passwordInput.getText().toString().equals(repasswordInput.getText().toString())) {
                    AtomicBoolean result = new AtomicBoolean();
                    MainActivity.runTask(() -> {
                        result.set(MainActivity.sqlConnection.changePassword(ForgetPasswordFragment.forgotten.getUsername(), passwordInput.getText().toString()));
                    }, () -> {
                        if (result.get()) {
                            MainActivity.toast.setText("Đổi mật khẩu thành công!");
                            MainActivity.toast.show();
                        } else {
                            MainActivity.toast.setText("Đổi mật khẩu thất bại!");
                            MainActivity.toast.show();
                        }

                    }, MainActivity.progressDialog);
                    Navigation.findNavController(view).navigate(R.id.navigation_login);
                } else {
                    MainActivity.toast.setText("Mật khẩu không giống nhau! Vui lòng kiểm tra lại");
                    MainActivity.toast.show();
                }
            }
        });
    }
}