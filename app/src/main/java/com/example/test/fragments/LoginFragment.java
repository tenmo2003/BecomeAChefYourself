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
import com.example.test.components.User;
import com.example.test.database.DatabaseHelper;
import com.example.test.databinding.FragmentUserBinding;

public class LoginFragment extends Fragment {

    private FragmentUserBinding binding;

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

        Button loginBtn = view.findViewById(R.id.login_button);
        Button toSignUpBtn = view.findViewById(R.id.to_sign_up_button);

        EditText usernameTv = view.findViewById(R.id.login_username_input);
        EditText passwordTv = view.findViewById(R.id.login_password_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                User user = dbHelper.userAuthentication(usernameTv.getText().toString(), passwordTv.getText().toString());

                if (user != null) {
                    Toast.makeText(getActivity(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.navigation_user);
                } else {
                    Toast.makeText(getActivity(), "Logged in failed! Check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_sign_up);
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