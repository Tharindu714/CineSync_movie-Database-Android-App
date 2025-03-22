package com.deltacodex.memoryme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.deltacodex.memoryme.model.TVShow;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {

    private NotificationHelper notificationHelper;
    private Handler handler;
    private Runnable notificationRunnable;
    private Runnable fetchMoviesRunnable;

    private static final long NOTIFICATION_INTERVAL = 900000; // 15 minutes in milliseconds
    private static final long INITIAL_FETCH_DELAY = 60000; // 1 minute in milliseconds
    private static final long NOTIFICATION_DURATION = 3600000; // 1 hour in milliseconds

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this, new ArrayList<>());
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        // Start the first cycle: wait 1 minute then fetch movies before starting notifications.
        scheduleFetchMovies();
        return START_STICKY;
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "service_channel",
                    "Background Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "service_channel")
                .setContentTitle("MemoryME Running")
                .setContentText("Checking for upcoming shows...")
                .setSmallIcon(R.drawable.logo)
                .build();
    }

    /**
     * Schedules the movie fetch with a 1 minute delay.
     * Once the movies are fetched, start the notifications cycle.
     */
    private void scheduleFetchMovies() {
        fetchMoviesRunnable = () -> {
            fetchMovies();
            // After fetching movies, start the notification cycle.
            startNotificationCycle();
        };
        handler.postDelayed(fetchMoviesRunnable, NOTIFICATION_INTERVAL);
    }

    /**
     * Starts a cycle that shows notifications every 15 minutes for 1 hour.
     * Once the 1 hour is up, it restarts the process by scheduling a fetch.
     */
    private void startNotificationCycle() {
        final long cycleStartTime = System.currentTimeMillis();
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - cycleStartTime;
                if (elapsedTime < NOTIFICATION_DURATION) {
                    // Show notification every 15 minutes.
                    notificationHelper.checkAndShowNotification();

                    // Make sure that the next notification comes 15 minutes later.
                    handler.postDelayed(this, NOTIFICATION_INTERVAL);
                } else {
                    // One hour is over; restart by scheduling fetchMovies after 1 minute.
                    scheduleFetchMovies();
                }
            }
        };
        // Start the notification cycle immediately after movies are fetched.
        handler.post(notificationRunnable);
    }


    private void fetchMovies() {
        FirebaseFirestore.getInstance().collection("Movie_memo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TVShow> tvShowList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TVShow show = document.toObject(TVShow.class);
                        tvShowList.add(show);
                    }
                    notificationHelper.updateMemos(tvShowList);
                })
                .addOnFailureListener(e -> Log.e("NotificationService", "Failed to fetch Movies", e));
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(fetchMoviesRunnable);
        handler.removeCallbacks(notificationRunnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not bound to anything
    }
}
