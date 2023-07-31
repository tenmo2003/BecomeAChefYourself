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
import com.example.test.utils.DatabaseHelper;

import java.util.List;

public class NotificationListAdapter extends ArrayAdapter<InAppNotification> {

    List<InAppNotification> notificationList;

    Context context;

    public NotificationListAdapter(@NonNull Context context, List<InAppNotification> notificationList) {
        super(context, R.layout.notification, notificationList);
        this.notificationList = notificationList;

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.notification, null);
        }
        InAppNotification notification = notificationList.get(position);

        TextView contentTv = convertView.findViewById(R.id.content);
        TextView createdTv = convertView.findViewById(R.id.created_time);
        ImageView avatar = convertView.findViewById(R.id.user_avatar);
        LinearLayout notificationContent = convertView.findViewById(R.id.noti);

        DatabaseHelper dbHelper = new DatabaseHelper(context);

//        User actionUser = dbHelper.getUserWithUsername(notification.getActionBy());
        final User[] actionUser = new User[1];
//        View finalConvertView = convertView;
//        MainActivity.runTask(() -> {
//            actionUser[0] = MainActivity.sqlConnection.getUserWithUsername(notification.getActionBy());
//        }, () -> {
//            if (actionUser[0].getAvatarURL().equals("")) {
//                avatar.setImageResource(R.drawable.baseline_person_black_24);
//            } else {
//                ProgressBar progress = finalConvertView.findViewById(R.id.progressbar);
//                progress.setVisibility(View.VISIBLE);
//                Glide.with(context).load(actionUser[0].getAvatarURL()).listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        progress.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        progress.setVisibility(View.GONE);
//                        return false;
//                    }
//                }).into(avatar);
//            }
//        }, MainActivity.progressDialog);




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
            default:
                content = "Notification type: " + type;
                break;
        }

        SpannableString spannableString = new SpannableString(content);
        int startIndex = content.indexOf(notification.getActionBy());
        int endIndex = startIndex + notification.getActionBy().length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        contentTv.setText(spannableString);

        createdTv.setText(notification.getCreatedTime());

        ImageView delete = convertView.findViewById(R.id.delete);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();

                args.putString("username", actionUser[0].getUsername());

                Navigation.findNavController(view).navigate(R.id.navigation_profile, args);
            }
        });

        notificationContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = notification.getType();
                Bundle args = new Bundle();

                if (type.equals("LIKE")) {
                    args.putInt("articleID", notification.getArticleId());
                    Navigation.findNavController(view).navigate(R.id.navigation_article, args);
                } else if (type.equals("COMMENT")) {
                    args.putInt("articleID", notification.getArticleId());
                    args.putBoolean("toComment", true);
                    Navigation.findNavController(view).navigate(R.id.navigation_article, args);
                } else {
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
                        notificationList.remove(position);
                        dbHelper.removeNotification(notification.getId());
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
}
