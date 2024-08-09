package com.example.nasaimagepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * SavedImagesActivity displays a list of saved images stored in the local database.
 * Users can view images in their browser or delete them from the database.
 */
public class SavedImagesActivity extends BaseActivity {

    private ListView listView;
    private ImageAdapter adapter;
    private ImageDatabaseHelper imageDatabaseHelper;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * Sets up the navigation, initializes views, and loads saved images from the database.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);

        setupNavigation();

        imageDatabaseHelper = new ImageDatabaseHelper(this);
        listView = findViewById(R.id.list_view_saved_images);

        loadSavedImages();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ImageItem imageItem = adapter.getItem(position);
            openImageInBrowser(imageItem.getImageUrl());
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            ImageItem imageItem = adapter.getItem(position);
            new AlertDialog.Builder(SavedImagesActivity.this)
                    .setTitle(R.string.delete_image_title)
                    .setMessage(R.string.delete_image_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        imageDatabaseHelper.deleteImage(imageItem.getId());
                        loadSavedImages();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        });
    }

    /**
     * Loads the saved images from the database and populates the ListView.
     */
    private void loadSavedImages() {
        List<ImageItem> imageItemList = imageDatabaseHelper.getAllImages();
        adapter = new ImageAdapter(this, imageItemList);
        listView.setAdapter(adapter);
    }

    /**
     * Opens the selected image in the default browser.
     *
     * @param url The URL of the image to be opened.
     */
    private void openImageInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * Provides the help message specific to this activity.
     *
     * @return A string containing the help message for SavedImagesActivity.
     */
    @Override
    protected String getHelpMessage() {
        return getString(R.string.help_message_saved_images_activity);
    }
}
