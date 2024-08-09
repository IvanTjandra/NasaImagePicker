package com.example.nasaimagepicker;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * HistoryActivity displays a list of previously accessed images in the NASA Image Picker app.
 * Users can view details about when an image was accessed or delete history entries.
 */
public class HistoryActivity extends BaseActivity {

    /**
     * The ListView that displays the list of history items.
     */
    private ListView listView;

    /**
     * The adapter that populates the ListView with history items.
     */
    private ImageAdapter adapter;

    /**
     * The helper class that interacts with the history database.
     */
    private HistoryDatabaseHelper historyDatabaseHelper;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * Sets up the navigation, initializes the history database helper, and loads the history images.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setupNavigation();

        historyDatabaseHelper = new HistoryDatabaseHelper(this);
        listView = findViewById(R.id.list_view_history);

        loadHistoryImages();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ImageItem imageItem = adapter.getItem(position);
            showImageDescription(imageItem);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            ImageItem imageItem = adapter.getItem(position);
            new AlertDialog.Builder(HistoryActivity.this)
                    .setTitle(R.string.delete_image_title)
                    .setMessage(R.string.delete_image_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        historyDatabaseHelper.deleteHistory(imageItem.getId());
                        loadHistoryImages();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        });
    }

    /**
     * Loads the history images from the database and populates the ListView.
     */
    private void loadHistoryImages() {
        List<ImageItem> imageItemList = historyDatabaseHelper.getAllHistory();
        adapter = new ImageAdapter(this, imageItemList);
        listView.setAdapter(adapter);
    }

    /**
     * Displays an AlertDialog showing the description and access date of the selected image.
     *
     * @param imageItem The ImageItem containing the description and access date to be displayed.
     */
    private void showImageDescription(ImageItem imageItem) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.description)
                .setMessage(imageItem.getDescription() + "\nAccessed on: " + imageItem.getDateAccessed())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Provides the help message specific to this activity.
     *
     * @return A string containing the help message for the HistoryActivity.
     */
    @Override
    protected String getHelpMessage() {
        return getString(R.string.help_message_history_activity);
    }
}
