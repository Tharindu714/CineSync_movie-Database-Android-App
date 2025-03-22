package com.deltacodex.memoryme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Single_TvshowActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_tvshow);

        // Initialize Views
        ImageView largeImage = findViewById(R.id.largeImage);
        TextView tvShowName = findViewById(R.id.tvshow_name);
        TextView tvDescription = findViewById(R.id.tv_description);
        TextView episodeList = findViewById(R.id.episodes);
        TextView date_watched = findViewById(R.id.dateWatched);
        WebView webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Get Data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("showName");
            String description = intent.getStringExtra("description");
            String imageUrl = intent.getStringExtra("landscapeUrl");
            String episodelist = getIntent().getStringExtra("episodes");
            String watched_date = getIntent().getStringExtra("watchedDate");
            // Placeholder for trailer
            String trailerUrl = intent.getStringExtra("trailerUrl");

            // Set Data to Views
            tvShowName.setText(name);
            tvDescription.setText(description);
            if (episodelist != null) {
                String formattedEpisodes = episodelist.replace(",", "\n");

                // Set formatted text
                episodeList.setText(formattedEpisodes);
            }
            date_watched.setText(watched_date);


            // Load Image using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.large)
                    .into(largeImage);

            // Load Trailer into WebView
            if (trailerUrl != null && !trailerUrl.isEmpty()) {
                // Convert YouTube URL to an embeddable format
                String videoId = extractYouTubeVideoId(trailerUrl);
                if (videoId != null) {
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    String html = "<html><body style='margin:0;padding:0;'><iframe width='100%' height='100%' " +
                            "src='" + embedUrl + "' frameborder='0' allowfullscreen></iframe></body></html>";
                    webView.loadData(html, "text/html", "utf-8");
                }
            }
        }

        // Apply Full-Screen Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Open Trailer in External Browser (Optional)
    private String extractYouTubeVideoId(String url) {
        String pattern = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
