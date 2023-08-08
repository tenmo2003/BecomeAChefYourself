package com.example.test.fragments;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

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
import com.example.test.components.InAppNotification;
import com.example.test.components.User;
import com.example.test.utils.NotificationUtils;
import com.google.android.material.appbar.AppBarLayout;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ArticleFragment extends Fragment {
    List<Comment> commentList;
    CommentListAdapter commentListAdapter;
    RecyclerView commentListView;
    Article article;
    public static TextView commentTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CoordinatorLayout parent = view.findViewById(R.id.parent);
        parent.setVisibility(View.INVISIBLE);

        TextView title = view.findViewById(R.id.title_in_article);
        TextView recipeContentTextView = view.findViewById(R.id.recipe_text);
        TextView ingredientsTextView = view.findViewById(R.id.ingredients_text);
        TextView publisherTextView = view.findViewById(R.id.publisher_text);
        TextView publishedDateTextView = view.findViewById(R.id.published_date_text);
        TextView timeToMakeTextView = view.findViewById(R.id.time_to_make_text);
        TextView reactTextView = view.findViewById(R.id.react_count);
        commentTextView = view.findViewById(R.id.cmt_count);
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
        AtomicInteger articleID = new AtomicInteger();
        AtomicBoolean checkBookmarked = new AtomicBoolean(false);
        MainActivity.runTask(() -> {
            if (args.containsKey("articleID")) {
                articleID.set(args.getInt("articleID"));
            } else if (args.containsKey("reportID")) {
                articleID.set(MainActivity.sqlConnection.getArticleIDWithReportID(args.getInt("reportID")));
            }
            checkBookmarked.set(MainActivity.loggedInUser != null && MainActivity.sqlConnection.checkBookmarked(MainActivity.loggedInUser.getUsername(), articleID.get()));

            article = MainActivity.sqlConnection.getArticleWithId(articleID.get());
        }, () -> {

            final boolean[] isBookmark = {checkBookmarked.get()};
            if (isBookmark[0]) {
                bookmark.setImageResource(R.drawable.bookmarked);
            } else {
                bookmark.setImageResource(R.drawable.bookmark_in_article);
            }

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.loggedInUser == null) {
                        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isBookmark[0]) {
                        isBookmark[0] = true;
                        MainActivity.runTask(() -> {
                            MainActivity.sqlConnection.addBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                        }, () -> {
                            bookmark.setImageResource(R.drawable.bookmarked);
                            HomeFragment.modified = true;
                        }, null);
                    } else {
                        isBookmark[0] = false;
                        MainActivity.runTask(() -> {
                            MainActivity.sqlConnection.removeBookmark(MainActivity.loggedInUser.getUsername(), article.getId());
                        }, () -> {
                            bookmark.setImageResource(R.drawable.bookmark_in_article);
                            HomeFragment.modified = true;
                        }, null);
                    }
                }
            });

            ImageView dishImg = view.findViewById(R.id.dish_img);

            //test data
            AtomicReference<User> author = new AtomicReference<>();
            MainActivity.runTask(() -> {
                author.set(MainActivity.sqlConnection.getUserWithUsername(article.getPublisher()));
            }, () -> {
                if (author.get().getAvatarURL().equals("")) {
                    authorAvatar.setImageResource(R.drawable.baseline_person_24);
                } else {
                    if (getActivity() != null) {
                        ProgressBar progressBar = view.findViewById(R.id.avatar_progressbar);
                        Glide.with(getActivity()).load(author.get().getAvatarURL()).listener(new RequestListener<Drawable>() {
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
                        }).error(R.drawable.image_load_failed).into(authorAvatar);
                    }
                }

                if (MainActivity.loggedInUser != null && (MainActivity.loggedInUser.getUsername().equals("admin") || MainActivity.loggedInUser.getUsername().equals(author.get().getUsername()))) {
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
                                    MainActivity.runTask(() -> {
                                        MainActivity.sqlConnection.removeArticle(articleID.get());
                                        if (!MainActivity.loggedInUser.getUsername().equals(author.get().getUsername())) {
                                            MainActivity.sqlConnection.increaseReportLevelForUser(article.getPublisher());
                                        }
                                    }, () -> {
                                        MainActivity.toast.setText("Bài viết đã bị xoá");
                                        MainActivity.toast.show();
                                        Navigation.findNavController(view).navigate(R.id.navigation_home);
                                    }, null);

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

                final boolean[] isLike = {false};


                if (MainActivity.loggedInUser != null) {
                    if (!MainActivity.loggedInUser.getAvatarURL().equals("")) {
                        Glide.with(getActivity()).load(MainActivity.loggedInUser.getAvatarURL()).into(userAvatar);
                    } else {
                        userAvatar.setImageResource(R.drawable.baseline_person_24);
                    }

                    MainActivity.runTask(() -> {
                        isLike[0] = MainActivity.sqlConnection.checkLiked(MainActivity.loggedInUser.getUsername(), articleID.get());
                    }, () -> {
                        if (isLike[0]) {
                            reactBtn.setImageResource(R.drawable.reacted);
                        } else {
                            reactBtn.setImageResource(R.drawable.react);
                        }
                    }, null);
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
                    }).error(R.drawable.image_load_failed).into(dishImg);
                }

                title.setText(article.getDishName());
                recipeContentTextView.setText(article.getRecipe());
                ingredientsTextView.setText(formatIngredients(article.getIngredients(), view));
                ingredientsCount.setText(article.getIngredients().split(";\\s*").length + " thành phần");
                publishedDateTextView.setText(article.getPublishedTime());
                publisherTextView.setText(article.getPublisher());
                AtomicReference<String> followCount = new AtomicReference<>();
                AtomicReference<String> articleCount = new AtomicReference<>();
                MainActivity.runTask(() -> {
                    followCount.set(MainActivity.sqlConnection.getTotalFollowCount(author.get().getUsername()) + " followers");
                    articleCount.set(MainActivity.sqlConnection.getTotalArticleCount(author.get().getUsername()) + " recipes");
                    commentList = MainActivity.sqlConnection.getCommentWithArticleID(articleID.get());
                }, () -> {
                    authorFollowers.setText(followCount.get());
                    authorRecipes.setText(articleCount.get());

                    commentListAdapter = new CommentListAdapter();
                    commentListAdapter.setComments(commentList);
                    commentListAdapter.setContext(getActivity());

                    commentListView = view.findViewById(R.id.comment_list);
                    commentListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    commentListView.setHasFixedSize(true);
                    commentListView.setAdapter(commentListAdapter);
                }, null);

                timeToMakeTextView.setText(article.getTimeToMake());
                reactTextView.setText(article.getLikes() + " lượt thích");
                commentTextView.setText(article.getComments() + " bình luận");


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
                            MainActivity.toast.setText("Vui lòng đăng nhập trước");
                            MainActivity.toast.show();
                            return;
                        }

                        //get react count number
                        int react_count = Integer.parseInt(reactTextView.getText().toString().split(" ")[0]);

                        if (isLike[0]) {
                            isLike[0] = false;
                            MainActivity.runTask(() -> {
                                MainActivity.sqlConnection.removeLike(MainActivity.loggedInUser.getUsername(), articleID.get());
                            }, () -> {
                                reactBtn.setImageResource(R.drawable.react);
                                reactTextView.setText((react_count - 1) + " lượt thích");
                                HomeFragment.likeAdded--;
                            }, MainActivity.progressDialog);
                        } else {
                            isLike[0] = true;
                            MainActivity.runTask(() -> {
                                MainActivity.sqlConnection.addLike(MainActivity.loggedInUser.getUsername(), articleID.get());
                            }, () -> {
                                reactBtn.setImageResource(R.drawable.reacted);
                                reactTextView.setText((react_count + 1) + " lượt thích");
                                HomeFragment.likeAdded++;
                            }, MainActivity.progressDialog);

                        }

                    }
                });

                sendCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MainActivity.loggedInUser == null) {
                            MainActivity.toast.setText("Vui lòng đăng nhập trước");
                            MainActivity.toast.show();
                            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            return;
                        }

                        String commenter = MainActivity.loggedInUser.getUsername();
                        String content = commentEditText.getText().toString();

                        if (content.equals("")) {
                            MainActivity.toast.setText("Xin điền nội dung bình luận");
                            MainActivity.toast.show();
                            return;
                        }

                        AtomicReference<Comment> cmt = new AtomicReference<>();

                        MainActivity.runTask(() -> {
                            MainActivity.sqlConnection.addComment(String.valueOf(articleID.get()), commenter, content);
                            cmt.set(new Comment(MainActivity.sqlConnection.getLastCommentID(), commenter, content, articleID.get()));
                        }, () -> {
                            commentListAdapter.addNewComment(cmt.get());
                            commentTextView.setText(commentListAdapter.getItemCount() + " bình luận");
                            HomeFragment.commentAdded++;

                            commentEditText.setText("");
                            commentEditText.clearFocus();

                            //Hide device keyboard
                            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        }, MainActivity.progressDialog);

                    }
                });

                reportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MainActivity.loggedInUser == null) {
                            MainActivity.toast.setText("Bạn cần đăng nhập trước");
                            MainActivity.toast.show();
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
                                        AtomicBoolean success = new AtomicBoolean(false);

                                        MainActivity.runTask(() -> {
                                            success.set(MainActivity.sqlConnection.reportArticle(articleID.get(), reporter, reason));
                                        }, () -> {
                                            if (success.get()) {
                                                MainActivity.toast.setText("Báo cáo của bạn đã được gửi để xem xét");
                                                MainActivity.toast.show();
                                            } else {
                                                MainActivity.toast.setText("Gửi báo cáo thất bại");
                                                MainActivity.toast.show();
                                            }
                                        }, MainActivity.progressDialog);
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

                if (MainActivity.loggedInUser != null && (MainActivity.loggedInUser.getUsername().equals(author.get().getUsername()))) {
                    editBtn.setVisibility(View.VISIBLE);
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle args = new Bundle();
                            args.putInt("articleID", articleID.get());
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


            }, null);
            parent.setVisibility(View.VISIBLE);
            }, MainActivity.progressDialog);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Handler handler = new Handler();

        service.scheduleAtFixedRate(() -> {
            System.out.println("Comment Updated");
            // Fetch notifications in the background
            if (articleID.get() != 0 && commentList != null) {
                List<Comment> comments = MainActivity.sqlConnection.getCommentWithArticleID(articleID.get());

                System.out.println(comments.size());
                System.out.println(commentList.size());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Do your stuff here, It gets loop every 15 Minutes
                        if (commentList.size() < comments.size()) {
                            int difference = comments.size() - commentList.size();
                            for (int i = 0; i < difference; i++) {
                                // Get the first n new notifications and show push notifications
                                Comment comment = comments.get(i);

                                commentList.add(0, comment);
                            }
                            commentListAdapter.setComments(commentList);
                        } else if (commentList.size() > comments.size()) {
                            commentList.clear();
                            commentList.addAll(comments);
                            commentListAdapter.setComments(commentList);
                        }
                    }

                });
            }
        }, 0, 1, TimeUnit.SECONDS);

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
