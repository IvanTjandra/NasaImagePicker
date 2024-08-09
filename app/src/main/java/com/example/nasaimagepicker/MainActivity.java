package com.example.nasaimagepicker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MainActivity handles the main functionality of the NASA Image Picker app.
 * It allows users to select a date, fetch an image from NASA's APOD API for that date,
 * view the image, and save it to the database. The app also tracks history whenever
 * the user views an image.
 */
public class MainActivity extends BaseActivity {

    private static final String NASA_API_KEY = "DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String LAST_DATE_KEY = "lastDate";

    private TextView selectedDateText;
    private TextView imageUrlText;
    private Button viewImageButton;
    private Button saveImageButton;
    private ProgressBar progressBar;

    private String imageUrl;
    private String selectedDate;

    private ImageDatabaseHelper imageDatabaseHelper;
    private HistoryDatabaseHelper historyDatabaseHelper;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * Sets up the navigation, initializes views and database helpers, and loads the last selected date.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigation();

        selectedDateText = findViewById(R.id.selected_date_text);
        imageUrlText = findViewById(R.id.image_url_text);
        viewImageButton = findViewById(R.id.button_view_image);
        saveImageButton = findViewById(R.id.button_save_image);
        progressBar = findViewById(R.id.progress_bar);

        imageDatabaseHelper = new ImageDatabaseHelper(this);
        historyDatabaseHelper = new HistoryDatabaseHelper(this);

        viewImageButton.setVisibility(View.GONE);
        saveImageButton.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        selectedDate = sharedPreferences.getString(LAST_DATE_KEY, null);
        if (selectedDate != null) {
            selectedDateText.setText(selectedDate);
            fetchImageData(selectedDate);
        }

        findViewById(R.id.button_pick_date).setOnClickListener(v -> showDatePicker());
        viewImageButton.setOnClickListener(v -> viewImage());
        saveImageButton.setOnClickListener(v -> saveImage());
    }

    /**
     * Displays a date picker dialog for the user to select a date.
     */
    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSelectedListener(date -> {
            selectedDate = date;
            selectedDateText.setText(selectedDate);
            fetchImageData(selectedDate);
        });
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Initiates the process of fetching image data from NASA's APOD API for the selected date.
     *
     * @param date The selected date for which to fetch the image.
     */
    private void fetchImageData(String date) {
        new FetchImageTask().execute(date);
    }

    /**
     * AsyncTask that fetches image data from NASA's APOD API in the background.
     * It shows a progress bar while fetching and handles the response once done.
     */
    private class FetchImageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            viewImageButton.setVisibility(View.GONE);
            saveImageButton.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String date = params[0];
            try {
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=" + NASA_API_KEY + "&date=" + date);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            // Show progress bar for 3 seconds before updating the UI
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Sleep for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (jsonResponse != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            imageUrl = jsonObject.getString("url");
                            imageUrlText.setText(imageUrl);
                            viewImageButton.setVisibility(View.VISIBLE);
                            saveImageButton.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Snackbar.make(findViewById(R.id.drawer_layout), R.string.error_loading_image, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(findViewById(R.id.drawer_layout), R.string.error_loading_image, Snackbar.LENGTH_LONG).show();
                    }
                });
            }).start();
        }
    }

    /**
     * Opens the image in the default browser and saves the event in the history database.
     */
    private void viewImage() {
        // Get the current date and time for "date accessed"
        String dateAccessed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Save history when the user views the image (multiple entries allowed)
        boolean isInserted = historyDatabaseHelper.insertHistory(imageUrl, selectedDate, "History Entry", dateAccessed);
        if (isInserted) {
            Toast.makeText(this, R.string.history_saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.error_saving_history, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }

    /**
     * Saves the image to the database, ensuring it is not saved more than once.
     */
    private void saveImage() {
        boolean isInserted = imageDatabaseHelper.insertImage(imageUrl, selectedDate, "Sample Description");
        if (isInserted) {
            Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_DATE_KEY, selectedDate);
            editor.apply();
        } else {
            Toast.makeText(this, R.string.image_already_saved, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Provides the help message specific to this activity.
     *
     * @return A string containing the help message for MainActivity.
     */
    @Override
    protected String getHelpMessage() {
        return getString(R.string.help_message_main_activity);
    }
}
