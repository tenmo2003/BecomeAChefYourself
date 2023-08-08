package com.example.test.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.InAppNotification;

import java.util.Date;

public class NotificationUtils {
    private static final String CHANNEL_ID = "channel_id";

    public static void showNotification(Context context, InAppNotification notification) {
        createNotificationChannel(context);

        String title = "BecomeChef";
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
                content = "Bài đăng '" + notification.getArticleName() + "' của bạn đã vi phạm quy chuẩn cộng đồng và đã bị xoá! Việc này có thể khiển tài khoản của bạn bị khoá. Mong bạn thông cảm vì một cộng đồng sạch";
                break;
            case "REPORT_COMMENT":
                content = "Bình luận '" + notification.getCommentContent() + "' của bạn đã vi phạm quy chuẩn cộng đồng và đã bị xoá! Việc này có thể khiển tài khoản của bạn bị khoá. Mong bạn thông cảm vì một cộng đồng sạch";
                break;
            default:
                content = "Notification type: " + type;
                break;
        }

        PendingIntent pendingIntent = createPendingIntent(context, notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.beefsteak)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        notificationManager.notify(getNotificationId(), builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static PendingIntent createPendingIntent(Context context, InAppNotification notification) {
        Intent intent = new Intent(context, MainActivity.class);

        String type = notification.getType();
        System.out.println(type);

        Bundle extrasBundle = new Bundle(); // Create a new Bundle

        extrasBundle.putString("type", type); // Add the "type" extra to the Bundle

        if (type.equals("LIKE") || type.equals("FOLLOWING_POST")) {
            extrasBundle.putInt("articleID", notification.getArticleId());
        } else if (type.equals("COMMENT")) {
            extrasBundle.putInt("articleID", notification.getArticleId());
            extrasBundle.putBoolean("toComment", true);
        } else if (type.equals("FOLLOW")) {
            extrasBundle.putString("username", notification.getActionBy());
        }

        intent.putExtras(extrasBundle); // Set the Bundle as extras in the Intent


        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }

    private static int getNotificationId() {
        return (int) new Date().getTime();
    }
}
