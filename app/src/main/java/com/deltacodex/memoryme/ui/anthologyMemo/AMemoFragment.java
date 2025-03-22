package com.deltacodex.memoryme.ui.anthologyMemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class AMemoFragment extends Fragment {

    private EditText imageUrlInput, landscapeimageUrlInput, tvShowName, dateWatched;
    private ImageView tvShowPortrait, tvShowLandscape;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_memo, container, false);
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
        tvShowPortrait = view.findViewById(R.id.tvShowPortrait);
        tvShowLandscape = view.findViewById(R.id.tvShowLandscape);
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

        // Set onClick listener to upload the memo data to Firestore
        uploadTvshowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gather all data from the inputs
                String portraitUrl = imageUrlInput.getText().toString().trim();
                String landscapeUrl = landscapeimageUrlInput.getText().toString().trim();
                String showName = tvShowName.getText().toString().trim();
                String watchedDate = dateWatched.getText().toString().trim();

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

                db.collection("Anthology_memo")
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
                                } else {
                                    Toast.makeText(getContext(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

}