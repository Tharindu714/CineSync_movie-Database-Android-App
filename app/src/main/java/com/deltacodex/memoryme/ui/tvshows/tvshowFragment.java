package com.deltacodex.memoryme.ui.tvshows;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.deltacodex.memoryme.Adapter.AlphabetCategoryAdapter;
import com.deltacodex.memoryme.NotificationHelper;
import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.Single_TvshowActivity;
import com.deltacodex.memoryme.model.TVShow;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class tvshowFragment extends Fragment {

    private RecyclerView rvAlphabetCategories;
    private CollectionReference tvShowsRef;
    private final List<TVShow> tvShowList = new ArrayList<>();
    private NotificationHelper notificationHelper;
    private final Map<Character, List<TVShow>> groupedShows = new TreeMap<>();
    private Spinner spinnerTVShows;
    private AlphabetCategoryAdapter adapter;

    // Predefined TV Show Names
    private final List<String> tvShowNames = Arrays.asList(
            "All TV Shows", "Arcane", "Banshee", "Cobra Kai", "Creeped Out", "Death Note", "Goosebumps",
            "Knight Rider", "MacGyver", "Reacher", "Supernatural", "Superman", "Stranger Things",
            "The Starin", "The Boys", "The Mandalorian", "From", "Walking Dead","Squid Game",
            "Vampire Diaries", "Invincible", "Peaky Blinders", "Breaking Bad", "Prison Break", "Better Call Saul", "Lost","Arrow"
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tvshow, container, false);

        // RecyclerView Setup
        rvAlphabetCategories = view.findViewById(R.id.rvAlphabetCategories);
        rvAlphabetCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AlphabetCategoryAdapter(getContext(), new ArrayList<>(), new HashMap<>(), tvShow -> {
            // Click Event - Open TV Show Details
            if (tvShow != null && tvShow.getShowName() != null) {
                Intent intent = new Intent(getContext(), Single_TvshowActivity.class);
                intent.putExtra("showName", tvShow.getShowName());
                intent.putExtra("description", tvShow.getDescription());
                intent.putExtra("landscapeUrl", tvShow.getLandscapeUrl());
                intent.putExtra("episodes", tvShow.getEpisodes());
                intent.putExtra("watchedDate", tvShow.getWatchedDate());
                intent.putExtra("trailerUrl", tvShow.getTrailerUrl());
                startActivity(intent);
            } else {
                Log.e("TVShow Click", "Data not ready yet!");
            }
        }, AlphabetCategoryAdapter.ContentType.TV_SHOW);

        rvAlphabetCategories.setAdapter(adapter);

        // Firestore Reference
        tvShowsRef = FirebaseFirestore.getInstance().collection("TVShow_memo");

        // Notification Helper
        notificationHelper = new NotificationHelper(requireContext(), new ArrayList<>());

        // Spinner Setup
        spinnerTVShows = view.findViewById(R.id.spinnerTVShows);
        setupSpinner();

        return view;
    }

    // 🔹 Setup Spinner for TV Show Selection
    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, tvShowNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTVShows.setAdapter(spinnerAdapter);

        spinnerTVShows.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedShow = tvShowNames.get(position);
                Log.d("Spinner", "Selected TV Show: " + selectedShow);
                filterTVShows(selectedShow); // Apply Filter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // 🔹 Fetch TV Shows from Firestore
    private void fetchTVShows() {
        tvShowsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvShowList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    TVShow show = document.toObject(TVShow.class);
                    show.setDocumentId(document.getId());
                    tvShowList.add(show);
                }
                groupTVShows(tvShowList);  // Group & Update UI
                requireActivity().runOnUiThread(this::setupAdapter);
                notificationHelper.updateMemos(tvShowList);
            } else {
                Log.e("FirestoreError", "Error fetching data: ", task.getException());
            }
        });
    }

    // 🔹 Filter TV Shows based on Spinner Selection
    private void filterTVShows(String selectedShow) {
        List<TVShow> filteredList = new ArrayList<>();
        String normalizedSearch = normalizeSearch(selectedShow);

        if (normalizedSearch.equals("all tv shows")) {
            filteredList.addAll(tvShowList);  // Show all TV shows
        } else {
            for (TVShow show : tvShowList) {
                if (normalizeSearch(show.getShowName()).contains(normalizedSearch)) {
                    filteredList.add(show);
                }
            }
        }

        groupTVShows(filteredList);
        requireActivity().runOnUiThread(this::setupAdapter);
    }


    // 🔹 Group TV Shows Alphabetically
    private void groupTVShows(List<TVShow> shows) {
        groupedShows.clear();
        for (TVShow show : shows) {
            char letter = Character.toUpperCase(show.getShowName().charAt(0));
            groupedShows.computeIfAbsent(letter, k -> new ArrayList<>()).add(show);
        }
    }

    // 🔹 Setup Adapter & Update RecyclerView
    private void setupAdapter() {
        List<Character> alphabetList = new ArrayList<>(groupedShows.keySet());
        adapter = new AlphabetCategoryAdapter(getContext(), alphabetList, groupedShows, tvShow -> {
            if (tvShow != null && tvShow.getShowName() != null) {
                Intent intent = new Intent(getContext(), Single_TvshowActivity.class);
                intent.putExtra("showName", tvShow.getShowName());
                intent.putExtra("description", tvShow.getDescription());
                intent.putExtra("landscapeUrl", tvShow.getLandscapeUrl());
                intent.putExtra("episodes", tvShow.getEpisodes());
                intent.putExtra("watchedDate", tvShow.getWatchedDate());
                intent.putExtra("trailerUrl", tvShow.getTrailerUrl());
                startActivity(intent);
            } else {
                Log.e("TVShow Click", "Data not ready yet!");
            }
        }, AlphabetCategoryAdapter.ContentType.TV_SHOW);
        rvAlphabetCategories.setAdapter(adapter);
    }
    private String normalizeSearch(String input) {
        if (input == null) return ""; // Handle null case

        return input.toLowerCase().replaceAll("[^\\p{L}\\p{N} ]", "") // Keep all letters (including Sinhala) & numbers
                .trim(); // Remove leading/trailing spaces
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTVShows();  // Load all TV shows initially
        notificationHelper.startNotifications(100000);
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationHelper.stopNotifications();
    }
}
