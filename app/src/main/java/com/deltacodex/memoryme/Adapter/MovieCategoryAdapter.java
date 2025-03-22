package com.deltacodex.memoryme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deltacodex.memoryme.R;
import com.deltacodex.memoryme.model.MovieCategory;

import java.util.List;

public class MovieCategoryAdapter extends ArrayAdapter<MovieCategory> {
    private final Context context;
    private final List<MovieCategory> categories;

    public MovieCategoryAdapter(Context context, List<MovieCategory> categories) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_movie, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.spinner_item_image);
        TextView textView = convertView.findViewById(R.id.spinner_item_text);

        MovieCategory category = categories.get(position);
        imageView.setImageResource(category.getImageResId()); // Set Image
        textView.setText(category.getName()+" Movies"); // Set Name

        return convertView;
    }
}
