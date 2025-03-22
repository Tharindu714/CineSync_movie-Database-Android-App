package com.deltacodex.memoryme;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;

public class StatusBarUtils {

    // Method to apply gradient status bar
    public static void applyGradientStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            // Use ContextCompat.getDrawable to safely get the drawable
            Drawable background = ContextCompat.getDrawable(activity, R.drawable.side_nav_bar);
            if (background != null) {
                window.setStatusBarColor(Color.TRANSPARENT);  // Make status bar color transparent

                // Add a custom View to the top to act as the status bar
                View statusBarView = new View(activity);
                statusBarView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));
                statusBarView.setBackground(background);
                ViewGroup decorView = (ViewGroup) window.getDecorView();
                decorView.addView(statusBarView);
            }
        }
    }

    // Method to get status bar height
    private static int getStatusBarHeight(Activity activity) {
        int result = 0;
        // Get the status bar height from the system resources
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Method to hide the status bar
    public static void hideStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // For older versions, you can try using a different method to hide it
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    // Method to show the status bar (if hidden)
    public static void showStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // For older versions
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = 0;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}

