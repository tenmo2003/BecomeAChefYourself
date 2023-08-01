package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.utils.SaveSharedPreference;

import java.util.concurrent.atomic.AtomicInteger;

public class LoginFragment extends Fragment {

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


        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView loginBtn = view.findViewById(R.id.login_button);
        ImageView toSignUpBtn = view.findViewById(R.id.to_sign_up_button);
        TextView forgotPasswordBtn = view.findViewById(R.id.forgot_password_btn);

        EditText usernameTv = view.findViewById(R.id.login_username_input);
        EditText passwordTv = view.findViewById(R.id.login_password_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameTv.getText().toString();
                String password = passwordTv.getText().toString();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(getActivity(), "Hãy nhập đủ thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicInteger result = new AtomicInteger();
                MainActivity.runTask(() -> {
                    result.set(MainActivity.sqlConnection.userAuthentication(username, password));
                }, () -> {
                    if (result.get() == 1) {
                        Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Save login status in preference file
                        SaveSharedPreference.setUserName(getContext(), usernameTv.getText().toString());

                        if (MainActivity.loggedInUser.getUsername().equals("admin")) {
                            Navigation.findNavController(view).navigate(R.id.navigation_admin);
                        } else {
                            Navigation.findNavController(view).navigate(R.id.navigation_profile);
                        }
                    } else if (result.get() == 0) {
                        Toast.makeText(getActivity(), "Đăng nhập thất bại! Hãy kiểm tra lại tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Đăng nhập thất bại! Người dùng đã bị cấm", Toast.LENGTH_SHORT).show();
                    }
                }, MainActivity.progressDialog);
            }
        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_sign_up);
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_forget_password);
            }
        });

//        Button test = view.findViewById(R.id.test_button);
//        TextView textView = view.findViewById(R.id.database_test);
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (MainActivity.sqlConnection != null) {
//                    AtomicReference<ResultSet> rsRef = new AtomicReference<>();
//                    Runner.runTask(() -> {
//                        ResultSet rs = MainActivity.sqlConnection.getDataQuery("SELECT * FROM users");
//                        rsRef.set(rs);
//                    }, () -> {
//                        ResultSet rs = rsRef.get();
//                        if (rs != null) {
//                            try {
//                                while (rs.next()) {
//                                    textView.setText(rs.getString(1));
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                try {
//                                    rs.close();
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }, getActivity());
//                }
//            }
//        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}