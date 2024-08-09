package com.example.nasaimagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.util.Log;

/**
 * BaseActivity is an abstract class that provides common functionality for activities
 * in the NASA Image Picker app. This class sets up the navigation drawer and toolbar,
 * and provides a standard way to display a help dialog.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * The DrawerLayout that hosts the navigation drawer.
     */
    protected DrawerLayout drawerLayout;

    /**
     * The NavigationView that displays the navigation menu.
     */
    protected NavigationView navigationView;

    /**
     * The ActionBarDrawerToggle that ties together the functionality of the DrawerLayout and Toolbar.
     */
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * Sets up the navigation and toolbar.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigation();
    }

    /**
     * Initialize the contents of the Activity's standard options menu. Adds the help menu item to the toolbar.
     *
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; if false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_menu, menu);
        return true;
    }

    /**
     * Handle item selection in the options menu. Shows the help dialog when the help menu item is selected.
     *
     * @param item The menu item that was selected.
     * @return true if the item was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a help dialog with information specific to the current activity.
     */
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(getHelpMessage())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Returns the help message specific to the activity that extends this BaseActivity.
     * This method must be implemented by subclasses to provide the appropriate help message.
     *
     * @return A string containing the help message for the activity.
     */
    protected abstract String getHelpMessage();

    /**
     * Sets up the navigation drawer and toolbar. This method initializes the toolbar and navigation drawer,
     * sets the action bar to use the toolbar, and configures the drawer toggle to synchronize the state of the drawer with the action bar.
     */
    protected void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        if (navigationView == null) {
            Log.e("BaseActivity", "NavigationView is null");
        } else {
            navigationView.setNavigationItemSelectedListener(menuItem -> {
                if (menuItem.getItemId() == R.id.nav_main) {
                    startActivity(new Intent(this, MainActivity.class));
                } else if (menuItem.getItemId() == R.id.nav_saved_images) {
                    startActivity(new Intent(this, SavedImagesActivity.class));
                } else if (menuItem.getItemId() == R.id.nav_history) {
                    startActivity(new Intent(this, HistoryActivity.class));
                } else if (menuItem.getItemId() == R.id.nav_mock_comment) {
                    startActivity(new Intent(this, MockCommentActivity.class));
                }
                drawerLayout.closeDrawers();
                return true;
            });

            drawerToggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.drawer_open, R.string.drawer_close
            );
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }
}
