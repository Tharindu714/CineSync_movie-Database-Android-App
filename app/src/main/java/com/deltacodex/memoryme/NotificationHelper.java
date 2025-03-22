package com.deltacodex.memoryme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.deltacodex.memoryme.model.TVShow;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class NotificationHelper {

    private final Context context;
    private final NotificationManager notificationManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable notificationRunnable;
    private List<TVShow> allMemos;
    private final Random random = new Random();
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel1";

    public NotificationHelper(Context context, List<TVShow> allMemos) {
        this.context = context.getApplicationContext();
        this.allMemos = allMemos != null ? allMemos : new ArrayList<>();
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Watch Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for TV Show reminders");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startNotifications(long intervalMillis) {
        stopNotifications(); // Clear any previous callbacks
        notificationRunnable = () -> {
            Log.d("NotificationHelper", "Running notification check...");
            new Thread(this::checkAndShowNotification).start(); // Run in a background thread
            handler.postDelayed(notificationRunnable, intervalMillis);
        };
        handler.post(notificationRunnable);
    }

    public void stopNotifications() {
        if (notificationRunnable != null) {
            handler.removeCallbacks(notificationRunnable);
        }
    }

    void checkAndShowNotification() {
        List<TVShow> yetToWatch = new ArrayList<>();

        if (allMemos != null && !allMemos.isEmpty()) {
            for (TVShow memo : allMemos) {
                if (memo.getWatchedDate() != null) {
                    String lower = memo.getWatchedDate().toLowerCase(Locale.ROOT);
                    if (lower.equals("yet to watch") || lower.contains("someday")) {
                        yetToWatch.add(memo);
                    }
                }
            }
        }

        if (!yetToWatch.isEmpty()) {
            int index = random.nextInt(yetToWatch.size());  // Using the cached random instance
            TVShow selected = yetToWatch.get(index);
            showNotification(selected);
        }
    }

    private void showNotification(TVShow tvShow) {
        // Load both portrait (large icon) and landscape (big picture) images
        Glide.with(context)
                .asBitmap()
                .load(tvShow.getPortraitUrl()) // Load portrait image for large icon
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap portraitIcon, Transition<? super Bitmap> transition) {
                        loadLandscapeImage(tvShow, portraitIcon); // Once portrait is ready, load landscape
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e("NotificationHelper", "Portrait image loading failed.");
                        loadLandscapeImage(tvShow, null); // Still load landscape even if portrait fails
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Clean up if needed
                    }
                });
    }

    private void loadLandscapeImage(TVShow tvShow, Bitmap portraitIcon) {
        Glide.with(context)
                .asBitmap()
                .load(tvShow.getLandscapeUrl()) // Load landscape image for big picture
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap landscapeImage, Transition<? super Bitmap> transition) {
                        showNotificationWithImages(tvShow, portraitIcon, landscapeImage);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e("NotificationHelper", "Landscape image loading failed.");
                        showNotificationWithImages(tvShow, portraitIcon, null); // Show portrait only if landscape fails
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Clean up if needed
                    }
                });
    }

    private void showNotificationWithImages(TVShow tvShow, Bitmap portraitIcon, Bitmap landscapeImage) {
        Intent intent = new Intent(context, Single_TvshowActivity.class);
        intent.putExtra("showName", tvShow.getShowName());
        intent.putExtra("description", tvShow.getDescription());
        intent.putExtra("landscapeUrl", tvShow.getLandscapeUrl());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Watch This Today!")
                .setContentText("Don't forget to watch: " + tvShow.getShowName())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (portraitIcon != null) {
            builder.setLargeIcon(portraitIcon); // Portrait as large icon
        }

        if (landscapeImage != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(landscapeImage) // Landscape as big picture
                    .setSummaryText(tvShow.getDescription()));
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void updateMemos(List<TVShow> newMemos) {
        this.allMemos = newMemos != null ? newMemos : new ArrayList<>();
    }
}

