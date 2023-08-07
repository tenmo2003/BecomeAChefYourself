package com.example.test.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.User;
import com.example.test.utils.ImageController;
import com.example.test.utils.MailSender;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EditProfileFragment extends Fragment {
    private String username;
    private String fullname;
    private String bio;
    private String email;
    private ImageView userAvatar;
    private Uri imageURI;
    private boolean imageChanged;
    private int code = -15;
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
        EditText emailEditText = view.findViewById(R.id.email_edit_text);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");
        fullname = args.getString("fullname");
        bio = args.getString("bio");
        email = args.getString("email");

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
            emailEditText.setText(email);

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
                    if (fullnameEditText.getText().toString().length() < 2) {
                        MainActivity.toast.setText("Tên người dùng phải có ít nhất 2 ký tự");
                        MainActivity.toast.show();
                        return;
                    }

                    String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
                    if (!emailEditText.getText().toString().matches(regex)) {
                        MainActivity.toast.setText("Email sai định dạng. Hãy kiểm tra lại");
                        MainActivity.toast.show();
                        return;
                    }

                    //Hide device keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    boolean fullnameUpdate = !fullname.equals(fullnameEditText.getText().toString());
                    boolean bioUpdate = !bio.equals(bioEditText.getText().toString());
                    boolean emailUpdate = !email.equals(emailEditText.getText().toString());

                    if (emailUpdate) {
                        MainActivity.runTask(() -> {
                            code = MailSender.sendUpdateEmailVerification(emailEditText.getText().toString());
                        });

                        // Create an EditText for user to enter the verification code
                        EditText codeEditText = new EditText(getActivity());
                        codeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        codeEditText.setHint("Nhập mã xác thực gửi về email");

                        // Create the AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Xác thực email");
                        builder.setView(codeEditText);


                        // Set up the positive button (OK)
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (codeEditText.getText().toString().length() < 6) {
                                    MainActivity.toast.setText("Mã xác thực phải có 6 số");
                                    MainActivity.toast.show();
                                    return;
                                }

                                int codeInput = Integer.parseInt(codeEditText.getText().toString());

                                if (codeInput == code) {
                                    String imageURL = user.get().getAvatarURL();
                                    fullname = fullnameEditText.getText().toString();
                                    bio = bioEditText.getText().toString();
                                    email = emailEditText.getText().toString();

                                    if (imageChanged) {
                                        imageURL = "https://tenmo2003.000webhostapp.com/user_" + MainActivity.loggedInUser.getUsername() + "_" + new Random().nextInt() + ".jpg";
                                    }

                                    AtomicBoolean updateSuccess = new AtomicBoolean(false);

                                    String finalImageURL1 = imageURL;
                                    MainActivity.runTask(() -> {
                                        updateSuccess.set(MainActivity.sqlConnection.updateProfile(username, fullname,
                                                bio, email, finalImageURL1, imageChanged));
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
                                    } else {
                                        MainActivity.toast.setText("Mã xác thực sai. Vui lòng nhập lại");
                                        MainActivity.toast.show();
                                    }
                                }
                            });

                            // Set up the negative button (Cancel)
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle cancel action if needed
                                }
                            });

                            // Show the AlertDialog
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            // Get the positive and negative buttons
                            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                            // Set the text color of the positive button
                            positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

                            // Set the text color of the negative button
                            negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

                    } else if (fullnameUpdate || bioUpdate || imageChanged) {
                        String imageURL = user.get().getAvatarURL();

                        fullname = fullnameEditText.getText().toString();
                        bio = bioEditText.getText().toString();
                        email = emailEditText.getText().toString();

                        if (imageChanged) {
                            imageURL = "https://tenmo2003.000webhostapp.com/user_" + MainActivity.loggedInUser.getUsername() + "_" + new Random().nextInt() + ".jpg";
                        }

                        AtomicBoolean updateSuccess = new AtomicBoolean(false);

                        String finalImageURL1 = imageURL;
                        MainActivity.runTask(() -> {
                            updateSuccess.set(MainActivity.sqlConnection.updateProfile(username, fullname,
                                    bio, email, finalImageURL1, imageChanged));
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
