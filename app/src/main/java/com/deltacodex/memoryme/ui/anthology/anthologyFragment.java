package com.deltacodex.memoryme.ui.anthology;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.deltacodex.memoryme.Adapter.AlphabetCategoryAdapter;
import com.deltacodex.memoryme.NotificationHelper;
import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.Single_AnthologyActivity;
import com.deltacodex.memoryme.model.TVShow;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class anthologyFragment extends Fragment {
    private RecyclerView rvAlphabetCategories;
    private CollectionReference tvShowsRef;
    private NotificationHelper notificationHelper;
    private final List<TVShow> tvShowList = new ArrayList<>();
    private final Map<Character, List<TVShow>> groupedShows = new TreeMap<>();
    private AlphabetCategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anthology, container, false);
        rvAlphabetCategories = view.findViewById(R.id.rvAlphabetCategories);
        rvAlphabetCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Attach an empty adapter initially
        adapter = new AlphabetCategoryAdapter(getContext(), new ArrayList<>(), new HashMap<>(), tvShow -> {

        },AlphabetCategoryAdapter.ContentType.ANTHOLOGY);
        rvAlphabetCategories.setAdapter(adapter);

        tvShowsRef = FirebaseFirestore.getInstance().collection("Anthology_memo");
        notificationHelper = new NotificationHelper(requireContext(), new ArrayList<>());
        return view;
    }

    private void fetchTVShows() {
        tvShowsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvShowList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    TVShow show = document.toObject(TVShow.class); // Convert Firestore document to TVShow object
                    show.setDocumentId(document.getId());
                    tvShowList.add(show);
                }
                groupTVShows();
                setupAdapter();
                notificationHelper.updateMemos(tvShowList);
            } else {
                Log.e("FirestoreError", "Error fetching data: ", task.getException());
            }
        });
    }

    private void groupTVShows() {
        groupedShows.clear();
        for (TVShow show : tvShowList) {
            char letter = Character.toUpperCase(show.getShowName().charAt(0));
            if (!groupedShows.containsKey(letter)) {
                groupedShows.put(letter, new ArrayList<>());
            }
            groupedShows.get(letter).add(show);
        }
        if (notificationHelper != null) {
            notificationHelper.updateMemos(tvShowList);
        }
    }

    private void setupAdapter() {
        List<Character> alphabetList = new ArrayList<>(groupedShows.keySet());
        adapter = new AlphabetCategoryAdapter(getContext(), alphabetList, groupedShows, tvShow -> {

            Intent intent = new Intent(getContext(), Single_AnthologyActivity.class);
            intent.putExtra("showName", tvShow.getShowName());
            intent.putExtra("landscapeUrl", tvShow.getLandscapeUrl());
            intent.putExtra("watchedDate", tvShow.getWatchedDate());
            startActivity(intent);
        },AlphabetCategoryAdapter.ContentType.ANTHOLOGY);
        rvAlphabetCategories.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTVShows();
        notificationHelper.startNotifications(100000);
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationHelper.stopNotifications();
    }
}