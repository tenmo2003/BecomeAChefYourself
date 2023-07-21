package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapters.CommentListAdapter;
import com.example.test.components.Comment;
import com.example.test.utils.DatabaseHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.dish_name_text);
        TextView recipeContentTextView = view.findViewById(R.id.recipe_text);
        TextView ingredientsTextView = view.findViewById(R.id.ingredients_text);
        TextView publisherTextView = view.findViewById(R.id.publisher_text);
        TextView publishedDateTextView = view.findViewById(R.id.published_date_text);
        TextView timeToMakeTextView = view.findViewById(R.id.time_to_make_text);
        TextView reactTextView = view.findViewById(R.id.react_count);
        TextView commentTextView = view.findViewById(R.id.cmt_count);
        ImageView userAvatar = view.findViewById(R.id.user_avatar_in_article);
        ImageView viewAuthorProfile = view.findViewById(R.id.view_author_profile);

        Bundle args = getArguments();
        if (args != null) {
            String articleID = args.getString("articleID");
            String dishName = args.getString("dish_name");
            String recipeContent = args.getString("recipe_content");
            String ingredients = args.getString("ingredients");
            String publisher = args.getString("publisher");
            String publishedDate = args.getString("publishedDate");
            String timeToMake = args.getString("time_to_make");
            String reacts = args.getString("reacts");
            String comments = args.getString("comments");
            String imageURL = args.getString("imageURL");
            ImageView dishImg = view.findViewById(R.id.dish_img);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    URL newurl = null;
                    try {
                        newurl = new URL(imageURL);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    Bitmap mIcon_val;
                    try {
                        mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dishImg.setImageBitmap(mIcon_val);
                        }
                    });
                }
            });

            collapsingToolbarLayout.setTitle(dishName);
            recipeContentTextView.setText(Html.fromHtml(recipeContent, Html.FROM_HTML_MODE_COMPACT));
            ingredientsTextView.setText(formatIngredients(ingredients));
            publishedDateTextView.setText(postedTime(publishedDate));
            publisherTextView.setText(publisher);
            timeToMakeTextView.setText(timeToMake);
            reactTextView.setText(reacts + " lượt thích");
            commentTextView.setText(comments + " bình luận");

            userAvatar.setImageResource(R.drawable.user_avatar);

            commentListAdapter = new CommentListAdapter();
            dbHelper = new DatabaseHelper(getActivity());
            commentList = dbHelper.getCommentWithArticleID(articleID);
            commentListAdapter.setComments(commentList);

            commentListView = view.findViewById(R.id.comment_list);
            commentListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            commentListView.setHasFixedSize(true);
            commentListView.setAdapter(commentListAdapter);
        }

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
