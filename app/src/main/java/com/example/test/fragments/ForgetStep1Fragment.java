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
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.MailSender;


public class ForgetStep1Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button nextBtn = view.findViewById(R.id.next_btn);
        EditText usernameInput = view.findViewById(R.id.username_input);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetPasswordFragment.forgotten = dbHelper.getUserWithUsername(usernameInput.getText().toString());

                if (ForgetPasswordFragment.forgotten == null) {
                    Toast.makeText(getActivity(), "Không tồn tại người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }

                MainActivity.runTask(() -> {
                    ForgetPasswordFragment.code = MailSender.sendResetPasswordMail(ForgetPasswordFragment.forgotten.getEmail());
                }, null, null);

                ForgetPasswordFragment.viewPager.setCurrentItem(ForgetPasswordFragment.viewPager.getCurrentItem() + 1, false);
            }
        });
    }
}