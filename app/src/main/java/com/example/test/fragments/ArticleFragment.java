package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.test.utils.DatabaseHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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


        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.dish_name_text);
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

        Bundle args = getArguments();
        assert args != null;
        int articleID = 0;
        if (args.containsKey("articleID")) {
             articleID = args.getInt("articleID");
        } else if (args.containsKey("reportID")) {
            articleID = dbHelper.getArticleIDWithReportID(args.getInt("reportID"));
        }
        Article article = dbHelper.getArticleWithId(articleID);

        ImageView dishImg = view.findViewById(R.id.dish_img);

        //test data
        authorAvatar.setImageResource(R.drawable.user_avatar);

        final boolean[] isLike = {false};


        if (MainActivity.loggedInUser != null) {
            userAvatar.setImageResource(R.drawable.user_avatar);
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

        collapsingToolbarLayout.setTitle(article.getDishName());
        recipeContentTextView.setText(Html.fromHtml(article.getRecipe(), Html.FROM_HTML_MODE_COMPACT));
        ingredientsTextView.setText(formatIngredients(article.getIngredients()));
        publishedDateTextView.setText(postedTime(article.getPublishedTime()));
        publisherTextView.setText(article.getPublisher());
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

        int finalArticleID = articleID;
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
                } else {
                    isLike[0] = true;
                    dbHelper.addLike(MainActivity.loggedInUser.getUsername(), finalArticleID);
                    reactBtn.setImageResource(R.drawable.reacted);
                    reactTextView.setText((react_count + 1) + " lượt thích");
                }

            }
        });

        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.loggedInUser == null) {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String formatIngredients(String input) {

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
            if (days >= 365) {
                return days / 365 + " năm trước";
            }
            if (days >= 30) {
                return days / 30 + " tháng trước";
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
