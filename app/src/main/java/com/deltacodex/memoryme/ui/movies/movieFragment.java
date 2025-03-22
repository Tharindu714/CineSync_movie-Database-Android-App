package com.deltacodex.memoryme.ui.movies;

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
import android.widget.Spinner;
import com.deltacodex.memoryme.Adapter.AlphabetCategoryAdapter;
import com.deltacodex.memoryme.Adapter.MovieCategoryAdapter;

import com.deltacodex.memoryme.NotificationHelper;
import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.Single_TvshowActivity;
import com.deltacodex.memoryme.Single_movieActivity;
import com.deltacodex.memoryme.model.MovieCategory;
import com.deltacodex.memoryme.model.TVShow;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class movieFragment extends Fragment {
    private Spinner spinnerMovies;
    private List<MovieCategory> movieCategories = new ArrayList<>();
    private RecyclerView rvAlphabetCategories;
    private CollectionReference tvShowsRef;
    private final List<TVShow> tvShowList = new ArrayList<>();
    private NotificationHelper notificationHelper;
    private final Map<Character, List<TVShow>> groupedShows = new TreeMap<>();
    private AlphabetCategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
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
        }, AlphabetCategoryAdapter.ContentType.MOVIE);
        rvAlphabetCategories.setAdapter(adapter);
        notificationHelper = new NotificationHelper(requireContext(), new ArrayList<>());
        // Initialize Spinner
        spinnerMovies = view.findViewById(R.id.spinner_movies);
        // Populate Spinner
        movieCategories.add(new MovieCategory("All", R.drawable.all));
        movieCategories.add(new MovieCategory("12 Rounds", R.drawable.twll));
        movieCategories.add(new MovieCategory("Avengers", R.drawable.avengers));
        movieCategories.add(new MovieCategory("Alien", R.drawable.alien));
        movieCategories.add(new MovieCategory("Ant-Man", R.drawable.ant));
        movieCategories.add(new MovieCategory("Annabelle", R.drawable.ann));
        movieCategories.add(new MovieCategory("Bruce Lee", R.drawable.bruce));
        movieCategories.add(new MovieCategory("Baaghi", R.drawable.baaghi));
        movieCategories.add(new MovieCategory("Boss Baby", R.drawable.boss_baby));
        movieCategories.add(new MovieCategory("Batman", R.drawable.batman));
        movieCategories.add(new MovieCategory("Captain America", R.drawable.cap));
        movieCategories.add(new MovieCategory("Conjuring", R.drawable._con));
        movieCategories.add(new MovieCategory("Creed", R.drawable.creed));
        movieCategories.add(new MovieCategory("Death Note", R.drawable.deathnote));
        movieCategories.add(new MovieCategory("Divergent", R.drawable.divergent));
        movieCategories.add(new MovieCategory("Deadpool", R.drawable.deadpool));
        movieCategories.add(new MovieCategory("Die Hard", R.drawable.die));
        movieCategories.add(new MovieCategory("Expendables", R.drawable.expen));
        movieCategories.add(new MovieCategory("Escape Plan", R.drawable.escapeplan));
        movieCategories.add(new MovieCategory("Equalizer", R.drawable.equlizer));
        movieCategories.add(new MovieCategory("Fast & Furious", R.drawable.fast));
        movieCategories.add(new MovieCategory("Green Street", R.drawable.green));
        movieCategories.add(new MovieCategory("Ghostbusters", R.drawable.ghostbus));
        movieCategories.add(new MovieCategory("Guardians Of the Galaxy", R.drawable.gog));
        movieCategories.add(new MovieCategory("Godzilla", R.drawable.godzilla));
        movieCategories.add(new MovieCategory("Harry Potter", R.drawable.harry));
        movieCategories.add(new MovieCategory("Hulk", R.drawable.hulk));
        movieCategories.add(new MovieCategory("Halloween", R.drawable.halloween));
        movieCategories.add(new MovieCategory("Hard Target", R.drawable.hard));
        movieCategories.add(new MovieCategory("How to train your Dragon", R.drawable.dragon));
        movieCategories.add(new MovieCategory("Ip Man", R.drawable.ipman));
        movieCategories.add(new MovieCategory("Indiana Jones", R.drawable.indiana));
        movieCategories.add(new MovieCategory("Iron Man", R.drawable.ironman));
        movieCategories.add(new MovieCategory("IT", R.drawable.itt));
        movieCategories.add(new MovieCategory("Jurassic", R.drawable.jurrasic));
        movieCategories.add(new MovieCategory("John Wick", R.drawable.wick));
        movieCategories.add(new MovieCategory("Jack Reacher", R.drawable.jackreacher));
        movieCategories.add(new MovieCategory("James Bond", R.drawable.james));
        movieCategories.add(new MovieCategory("Joker", R.drawable.joker));
        movieCategories.add(new MovieCategory("Kill Bill", R.drawable.kill));
        movieCategories.add(new MovieCategory("King Kong", R.drawable.kong));
        movieCategories.add(new MovieCategory("Kung Fu Panda", R.drawable.panda));
        movieCategories.add(new MovieCategory("Kickboxer", R.drawable.kickbox));
        movieCategories.add(new MovieCategory("Lord of the Rings", R.drawable.lord));
        movieCategories.add(new MovieCategory("Mazerunner", R.drawable.mazerunner));
        movieCategories.add(new MovieCategory("Mission Impossible", R.drawable.mi));
        movieCategories.add(new MovieCategory("Matrix", R.drawable.matrix));
        movieCategories.add(new MovieCategory("Marine", R.drawable.marine));
        movieCategories.add(new MovieCategory("Mad Max", R.drawable.max));
        movieCategories.add(new MovieCategory("Ong Bak", R.drawable.ong));
        movieCategories.add(new MovieCategory("Ouija", R.drawable.ouija));
        movieCategories.add(new MovieCategory("Predator", R.drawable.predator));
        movieCategories.add(new MovieCategory("Pacific Rim", R.drawable.pacific));
        movieCategories.add(new MovieCategory("Paranormal Activity", R.drawable.paranormal));
        movieCategories.add(new MovieCategory("Quiet Place", R.drawable.quiet));
        movieCategories.add(new MovieCategory("Resident Evil", R.drawable.res));
        movieCategories.add(new MovieCategory("Rambo", R.drawable.rambo));
        movieCategories.add(new MovieCategory("RoboCop", R.drawable.robo));
        movieCategories.add(new MovieCategory("Rocky", R.drawable.rocky));
        movieCategories.add(new MovieCategory("Sonic", R.drawable.sonic));
        movieCategories.add(new MovieCategory("Spider man", R.drawable.spider));
        movieCategories.add(new MovieCategory("Superman", R.drawable.superman));
        movieCategories.add(new MovieCategory("Saw", R.drawable.saw));
        movieCategories.add(new MovieCategory("Twilight", R.drawable.twilight));
        movieCategories.add(new MovieCategory("Transformers", R.drawable.transformer));
        movieCategories.add(new MovieCategory("Transporter", R.drawable.trans));
        movieCategories.add(new MovieCategory("Terminator", R.drawable.term));
        movieCategories.add(new MovieCategory("Taken", R.drawable.taken));
        movieCategories.add(new MovieCategory("The Meg", R.drawable.meg));
        movieCategories.add(new MovieCategory("The Thing", R.drawable.thing));
        movieCategories.add(new MovieCategory("Thor", R.drawable.thor));
        movieCategories.add(new MovieCategory("Universal Soldier", R.drawable.uni));
        movieCategories.add(new MovieCategory("Undisputed", R.drawable.undis));
        movieCategories.add(new MovieCategory("Venom", R.drawable.venom));
        movieCategories.add(new MovieCategory("Wrong Turn", R.drawable.wrong));
        movieCategories.add(new MovieCategory("X-Men", R.drawable.xm));

        // Set Custom Adapter for Spinner
        MovieCategoryAdapter spinnerAdapter = new MovieCategoryAdapter(getContext(), movieCategories);
        spinnerMovies.setAdapter(spinnerAdapter);

        // Firestore Reference
        tvShowsRef = FirebaseFirestore.getInstance().collection("Movie_memo");

        // Spinner Selection Listener
        spinnerMovies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterMovies(movieCategories.get(position).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return view;
    }

    /**
     * Fetches movie data from Firestore and populates tvShowList
     */
    private void fetchMovies() {
        tvShowsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvShowList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    TVShow show = document.toObject(TVShow.class);
                    show.setDocumentId(document.getId());
                    tvShowList.add(show);
                }
                groupFilteredMovies(tvShowList);  // Group & Update UI
                requireActivity().runOnUiThread(this::setupAdapter);
                notificationHelper.updateMemos(tvShowList);
                // Load all movies initially
                filterMovies("All");
            } else {
                Log.e("Firestore", "Error fetching movies", task.getException());
            }
        });
    }

    /**
     * Filters movies based on the selected category and updates RecyclerView
     */
    private void filterMovies(String selectedCategory) {
        List<TVShow> filteredList = new ArrayList<>();

        // Normalize selected category
        String normalizedCategory = normalizeText(selectedCategory);

        if (normalizedCategory.equals("all")) {
            filteredList.addAll(tvShowList); // Show all movies
        } else {
            for (TVShow show : tvShowList) {
                // Normalize movie name before comparison
                String normalizedShowName = normalizeText(show.getShowName());
                if (normalizedShowName.contains(normalizedCategory)) {
                    filteredList.add(show);
                }
            }
        }

        // Update the adapter
        groupFilteredMovies(filteredList);
        setupAdapter();
    }

    /**
     * Groups filtered movies alphabetically
     */
    private void groupFilteredMovies(List<TVShow> filteredList) {
        groupedShows.clear();
        for (TVShow show : filteredList) {
            String normalizedShowName = normalizeText(show.getShowName()); // Normalize movie name

            if (!normalizedShowName.isEmpty()) {
                char letter = Character.toUpperCase(normalizedShowName.charAt(0)); // Get first valid character
                groupedShows.computeIfAbsent(letter, k -> new ArrayList<>()).add(show);
            }
        }
    }

    /**
     * Sets up and updates the RecyclerView adapter
     */
    private void setupAdapter() {
        List<Character> alphabetList = new ArrayList<>(groupedShows.keySet());
        adapter = new AlphabetCategoryAdapter(getContext(), alphabetList, groupedShows, tvShow -> {
            Intent intent = new Intent(getContext(), Single_movieActivity.class);
            intent.putExtra("showName", tvShow.getShowName());
            intent.putExtra("description", tvShow.getDescription());
            intent.putExtra("landscapeUrl", tvShow.getLandscapeUrl());
            intent.putExtra("watchedDate", tvShow.getWatchedDate());
            intent.putExtra("trailerUrl", tvShow.getTrailerUrl());
            startActivity(intent);
        }, AlphabetCategoryAdapter.ContentType.MOVIE);
        rvAlphabetCategories.setAdapter(adapter);
    }

    private String normalizeText(String text) {
        if (text == null) return ""; // Handle null case

        return text.toLowerCase().replaceAll("[^\\p{L}\\p{N} ]", "") // Keep all letters (including Sinhala) & numbers
                .trim(); // Remove leading/trailing spaces
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchMovies();
        notificationHelper.startNotifications(100000);
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationHelper.stopNotifications();
    }
}
