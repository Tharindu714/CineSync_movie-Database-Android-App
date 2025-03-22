package com.deltacodex.memoryme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class Single_AnthologyActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_anthology);

        // Initialize Views
        ImageView largeImage = findViewById(R.id.largeImage);
        TextView tvShowName = findViewById(R.id.tvshow_name);
        TextView date_watched = findViewById(R.id.dateWatched);


        // Get Data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("showName");
            String imageUrl = intent.getStringExtra("landscapeUrl");
            String watched_date = getIntent().getStringExtra("watchedDate");

            // Set Data to Views
            tvShowName.setText(name);
            date_watched.setText(watched_date);


            // Load Image using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.large)
                    .into(largeImage);
        }

        // Apply Full-Screen Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}