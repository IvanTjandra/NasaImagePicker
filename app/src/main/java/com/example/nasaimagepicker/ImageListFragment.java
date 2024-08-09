package com.example.nasaimagepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * ImageListFragment is a Fragment that displays a list of images in a ListView.
 * It includes functionality to display a description when an item is long-pressed,
 * and shows an empty view when the list is empty.
 */
public class ImageListFragment extends Fragment {

    private ListView listView;
    private TextView descriptionTextView;
    private TextView emptyView;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_list, container, false);

        listView = rootView.findViewById(R.id.list_view);
        descriptionTextView = rootView.findViewById(R.id.description_text_view);
        emptyView = rootView.findViewById(R.id.empty_view);

        // Set the empty view to be displayed when the list is empty
        listView.setEmptyView(emptyView);

        // Set a long click listener to display the description when an item is long-pressed
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String description = "Description for the selected item";
            descriptionTextView.setText(description);
            descriptionTextView.setVisibility(View.VISIBLE);
            return true;
        });

        return rootView;
    }
}
