package com.deltacodex.memoryme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.model.TVShow;

import java.util.List;
import java.util.Map;

public class AlphabetCategoryAdapter extends RecyclerView.Adapter<AlphabetCategoryAdapter.AlphabetViewHolder> {

    private final List<Character> alphabetList;
    private final Map<Character, List<TVShow>> groupedShows;
    private final Context context;
    private final OnTVShowClickListener listener;
    private final ContentType contentType;

    public interface OnTVShowClickListener {
        void onTVShowClick(TVShow tvShow);
    }

    public enum ContentType {
        TV_SHOW, MOVIE, ANTHOLOGY
    }


    public AlphabetCategoryAdapter(Context context, List<Character> alphabetList, Map<Character, List<TVShow>> groupedShows,OnTVShowClickListener listener, ContentType contentType) {
        this.context = context;
        this.alphabetList = alphabetList;
        this.groupedShows = groupedShows;
        this.listener = listener;
        this.contentType = contentType;
    }

    @NonNull
    @Override
    public AlphabetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alphabet_category, parent, false);
        return new AlphabetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlphabetViewHolder holder, int position) {
        char letter = alphabetList.get(position);
        holder.tvAlphabetHeader.setText(String.valueOf(letter));
        List<TVShow> tvShows = groupedShows.get(letter);

        // Set up the inner RecyclerView (horizontal)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        holder.rvTvShowsHorizontal.setLayoutManager(gridLayoutManager);
        switch (contentType) {
            case TV_SHOW:
                holder.rvTvShowsHorizontal.setAdapter(new TvShowAdapter(context, tvShows, listener));
                break;
            case MOVIE:
                holder.rvTvShowsHorizontal.setAdapter(new MovieAdapter(context, tvShows, listener));
                break;
            case ANTHOLOGY:
                holder.rvTvShowsHorizontal.setAdapter(new AnthoAdapter(context, tvShows, listener));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return alphabetList.size();
    }

    public static class AlphabetViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlphabetHeader;
        RecyclerView rvTvShowsHorizontal;
        public AlphabetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlphabetHeader = itemView.findViewById(R.id.tvAlphabetHeader);
            rvTvShowsHorizontal = itemView.findViewById(R.id.rvTvShowsHorizontal);
        }
    }
}
