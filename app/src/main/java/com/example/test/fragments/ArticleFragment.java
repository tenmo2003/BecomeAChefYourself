package com.example.test.fragments;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.adapters.CommentListAdapter;
import com.example.test.components.Article;
import com.example.test.components.Comment;
import com.example.test.components.User;
import com.example.test.utils.DatabaseHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ArticleFragment extends Fragment {
    List<Comment> commentList;
    CommentListAdapter commentListAdapter;
    RecyclerView commentListView;
    DatabaseHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());

        TextView title = view.findViewById(R.id.title_in_article);
        TextView recipeContentTextView = view.findViewById(R.id.recipe_text);
        TextView ingredientsTextView = view.findViewById(R.id.ingredients_text);
        TextView publisherTextView = view.findViewById(R.id.publisher_text);
        TextView publishedDateTextView = view.findViewById(R.id.published_date_text);
        TextView timeToMakeTextView = view.findViewById(R.id.time_to_make_text);
        TextView reactTextView = view.findViewById(R.id.react_count);
        TextView commentTextView = view.findViewById(R.id.cmt_count);
        ImageView authorAvatar = view.findViewById(R.id.author_avatar_in_article);
        ImageView viewAuthorProfile = view.findViewById(R.id.view_author_profile);
        ImageView userAvatar = view.findViewById(R.id.user_avatar_in_article);
        ImageView reactBtn = view.findViewById(R.id.react_btn);
        EditText commentEditText = view.findViewById(R.id.comment_edit_text);
        Button sendCommentBtn = view.findViewById(R.id.send_comment_btn);
        ImageView bookmark = view.findViewById(R.id.bookmark_in_article);
        TextView reportBtn = view.findViewById(R.id.report_btn);
        TextView removeBtn = view.findViewById(R.id.remove_btn);
        TextView editBtn = view.findViewById(R.id.edit_btn);
        TextView ingredientsCount = view.findViewById(R.id.ingredients_count);
        TextView authorRecipes = view.findViewById(R.id.author_recipes);
        TextView authorFollowers = view.findViewById(R.id.author_followers);

        Bundle args = getArguments();
        assert args != null;
        int articleID = 0;
        if (args.containsKey("articleID")) {
             articleID = args.getInt("articleID");
        } else if (args.containsKey("reportID")) {
            articleID = dbHelper.getArticleIDWithReportID(args.getInt("reportID"));
        }
        int finalArticleID = articleID;

        Article article = dbHelper.getArticleWithId(articleID);

        final boolean[] isBookmark = {MainActivity.loggedInUser != null && dbHelper.checkBookmarked(MainActivity.loggedInUser.getUsername(), articleID)};
        if (isBookmark[0]) {
            bookmark.setImageResource(R.drawable.bookmarked);
        } else {
            bookmark.setImageResource(R.drawable.bookmark_in_article);
        }
        HomeFragment.bookmarked = isBookmark[0];

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isBookmark[0]) {
                    isBookmark[0] = true;
                    dbHelper.addBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    bookmark.setImageResource(R.drawable.bookmarked);
                    HomeFragment.bookmarked = true;
                } else {
                    isBookmark[0] = false;
                    dbHelper.removeBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                    bookmark.setImageResource(R.drawable.bookmark_in_article);
                    HomeFragment.bookmarked = false;
                }
            }
        });

        ImageView dishImg = view.findViewById(R.id.dish_img);

        //test data
        User author = dbHelper.getUserWithUsername(article.getPublisher());
        if (author.getAvatarURL().equals("")) {
            authorAvatar.setImageResource(R.drawable.baseline_person_24);
        } else {
            ProgressBar progress = view.findViewById(R.id.avatar_progressbar);
            progress.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(author.getAvatarURL()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progress.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progress.setVisibility(View.GONE);
                    return false;
                }
            }).into(authorAvatar);

        }

        if (MainActivity.loggedInUser != null && (MainActivity.loggedInUser.getUsername().equals("admin") || MainActivity.loggedInUser.getUsername().equals(author.getUsername()))) {
            removeBtn.setVisibility(View.VISIBLE);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Xoá bài");
                    builder.setMessage("Bạn chắc chắn muốn xoá bài viết '" + article.getDishName() + "' chứ?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.removeArticle(finalArticleID);
                            if (MainActivity.loggedInUser.getUsername().equals("admin")) {
                                dbHelper.increaseReportLevelForUser(article.getPublisher());
                            }
                            HomeFragment.deleted = true;
                            Navigation.findNavController(view).navigateUp();
                        }
                    });
                    builder.setNegativeButton("Huỷ", null);
                    AlertDialog dialog = builder.create();

                    dialog.show();

                    // Get the positive and negative buttons
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    // Set the text color of the positive button
                    positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

                    // Set the text color of the negative button
                    negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));
                }
            });
        }

        MainActivity.runTask(() -> {
            ResultSet rs = MainActivity.sqlConnection.getDataQuery("SELECT CURRENT_TIMESTAMP;");
            try {
                if (rs.next()) {
                    String test = rs.getString(1);
                    Log.i("Database TEST", test);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, null, null);

        final boolean[] isLike = {false};


        if (MainActivity.loggedInUser != null) {
            if (!MainActivity.loggedInUser.getAvatarURL().equals("")) {
                Glide.with(getActivity()).load(MainActivity.loggedInUser.getAvatarURL()).into(userAvatar);
            } else {
                userAvatar.setImageResource(R.drawable.baseline_person_24);
            }

            isLike[0] = dbHelper.checkLiked(MainActivity.loggedInUser.getUsername(), String.valueOf(articleID));
            if (isLike[0]) {
                reactBtn.setImageResource(R.drawable.reacted);
            } else {
                reactBtn.setImageResource(R.drawable.react);
            }
        } else {
            userAvatar.setImageResource(R.drawable.ban);
            reactBtn.setImageResource(R.drawable.react);
        }

        ProgressBar progressBar = view.findViewById(R.id.progressbar);
        if (article.getImgURL().equals("")) {
            dishImg.setImageResource(R.drawable.no_preview);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(article.getImgURL()).listener(new RequestListener<Drawable>() {
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
            }).into(dishImg);
        }

        title.setText(article.getDishName());
        recipeContentTextView.setText(Html.fromHtml(article.getRecipe(), Html.FROM_HTML_MODE_COMPACT));
        ingredientsTextView.setText(formatIngredients(article.getIngredients(), view));
        ingredientsCount.setText(article.getIngredients().split(";\\s*").length + " thành phần");
        publishedDateTextView.setText(postedTime(article.getPublishedTime()));
        publisherTextView.setText(article.getPublisher());
        authorFollowers.setText(dbHelper.getTotalFollowCount(author.getUsername()) + " followers");
        authorRecipes.setText(dbHelper.getTotalArticleCount(author.getUsername()) + " recipes");
        timeToMakeTextView.setText(article.getTimeToMake());
        reactTextView.setText(article.getLikes() + " lượt thích");
        commentTextView.setText(article.getComments() + " bình luận");

        commentListAdapter = new CommentListAdapter();
        commentList = dbHelper.getCommentWithArticleID(String.valueOf(articleID));
        commentListAdapter.setComments(commentList);
        commentListAdapter.setContext(getActivity());
        commentListAdapter.setDbHelper(dbHelper);

        commentListView = view.findViewById(R.id.comment_list);
        commentListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        commentListView.setHasFixedSize(true);
        commentListView.setAdapter(commentListAdapter);

        viewAuthorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();

                args.putString("username", publisherTextView.getText().toString());

                Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
            }
        });

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        reactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get react count number
                int react_count = Integer.parseInt(reactTextView.getText().toString().split(" ")[0]);

                if (isLike[0]) {
                    isLike[0] = false;
                    dbHelper.removeLike(MainActivity.loggedInUser.getUsername(), finalArticleID);
                    reactBtn.setImageResource(R.drawable.react);
                    reactTextView.setText((react_count - 1) + " lượt thích");
                    HomeFragment.likeAdded--;
                } else {
                    isLike[0] = true;
                    dbHelper.addLike(MainActivity.loggedInUser.getUsername(), finalArticleID);
                    reactBtn.setImageResource(R.drawable.reacted);
                    reactTextView.setText((react_count + 1) + " lượt thích");
                    HomeFragment.likeAdded++;
                }

            }
        });

        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getActivity(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return;
                }

                String commenter = MainActivity.loggedInUser.getUsername();
                String content = commentEditText.getText().toString();

                dbHelper.addComment(String.valueOf(finalArticleID), commenter, content);

                commentEditText.setText("");
                commentEditText.clearFocus();

                //Hide device keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                commentListAdapter.addNewComment(new Comment(dbHelper.getLastCommentID(), commenter, content, finalArticleID));
                commentTextView.setText(commentListAdapter.getItemCount() + " bình luận");

                HomeFragment.commentAdded++;
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getActivity(), "Bạn cần đăng nhập trước", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.navigation_login);
                    return;
                }

                final EditText reasonEditText = new EditText(getActivity());
                reasonEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                reasonEditText.setHint("Lí do");

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Báo cáo bài viết")
                        .setMessage("Vấn đề của bài viết này:")
                        .setView(reasonEditText)
                        .setPositiveButton("Báo cáo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reporter = MainActivity.loggedInUser.getUsername(); // replace with actual user who reported the comment
                                String reason = reasonEditText.getText().toString();
                                boolean success = dbHelper.reportArticle(finalArticleID, reporter, reason);

                                if (success) {
                                    Toast.makeText(getActivity(), "Báo cáo của bạn đã được gửi để xem xét", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();

                // Get the positive and negative buttons
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Set the text color of the positive button
                positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));

                // Set the text color of the negative button
                negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.mainTheme));
            }
        });

        if (MainActivity.loggedInUser != null && (MainActivity.loggedInUser.getUsername().equals(author.getUsername()))) {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putInt("articleID", finalArticleID);
                    args.putString("mealChoice", article.getMeal());
                    args.putString("typeChoice", article.getType());
                    args.putString("serveOrderChoice", article.getServeOrderClass());
                    args.putString("dishName", article.getDishName());
                    args.putString("ingredients", article.getIngredients());
                    args.putString("recipe", article.getRecipe());
                    args.putString("timeToMake", article.getTimeToMake());
                    args.putString("imageURL", article.getImgURL());
                    args.putBoolean("editing", true);

                    Navigation.findNavController(view).navigate(R.id.navigation_share, args);
                }
            });
        }

        LinearLayout cmtSection = view.findViewById(R.id.cmt_section);
        NestedScrollView nestedScrollView = view.findViewById(R.id.scrollview);
        TextView cmtTv = view.findViewById(R.id.comment_text);
        AppBarLayout appbar = view.findViewById(R.id.appbar);
        if (args.containsKey("toComment")) {
            if (args.getBoolean("toComment")) {
                appbar.setExpanded(false, true);
                nestedScrollView.smoothScrollTo(0, cmtTv.getTop());
            }
        }

        cmtSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appbar.setExpanded(false, true);
                nestedScrollView.smoothScrollTo(0, cmtTv.getTop());
            }
        });
    }

    private View createCustomActionBar() {
        // Create a RelativeLayout to hold the custom ActionBar
        RelativeLayout customActionBarLayout = new RelativeLayout(getActivity());
        customActionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        customActionBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Create the button and set its attributes
        Button btnHandleReport = new Button(getActivity());
        btnHandleReport.setId(View.generateViewId()); // Generate a unique ID for the button
        btnHandleReport.setText("Handle Report");
        btnHandleReport.setTextColor(Color.WHITE);
        btnHandleReport.setPadding(16, 8, 16, 8);
//        btnHandleReport.setBackground(getResources().getDrawable(R.drawable.custom_button_background));

        // Define the layout parameters for the button
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.setMargins(0, 0, 16, 0); // Set margins (adjust as needed)

        // Add the button to the custom ActionBar layout
        customActionBarLayout.addView(btnHandleReport, layoutParams);

        // Set the click listener for the button
        btnHandleReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the report button click here
                // For example, show a dialog to handle the report
//                handleReport();
            }
        });

        return customActionBarLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String formatIngredients(String input, View view) {

        // Split the input string into an array of individual ingredients
        String[] ingredients = input.split(";\\s*");

        // Use a StringBuilder to build the formatted string
        StringBuilder builder = new StringBuilder();
        for (String ingredient : ingredients) {
            builder.append("- ")
                    .append(ingredient.substring(0, 1).toUpperCase())
                    .append(ingredient.substring(1))
                    .append("\n");
        }
        return builder.toString();
    }

    private String postedTime(String publishedTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            int periodAsSecond = subTime(publishedTime, dateTimeFormatter.format(now));

            int days = periodAsSecond / 3600 / 24;
//            if (days >= 365) {
//                return days / 365 + " năm trước";
//            }
//            if (days >= 30) {
//                return days / 30 + " tháng trước";
//            }

            if (days >= 3) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM");

                Date date = null;
                try {
                    date = inputFormat.parse(publishedTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Format the Date object to the desired output format
                assert date != null;
                return outputFormat.format(date);
            }

            if (days >= 1) {
                return days + " ngày trước";
            }

            if (periodAsSecond >= 3600) {
                return periodAsSecond / 3600 + " giờ trước";
            }
            if (periodAsSecond >= 60) {
                return periodAsSecond / 60 + " phút trước";
            }
            return "Vừa xong";
        }
        return "Đã từ rất lâu";
    }

    private int subTime(String past, String now) {
        //DateTime format yyyy-MM-dd HH:mm:ss

        //Convert DateTime to array
        List<String> pastDate = Arrays.asList(past.split(" ")[0].split("-"));
        List<String> pastTime = Arrays.asList(past.split(" ")[1].split(":"));
        List<String> pastList = new ArrayList<>();
        pastList.addAll(pastDate);
        pastList.addAll(pastTime);

        List<String> nowDate = Arrays.asList(now.split(" ")[0].split("-"));
        List<String> nowTime = Arrays.asList(now.split(" ")[1].split(":"));
        List<String> nowList = new ArrayList<>();
        nowList.addAll(nowDate);
        nowList.addAll(nowTime);

        //Change time from GMT+0 to GMT+7
        //pastList.set(3, String.valueOf(Integer.parseInt(pastList.get(4)) + 7));
        //Sub year for year, month for month,...
        int[] period = new int[6];
        for (int i = 0; i < 6; i++) {
            period[i] = Integer.parseInt(nowList.get(i)) - Integer.parseInt(pastList.get(i));
        }

        return (period[0] * 365 + period[1] * 30 + period[2]) * 24 * 3600
                + (period[3] * 3600 + period[4] * 60 + period[5]);
    }
}
