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


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(codeInput.getText().toString()) == ForgetPasswordFragment.code) {
                    ForgetPasswordFragment.viewPager.setCurrentItem(ForgetPasswordFragment.viewPager.getCurrentItem() + 1, false);
                } else {
                    Toast.makeText(getActivity(), "Mã xác thực sai! Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
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