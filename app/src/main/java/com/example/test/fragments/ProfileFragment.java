package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.SectionsPagerAdapter;
import com.example.test.components.User;
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.SaveSharedPreference;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private View fragmentView;

    ViewPager2 viewPager2;
    TabLayout tabLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    public User profileUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_user, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            profileUser = dbHelper.getUserWithUsername(username);
        } else {
            //reload profile if change
            profileUser = dbHelper.getUserWithUsername(MainActivity.loggedInUser.getUsername());
        }

        if (profileUser.getUsername().equals(MainActivity.loggedInUser.getUsername())) {
            MainActivity.navView.setItemActiveIndex(3);
        }

        TextView usernameTv = view.findViewById(R.id.user_username);
        TextView fullnameTv = view.findViewById(R.id.user_fullname);
        TextView bioTv = view.findViewById(R.id.user_bio);
        RelativeLayout followBtn = view.findViewById(R.id.follow_btn);
        ImageView popupMenu = view.findViewById(R.id.user_menu);
        ImageView userAvatar = view.findViewById(R.id.user_avatar);

        ProgressBar progressBar = view.findViewById(R.id.progressbar);

        if (!profileUser.getAvatarURL().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(profileUser.getAvatarURL()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(userAvatar);
        }

        List<Integer> stats = dbHelper.getUserProfileStats(profileUser.getUsername());
        TextView postCountTv = view.findViewById(R.id.post_count);
        TextView followerCountTv = view.findViewById(R.id.follower_count);
        TextView likeCountTv = view.findViewById(R.id.likes_count);
        postCountTv.setText(String.valueOf(stats.get(0)));
        followerCountTv.setText(String.valueOf(stats.get(1)));
        likeCountTv.setText(String.valueOf(stats.get(2)));

        usernameTv.setText(profileUser.getUsername());
        fullnameTv.setText(profileUser.getFullname());
        bioTv.setText(profileUser.getBio());

        if (MainActivity.loggedInUser != null && MainActivity.loggedInUser.getUsername().equals(profileUser.getUsername())) {
            popupMenu(popupMenu);
            followBtn.setVisibility(View.INVISIBLE);
        } else {
            popupMenu.setVisibility(View.INVISIBLE);
            followBtn.setVisibility(View.VISIBLE);
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
            if (MainActivity.loggedInUser == null || !databaseHelper.isFollowing(MainActivity.loggedInUser.getUsername(), profileUser.getUsername())) {
                View follow_frame = view.findViewById(R.id.follow_frame);
                follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.follow_frame, null));
                TextView follow_text = view.findViewById(R.id.follow_text);
                follow_text.setText("Theo dõi");
            } else {
                View follow_frame = view.findViewById(R.id.follow_frame);
                follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.following_frame, null));
                TextView follow_text = view.findViewById(R.id.follow_text);
                follow_text.setText("Đang theo dõi");
            }

            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.loggedInUser == null) {
                        Toast.makeText(getActivity(), "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.navigation_login);
                        return;
                    }

                    if (!databaseHelper.isFollowing(MainActivity.loggedInUser.getUsername(), profileUser.getUsername())) {
                        boolean result = databaseHelper.addFollow(MainActivity.loggedInUser.getUsername(), profileUser.getUsername());

                        if (result) {
                            View follow_frame = view.findViewById(R.id.follow_frame);
                            follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.following_frame, null));
                            TextView follow_text = view.findViewById(R.id.follow_text);
                            follow_text.setText("Following");
                            followerCountTv.setText(String.valueOf(Integer.parseInt(followerCountTv.getText().toString()) + 1));
                        }
                    } else {
                        boolean result = databaseHelper.removeFollow(MainActivity.loggedInUser.getUsername(), profileUser.getUsername());

                        if (result) {
                            View follow_frame = view.findViewById(R.id.follow_frame);
                            follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.follow_frame, null));
                            TextView follow_text = view.findViewById(R.id.follow_text);
                            follow_text.setText("Follow");
                            followerCountTv.setText(String.valueOf(Integer.parseInt(followerCountTv.getText().toString()) - 1));
                        }
                    }
                }
            });
        }
        viewPager2 = fragmentView.findViewById(R.id.view_pager);
        tabLayout = fragmentView.findViewById(R.id.tabLayout);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.black));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getActivity(), R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getActivity(), R.color.darkGray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupViewPager(viewPager2);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setIcon(R.drawable.gray_your_posts_24);
            } else {
                tab.setIcon(R.drawable.gray_bookmark_border_24);
            }
        }).attach();
    }

    public void popupMenu(ImageView popupMenu) {

        popupMenu.setVisibility(View.VISIBLE);

        PopupMenu popup = new PopupMenu(getActivity(), popupMenu);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.user_popup_menu);

        popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.edit_profile_menu) {
            Bundle args = new Bundle();
            args.putString("username", profileUser.getUsername());
            args.putString("fullname", profileUser.getFullname());
            args.putString("bio", profileUser.getBio());

            Navigation.findNavController(fragmentView).navigate(R.id.navigation_edit_profile, args);
            return true;
        } else if (menuItem.getItemId() == R.id.change_password) {
            Bundle args = new Bundle();
            args.putString("username", profileUser.getUsername());
            Navigation.findNavController(fragmentView).navigate(R.id.navigation_change_password, args);
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Đăng xuất");
            builder.setMessage("Bạn chắc chắn muốn đăng xuất chứ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform logout action here
                    MainActivity.loggedInUser = null;
                    // Clear preference file
                    SaveSharedPreference.setUserName(getContext(), "");

                    Navigation.findNavController(fragmentView).navigate(R.id.navigation_login);
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();

            // Get the positive and negative buttons
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Set the text color of the positive button
            positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

            // Set the text color of the negative button
            negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));
            return true;
        }
    }

    private void setupViewPager(ViewPager2 viewPager2) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        adapter.addFragment(new ProfilePostFragment(profileUser));
        if (MainActivity.loggedInUser != null && MainActivity.loggedInUser.getUsername().equals(profileUser.getUsername())) {
            adapter.addFragment(new ProfileSavedFragment(profileUser));
        }
        viewPager2.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileUser.getUsername().equals(MainActivity.loggedInUser.getUsername())) {
            MainActivity.navView.setItemActiveIndex(3);
        }
    }
}
