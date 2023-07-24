package com.example.test.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.utils.DatabaseHelper;

public class EditProfileFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private String username;
    private String fullname;
    private String bio;
    private ImageView userAvatar;
    private final ActivityResultLauncher<PickVisualMediaRequest> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        userAvatar.setImageURI(uri);
                    } else {
                        return;
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getContext());

        userAvatar = view.findViewById(R.id.user_avatar);
        Button backButton = view.findViewById(R.id.back_button);
        Button updateProfileButton = view.findViewById(R.id.update_profile_btn);
        TextView usernameTextView = view.findViewById(R.id.username);
        EditText fullnameEditText = view.findViewById(R.id.fullname_edit_text);
        EditText bioEditText = view.findViewById(R.id.bio_edit_text);
        EditText oldPasswordEditText = view.findViewById(R.id.old_password);
        EditText newPasswordEditText = view.findViewById(R.id.new_password);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");
        fullname = args.getString("fullname");
        bio = args.getString("bio");

        usernameTextView.setText(username);
        fullnameEditText.setText(fullname);
        bioEditText.setText(bio);

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityIntent.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean fullnameUpdate = !fullname.equals(fullnameEditText.getText().toString());
                boolean bioUpdate = !bio.equals(bioEditText.getText().toString());
                boolean oldPasswordChange = !oldPasswordEditText.getText().toString().equals("");
                boolean newPasswordChange = !newPasswordEditText.getText().toString().equals("");
                String oldPassword = "";
                String newPassword = "";

                if (oldPasswordChange && newPasswordChange) {
                    if (oldPasswordEditText.getText().toString().equals(newPasswordEditText.getText().toString())) {
                        Toast.makeText(getContext(), "Mật khẩu mới không được trùng với mật khẩu cũ", Toast.LENGTH_SHORT).show();
                    } else {
                        oldPassword = oldPasswordEditText.getText().toString();
                        newPassword = newPasswordEditText.getText().toString();
                    }
                } else if (oldPasswordChange ^ newPasswordChange) {
                    if (!oldPasswordChange) {
                        Toast.makeText(getContext(), "Hãy nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Hãy nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                    }
                }

                int updateSuccess = dbHelper.updateProfile(username, fullnameEditText.getText().toString(), fullnameUpdate,
                        bioEditText.getText().toString(), bioUpdate, oldPassword, newPassword);

                if (updateSuccess == 1) {
                    Toast.makeText(getContext(), "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();
                    fullname = fullnameEditText.getText().toString();
                    bio = bioEditText.getText().toString();
                } else if (updateSuccess == -1) {
                    Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }

                //clear focus and hide device keyboard
                View v = getActivity().getCurrentFocus();
                if (v instanceof EditText) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                oldPasswordEditText.setText("");
                newPasswordEditText.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
