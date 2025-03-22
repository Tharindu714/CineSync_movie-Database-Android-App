package com.deltacodex.memoryme.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.model.TVShow;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<TVShow> tvShowList;
    private final Context context;
    private final AlphabetCategoryAdapter.OnTVShowClickListener listener;

    public MovieAdapter(Context context, List<TVShow> tvShowList, AlphabetCategoryAdapter.OnTVShowClickListener listener) {
        this.context = context;
        this.tvShowList = tvShowList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        TVShow show = tvShowList.get(position);
        holder.tvShowName.setText(show.getShowName());

        // Load thumbnail using Glide (or Picasso)
        Glide.with(context)
                .load(show.getPortraitUrl())
                .into(holder.ivThumbnail);

        // Handle normal click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTVShowClick(show);
            }
        });

        // Handle long click for updating
        holder.itemView.setOnLongClickListener(v -> {
            showUpdateDialog(show);
            return true; // consume the long-click event
        });
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvShowName;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvShowName = itemView.findViewById(R.id.tvShowName);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showUpdateDialog(TVShow tvShow) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_tvshow, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Get references to the dialog views
        ImageView ivLargeImage = dialogView.findViewById(R.id.ivLargeImage);
        EditText etThumbUrl = dialogView.findViewById(R.id.etThumbUrl);
        EditText etLargeUrl = dialogView.findViewById(R.id.etLargeUrl);
        EditText etWatchedDate = dialogView.findViewById(R.id.etWatchedDate);
        EditText etYoutubeLink = dialogView.findViewById(R.id.etYoutubeLink);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        btnUpdate.setText("Update : "+tvShow.getShowName());
        // Pre-fill the fields with existing data
        Glide.with(context).load(tvShow.getLandscapeUrl()).into(ivLargeImage);
        etThumbUrl.setText(tvShow.getPortraitUrl());
        etLargeUrl.setText(tvShow.getLandscapeUrl());
        etWatchedDate.setText(tvShow.getWatchedDate());
        etYoutubeLink.setText(tvShow.getTrailerUrl());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle update button click
        btnUpdate.setOnClickListener(v -> {
            // Get updated values
            String newThumbUrl = etThumbUrl.getText().toString().trim();
            String newLargeUrl = etLargeUrl.getText().toString().trim();
            String newWatchedDate = etWatchedDate.getText().toString().trim();
            String newYoutubeLink = etYoutubeLink.getText().toString().trim();

            // Update Firestore
            updateMovie(tvShow, newThumbUrl, newLargeUrl, newWatchedDate, newYoutubeLink);
            dialog.dismiss();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateMovie(TVShow tvShow, String thumbUrl, String largeUrl,
                             String watchedDate, String youtubeLink) {

        // 1. Ensure we have a valid document ID in the TVShow
        if (tvShow.getDocumentId() == null || tvShow.getDocumentId().isEmpty()) {
            Log.e("MovieUpdateError", "Cannot update movie: Document ID is null/empty for " + tvShow.getShowName());
            Toast.makeText(context, "Error: Invalid Document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference movieRef = db.collection("Movie_memo")
                .document(tvShow.getDocumentId()); // Use doc ID!

        // Only update the fields that changed
        Map<String, Object> updates = new HashMap<>();
        updates.put("portraitUrl", thumbUrl);
        updates.put("landscapeUrl", largeUrl);
        updates.put("watchedDate", watchedDate);
        updates.put("trailerUrl", youtubeLink);

        // Perform the update in Firestore
        movieRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, tvShow.getShowName()+" updated!", Toast.LENGTH_SHORT).show();

                    // Update local object to reflect changes
                    tvShow.setPortraitUrl(thumbUrl);
                    tvShow.setLandscapeUrl(largeUrl);
                    tvShow.setWatchedDate(watchedDate);
                    tvShow.setTrailerUrl(youtubeLink);

                    // Refresh RecyclerView
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("MovieUpdateError", "Failed to update movie", e);
                    Toast.makeText(context, "Failed to update movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
