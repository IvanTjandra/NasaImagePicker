package com.example.nasaimagepicker;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Map;

public class SavedImagesActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> savedImageList;
    private ImageAdapter adapter;
    private SharedPreferences sharedPreferences;
    private ImageView imageView;
    private TextView textViewUrl;
    private TextView noImageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Images");
        getSupportActionBar().setSubtitle("v1.0");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_view_saved_images);
        imageView = findViewById(R.id.image_view_saved);
        textViewUrl = findViewById(R.id.text_view_url_saved);
        noImageTextView = findViewById(R.id.no_image_text_view);
        sharedPreferences = getSharedPreferences("NASA_IMAGES", MODE_PRIVATE);
        savedImageList = new ArrayList<>();

        loadSavedImages();

        if (savedImageList.isEmpty()) {
            noImageTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noImageTextView.setVisibility(View.GONE);
            adapter = new ImageAdapter(this, savedImageList);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = savedImageList.get(position);
            String[] parts = selectedItem.split(",");
            String url = parts[1];
            String imagePath = parts[2];

            textViewUrl.setText(url);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            final String selectedItem = savedImageList.get(position);
            String[] parts = selectedItem.split(",");
            final String date = parts[0];

            new AlertDialog.Builder(SavedImagesActivity.this)
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(R.string.confirm_delete_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(date);
                        editor.apply();

                        savedImageList.remove(selectedItem);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(SavedImagesActivity.this, R.string.image_deleted, Toast.LENGTH_SHORT).show();

                        if (savedImageList.isEmpty()) {
                            noImageTextView.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.help)
                    .setMessage(R.string.help_message_saved_images)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSavedImages() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            savedImageList.add(entry.getKey() + "," + entry.getValue().toString());
        }
    }
}
