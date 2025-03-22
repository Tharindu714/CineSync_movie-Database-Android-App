package com.deltacodex.memoryme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.deltacodex.memoryme.databinding.ActivityHomeBinding;

import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.deltacodex.memoryme.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tvshow, R.id.nav_movie, R.id.nav_anthology,
                R.id.nav_antho_memo, R.id.nav_tv_memo, R.id.nav_movie_memo )
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tvshow) {
                navController.navigate(R.id.nav_tvshow);
            } else if (id == R.id.nav_anthology) {
                navController.navigate(R.id.nav_anthology);
            } else if (id == R.id.nav_movie) {
                navController.navigate(R.id.nav_movie);
            } else if (id == R.id.nav_tv_memo) {
                navController.navigate(R.id.nav_tv_memo);
            } else if (id == R.id.nav_antho_memo) {
                navController.navigate(R.id.nav_antho_memo);
            } else if (id == R.id.nav_movie_memo) {
                navController.navigate(R.id.nav_movie_memo);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // ✅ Open drawer only if savedInstanceState is null (prevents multiple openings)
        if (savedInstanceState == null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> binding.drawerLayout.openDrawer(GravityCompat.START), 500);
        }

        StatusBarUtils.applyGradientStatusBar(this);

        // 🔹 Ensure notification worker is started correctly
        startNotificationService();
        scheduleNotificationWorker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // 🔹 Ensures notification service is started properly
    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("HomeActivity", "Starting foreground notification service...");
            startForegroundService(serviceIntent);
        } else {
            Log.d("HomeActivity", "Starting normal notification service...");
            startService(serviceIntent);
        }
    }

    private void scheduleNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(this);

        // 🔹 Check if a worker is already running to prevent cancellation issues
        workManager.getWorkInfosForUniqueWorkLiveData("NotificationWork").observe(this, workInfos -> {
            boolean isRunning = false;
            for (WorkInfo workInfo : workInfos) {
                if (workInfo.getState() == WorkInfo.State.ENQUEUED || workInfo.getState() == WorkInfo.State.RUNNING) {
                    isRunning = true;
                    break;
                }
            }

            if (!isRunning) {
                Log.d("HomeActivity", "Scheduling new notification worker...");
                PeriodicWorkRequest notificationWorkRequest =
                        new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                                .setInitialDelay(15, TimeUnit.MINUTES)
                                .build();

                workManager.enqueueUniquePeriodicWork(
                        "NotificationWork",
                        ExistingPeriodicWorkPolicy.REPLACE, // ✅ Ensures worker is refreshed if needed
                        notificationWorkRequest
                );
            } else {
                Log.d("HomeActivity", "Notification worker already running.");
            }
        });
    }
}
