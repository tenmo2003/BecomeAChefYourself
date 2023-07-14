package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;

public class UserFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    View fragmentView;
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


        fragmentView = inflater.inflate(R.layout.fragment_user, container, false);

//        Button logoutBtn = view.findViewById(R.id.logout_button);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Logout");
//                builder.setMessage("Are you sure you want to log out?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Perform logout action here
//                        MainActivity.loggedInUser = null;
//                        Navigation.findNavController(view).navigate(R.id.navigation_login);
//                    }
//                });
//                builder.setNegativeButton("No", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView usernameTv = view.findViewById(R.id.user_username);
        EditText fullnameTv = view.findViewById(R.id.user_fullname);
        TextView bioTv = view.findViewById(R.id.user_bio);

        usernameTv.setText(MainActivity.loggedInUser.getUsername());
        fullnameTv.setText(MainActivity.loggedInUser.getFullname());
        bioTv.setText(MainActivity.loggedInUser.getBio());

        Button popupMenu = view.findViewById(R.id.user_menu);

        popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.user_popup_menu);
        popup.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.edit_profile_menu) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform logout action here
                    MainActivity.loggedInUser = null;
                    Navigation.findNavController(fragmentView).navigate(R.id.navigation_login);
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
}
