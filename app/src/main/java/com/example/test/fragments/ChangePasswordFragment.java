package com.example.test.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.utils.DatabaseHelper;

public class ChangePasswordFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = view.findViewById(R.id.back_button);
        Button changePasswordButton = view.findViewById(R.id.change_password_btn);
        EditText oldPasswordEditText = view.findViewById(R.id.old_password);
        EditText newPasswordEditText = view.findViewById(R.id.new_password);
        EditText renewPasswordEditText = view.findViewById(R.id.re_new_password);

        Bundle args = getArguments();
        assert args != null;
        String username = args.getString("username");

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide device keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String renewPassword = renewPasswordEditText.getText().toString();

                int oldPasswordChange = (!oldPassword.equals("")) ? 1 : 0;
                int newPasswordChange = (!newPassword.equals("")) ? 1 : 0;
                int renewPasswordChange = (!renewPassword.equals("")) ? 1 : 0;

                if (oldPasswordChange + newPasswordChange + renewPasswordChange == 0) {
                    return;
                }

                if (oldPasswordChange + newPasswordChange + renewPasswordChange == 3) {
                    if (oldPassword.equals(newPassword)) {
                        Toast.makeText(getContext(), "Mật khẩu mới không được trùng với mật khẩu cũ", Toast.LENGTH_SHORT).show();
                    } else if (!newPassword.equals(renewPassword)) {
                        Toast.makeText(getContext(), "Nhập lại mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        if (dbHelper.changePassword(username, oldPassword, newPassword)) {
                            Toast.makeText(getContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Hãy nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }

                oldPasswordEditText.setText("");
                newPasswordEditText.setText("");
                renewPasswordEditText.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }
}
