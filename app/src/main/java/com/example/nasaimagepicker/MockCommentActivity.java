package com.example.nasaimagepicker;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * MockCommentActivity allows users to select an image from a list of saved images
 * and submit a mock comment. The comment is not actually saved but is acknowledged
 * through a Toast message.
 */
public class MockCommentActivity extends BaseActivity {

    private ListView listView;
    private ImageAdapter adapter;
    private ImageDatabaseHelper databaseHelper;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * Sets up the navigation, initializes views, and loads saved images.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_comment);

        setupNavigation();

        listView = findViewById(R.id.list_view_saved_images);
        databaseHelper = new ImageDatabaseHelper(this);

        loadSavedImages();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ImageItem imageItem = adapter.getItem(position);
            showCommentDialog();
        });
    }

    /**
     * Loads the saved images from the database and populates the ListView.
     * If no images are found, a Toast message is displayed.
     */
    private void loadSavedImages() {
        List<ImageItem> imageItemList = databaseHelper.getAllImages();
        if (imageItemList.isEmpty()) {
            Toast.makeText(this, R.string.no_image_saved, Toast.LENGTH_SHORT).show();
        }
        adapter = new ImageAdapter(this, imageItemList);
        listView.setAdapter(adapter);
    }

    /**
     * Displays a dialog that allows the user to enter a comment.
     * The comment is acknowledged with a Toast message, but not actually saved.
     */
    private void showCommentDialog() {
        // Create an EditText field to input the comment
        final EditText commentInput = new EditText(this);
        commentInput.setHint(R.string.enter_comment);

        new AlertDialog.Builder(this)
                .setTitle(R.string.send_comment)
                .setView(commentInput)
                .setPositiveButton(R.string.send_comment, (dialog, which) -> {
                    String comment = commentInput.getText().toString();
                    if (!comment.isEmpty()) {
                        // Display a Toast indicating the comment was sent
                        Toast.makeText(this, R.string.comment_sent, Toast.LENGTH_SHORT).show();
                    } else {
                        // Display a Toast indicating the comment is empty
                        Toast.makeText(this, R.string.comment_empty, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Provides the help message specific to this activity.
     *
     * @return A string containing the help message for MockCommentActivity.
     */
    @Override
    protected String getHelpMessage() {
        return getString(R.string.help_message_mock_comment_activity);
    }
}
