package com.deltacodex.memoryme;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.deltacodex.memoryme.model.TVShow;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            List<TVShow> allMemos = new ArrayList<>();

            // Fetch TV shows synchronously
            QuerySnapshot tvShowsSnapshot = Tasks.await(FirebaseFirestore.getInstance().collection("TVShow_memo").get());
            for (QueryDocumentSnapshot document : tvShowsSnapshot) {
                allMemos.add(document.toObject(TVShow.class));
            }

            // Fetch movies synchronously
            QuerySnapshot moviesSnapshot = Tasks.await(FirebaseFirestore.getInstance().collection("Movie_memo").get());
            for (QueryDocumentSnapshot document : moviesSnapshot) {
                allMemos.add(document.toObject(TVShow.class));
            }

            // Show notification
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext(), allMemos);
            notificationHelper.checkAndShowNotification();

            Log.d("NotificationWorker", "Notification sent successfully.");
            return Result.success();

        } catch (ExecutionException | InterruptedException e) {
            Log.e("NotificationWorker", "Error fetching data", e);
            return Result.failure();
        }
    }
}


