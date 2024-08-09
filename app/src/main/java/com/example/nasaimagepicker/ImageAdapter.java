package com.example.nasaimagepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * ImageAdapter is a custom ArrayAdapter for displaying ImageItem objects in a ListView.
 * It handles the binding of image data, including the URL, date, and image itself, to the views in the list item layout.
 */
public class ImageAdapter extends ArrayAdapter<ImageItem> {

    /**
     * Constructs a new ImageAdapter.
     *
     * @param context    The current context. Used to inflate the layout file.
     * @param imageItems A list of ImageItem objects to display in the ListView.
     */
    public ImageAdapter(Context context, List<ImageItem> imageItems) {
        super(context, 0, imageItems);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position    The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using it.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item, parent, false);
        }

        ImageItem imageItem = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);
        TextView urlTextView = convertView.findViewById(R.id.url_text_view);

        if (imageItem != null) {
            dateTextView.setText(imageItem.getDate());
            urlTextView.setText(imageItem.getImageUrl());

            // Load the image from the URL into the ImageView using Picasso
            Picasso.get().load(imageItem.getImageUrl()).into(imageView);
        }

        return convertView;
    }
}
