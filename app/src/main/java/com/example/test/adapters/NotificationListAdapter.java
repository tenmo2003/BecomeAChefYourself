package com.example.test.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.InAppNotification;
import com.example.test.components.User;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationListAdapter extends ArrayAdapter<InAppNotification> {

//    List<InAppNotification> notificationList;

    Context context;

    public NotificationListAdapter(@NonNull Context context) {
        super(context, R.layout.notification, MainActivity.notificationList);
//        this.notificationList = notificationList;

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.notification, null);
        }
        InAppNotification notification = MainActivity.notificationList.get(position);

        TextView contentTv = convertView.findViewById(R.id.content);
        TextView createdTv = convertView.findViewById(R.id.created_time);
        ImageView avatar = convertView.findViewById(R.id.user_avatar);
        LinearLayout notificationContent = convertView.findViewById(R.id.noti);


        AtomicReference<User> actionUser = new AtomicReference<>();
        View finalConvertView = convertView;
        MainActivity.runTask(() -> {
            actionUser.set(MainActivity.sqlConnection.getUserWithUsername(notification.getActionBy()));
        }, () -> {
            if (actionUser.get().getAvatarURL().equals("")) {
                avatar.setImageResource(R.drawable.baseline_person_black_24);
            } else {
                ProgressBar progress = finalConvertView.findViewById(R.id.progressbar);
                progress.setVisibility(View.VISIBLE);
                Glide.with(context).load(actionUser.get().getAvatarURL()).listener(new RequestListener<Drawable>() {
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
                }).into(avatar);
            }

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();

                    args.putString("username", actionUser.get().getUsername());

                    Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
                }
            });
        }, null);


        String type = notification.getType();
        String content = "";
        switch (type) {
            case "LIKE":
                content = notification.getActionBy() + " đã thích bài viết \"" + notification.getArticleName() + "\" của bạn";
                break;
            case "COMMENT":
                content = notification.getActionBy() + " đã bình luận về bài viết của bạn: \"" + notification.getCommentContent() + "\"";
                break;
            case "FOLLOW":
                content = notification.getActionBy() + " đã theo dõi bạn";
                break;
            // Add more cases for other types if needed
            case "FOLLOWING_POST":
                content = notification.getActionBy() + " đã đăng bài viết mới: '" + notification.getArticleName() + "'";
                break;
            case "REPORT_ARTICLE":

                content = "Bài đăng '" + notification.getArticleName() + "' của bạn đã vi phạm quy chuẩn cộng đồng và đã bị xoá! Việc này có thể khiến tài khoản của bạn bị khoá. Mong bạn thông cảm vì một cộng đồng sạch";
                break;
            case "REPORT_COMMENT":
                content = "Bình luận '" + notification.getCommentContent() + "' của bạn đã vi phạm quy chuẩn cộng đồng và đã bị xoá! Việc này có thể khiến tài khoản của bạn bị khoá. Mong bạn thông cảm vì một cộng đồng sạch";
                break;
            default:
                content = "Notification type: " + type;
                break;
        }

        SpannableString spannableString = new SpannableString(content);
        int startIndex = content.indexOf(notification.getActionBy());
        int endIndex = startIndex + notification.getActionBy().length();
        if (startIndex != -1 && endIndex != -1) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentTv.setText(spannableString);
        } else {
            contentTv.setText(content);
        }


        createdTv.setText(notification.getCreatedTime());

        ImageView delete = convertView.findViewById(R.id.delete);


        notificationContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = notification.getType();
                Bundle args = new Bundle();

                if (type.equals("LIKE") || type.equals("FOLLOWING_POST")) {
                    args.putInt("articleID", notification.getArticleId());
                    Navigation.findNavController(view).navigate(R.id.navigation_article, args);
                } else if (type.equals("COMMENT")) {
                    args.putInt("articleID", notification.getArticleId());
                    args.putBoolean("toComment", true);
                    Navigation.findNavController(view).navigate(R.id.navigation_article, args);
                } else if (type.equals("FOLLOW")){
                    args.putString("username", notification.getActionBy());
                    Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xoá thông báo");
                builder.setMessage("Bạn chắc chắn muốn xoá thông báo chứ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform logout action here
                        MainActivity.notificationList.remove(position);
                        MainActivity.runTask(() -> {
                            MainActivity.sqlConnection.removeNotification(notification.getId());
                        }, null, null);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Huỷ", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                // Get the positive and negative buttons
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Set the text color of the positive button
                positiveButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));

                // Set the text color of the negative button
                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.mainTheme));
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return MainActivity.notificationList.size();
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(getCount(), 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
