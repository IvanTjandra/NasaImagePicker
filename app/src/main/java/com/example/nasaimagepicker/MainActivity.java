package com.example.nasaimagepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button pickDateButton, saveImageButton, viewSavedImagesButton, openUrlButton;
    private ProgressBar progressBar;
    private TextView textViewDate, textViewUrl;
    private String imageUrl, imageDate, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NASA Image Picker");
        getSupportActionBar().setSubtitle("v1.0");

        pickDateButton = findViewById(R.id.button_pick_date);
        saveImageButton = findViewById(R.id.button_save_image);
        viewSavedImagesButton = findViewById(R.id.button_view_saved_images);
        openUrlButton = findViewById(R.id.button_open_url);
        progressBar = findViewById(R.id.progress_bar);
        textViewDate = findViewById(R.id.text_view_date);
        textViewUrl = findViewById(R.id.text_view_url);

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        new FetchImageTask().execute(selectedDate);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null && imageDate != null && imagePath != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("NASA_IMAGES", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(imageDate, imageUrl + "," + imagePath);
                    editor.apply();
                    Toast.makeText(MainActivity.this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_image_to_save, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewSavedImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedImagesActivity.class);
                startActivity(intent);
            }
        });

        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_url_available, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Snackbar.make(findViewById(R.id.main_layout), R.string.welcome_message, Snackbar.LENGTH_LONG).show();
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
                    .setMessage(R.string.help_message_main)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String date = params[0];
            String apiKey = "DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d";
            String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + date;
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    imageDate = jsonObject.getString("date");
                    imageUrl = jsonObject.getString("url");

                    textViewDate.setText(imageDate);
                    textViewUrl.setText(imageUrl);

                    new DownloadImageTask().execute(imageUrl);

                    openUrlButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, R.string.error_loading_image, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.error_fetching_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                File directory = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = new File(directory, imageDate + ".jpg");

                FileOutputStream out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                return imageFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String path) {
            if (path != null) {
                imagePath = path;
            } else {
                Toast.makeText(MainActivity.this, R.string.error_saving_image, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
