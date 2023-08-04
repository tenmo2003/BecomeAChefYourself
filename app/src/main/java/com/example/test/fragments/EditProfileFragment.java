package com.example.test.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.User;
import com.example.test.utils.ImageController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EditProfileFragment extends Fragment {
    private String username;
    private String fullname;
    private String bio;
    private ImageView userAvatar;
    private Uri imageURI;
    private boolean imageChanged;
    private final ActivityResultLauncher<PickVisualMediaRequest> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        imageChanged = true;
                        imageURI = uri;
                        userAvatar.setImageURI(uri);
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        imageChanged = false;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userAvatar = view.findViewById(R.id.user_avatar);
        Button backButton = view.findViewById(R.id.back_button);
        Button updateProfileButton = view.findViewById(R.id.update_profile_btn);
        TextView usernameTextView = view.findViewById(R.id.username);
        EditText fullnameEditText = view.findViewById(R.id.fullname_edit_text);
        EditText bioEditText = view.findViewById(R.id.bio_edit_text);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");
        fullname = args.getString("fullname");
        bio = args.getString("bio");

        AtomicReference<User> user = new AtomicReference<>();

        MainActivity.runTask(() -> {
            user.set(MainActivity.sqlConnection.getUserWithUsername(username));
        }, () -> {
            if (!user.get().getAvatarURL().equals("")) {
                Glide.with(requireActivity()).load(user.get().getAvatarURL()).into(userAvatar);
            }

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
                    //Hide device keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    boolean fullnameUpdate = !fullname.equals(fullnameEditText.getText().toString());
                    boolean bioUpdate = !bio.equals(bioEditText.getText().toString());

                    if (fullnameUpdate || bioUpdate || imageChanged) {
                        String imageURL = user.get().getAvatarURL();

                        fullname = fullnameEditText.getText().toString();
                        bio = bioEditText.getText().toString();

                        if (imageChanged) {
                            imageURL = "https://tenmo2003.000webhostapp.com/user_" + MainActivity.loggedInUser.getUsername() + "_" + new Random().nextInt() + ".jpg";
                        }

                        AtomicBoolean updateSuccess = new AtomicBoolean(false);

                        String finalImageURL1 = imageURL;
                        MainActivity.runTask(() -> {
                            updateSuccess.set(MainActivity.sqlConnection.updateProfile(username, fullname,
                                    bio, finalImageURL1, imageChanged));
                        }, () -> {
                            if (updateSuccess.get()) {
                                if (imageChanged) {
                                    int startIndex = finalImageURL1.indexOf("user");
                                    String finalImageURL = finalImageURL1.substring(startIndex);
                                    MainActivity.runTask(() -> {
                                        ImageController.uploadImage(imageURI, finalImageURL, getContext());
                                    }, () -> {
                                        MainActivity.toast.setText("Cập nhật thông tin thành công");
                                        MainActivity.toast.show();
                                    }, MainActivity.progressDialog);

                                    MainActivity.loggedInUser.setAvatarURL(finalImageURL1);
                                } else {
                                    MainActivity.toast.setText("Cập nhật trang cá nhân thành công");
                                    MainActivity.toast.show();
                                }
                            }
                        }, MainActivity.progressDialog);
                    }
                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigateUp();
                }
            });
        }, MainActivity.progressDialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
