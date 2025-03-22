package com.deltacodex.memoryme.ui.Tvshow_Memo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.StatusBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TMemoFragment extends Fragment {

    private EditText imageUrlInput, landscapeimageUrlInput, tvShowName, dateWatched, tvShowDescription, tvShowEpisodes, myThoughts, trailerLink;
    private ImageView tvShowPortrait, tvShowLandscape;
    private WebView videoPreview;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_memo, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Find views by id
        imageUrlInput = view.findViewById(R.id.imageUrlInput);
        landscapeimageUrlInput = view.findViewById(R.id.landscapeimageUrlInput);
        tvShowName = view.findViewById(R.id.tvShowName);
        dateWatched = view.findViewById(R.id.dateWatched);
        tvShowDescription = view.findViewById(R.id.tvShowDescription);
        tvShowEpisodes = view.findViewById(R.id.tvShowEpisodes);
        myThoughts = view.findViewById(R.id.myThoughts);
        trailerLink = view.findViewById(R.id.trailerLink);
        tvShowPortrait = view.findViewById(R.id.tvShowPortrait);
        tvShowLandscape = view.findViewById(R.id.tvShowLandscape);
        videoPreview = view.findViewById(R.id.videoPreview);
        Button uploadTvshowBtn = view.findViewById(R.id.uploadTvshowBtn);

        // Load portrait image when URL is entered
        imageUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Load the portrait image using Glide
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.tumb) // add a placeholder image in your resources
                            .into(tvShowPortrait);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed.
            }
        });

        // Load landscape image when URL is entered
        landscapeimageUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.large)
                            .into(tvShowLandscape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed.
            }
        });

        // When trailer link is entered, load its thumbnail in the WebView or you could load in an ImageView if you prefer.
        trailerLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadYouTube_Preview(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set onClick listener to upload the memo data to Firestore
        uploadTvshowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gather all data from the inputs
                String portraitUrl = imageUrlInput.getText().toString().trim();
                String landscapeUrl = landscapeimageUrlInput.getText().toString().trim();
                String showName = tvShowName.getText().toString().trim();
                String watchedDate = dateWatched.getText().toString().trim();
                String description = tvShowDescription.getText().toString().trim();
                String episodes = tvShowEpisodes.getText().toString().trim();
                String thoughts = myThoughts.getText().toString().trim();
                String ytTrailer = trailerLink.getText().toString().trim();

                if (showName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the TV show name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prepare data map
                Map<String, Object> tvShowMemo = new HashMap<>();
                tvShowMemo.put("portraitUrl", portraitUrl);
                tvShowMemo.put("landscapeUrl", landscapeUrl);
                tvShowMemo.put("showName", showName);
                tvShowMemo.put("watchedDate", watchedDate);
                tvShowMemo.put("description", description);
                tvShowMemo.put("episodes", episodes);
                tvShowMemo.put("thoughts", thoughts);
                tvShowMemo.put("trailerUrl", ytTrailer);

                db.collection("TVShow_memo")
                        .add(tvShowMemo)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Memo uploaded successfully!", Toast.LENGTH_SHORT).show();

                                    imageUrlInput.setText("");
                                    landscapeimageUrlInput.setText("");

                                    tvShowPortrait.setImageResource(R.drawable.tumb);
                                    tvShowLandscape.setImageResource(R.drawable.large);

                                    tvShowName.setText("");
                                    dateWatched.setText("");
                                    tvShowDescription.setText("");
                                    tvShowEpisodes.setText("");
                                    myThoughts.setText("");

                                    trailerLink.setText("");
                                    resetWebView(videoPreview);
                                } else {
                                    Toast.makeText(getContext(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void loadYouTube_Preview(String url) {
        String videoId = extractYouTubeVideoId(url);

        if (!videoId.isEmpty()) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            String iframeHtml = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

            videoPreview.getSettings().setJavaScriptEnabled(true);
            videoPreview.loadData(iframeHtml, "text/html", "utf-8");
        }
    }

    private String extractYouTubeVideoId(String url) {
        String pattern = "(?:(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/.*(?:\\?|&)v=|youtu\\.be\\/|youtube\\.com\\/embed\\/|youtube\\.com\\/v\\/))([a-zA-Z0-9_-]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }

    private void resetWebView(WebView webView) {
        String blackScreenHtml = "<html><body style='background-color:black;'></body></html>";
        webView.loadData(blackScreenHtml, "text/html", "utf-8");
    }


}
