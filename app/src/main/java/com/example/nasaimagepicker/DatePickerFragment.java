package com.example.nasaimagepicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * DatePickerFragment is a DialogFragment that displays a DatePicker dialog.
 * It allows the user to pick a date and passes the selected date back to the calling activity or fragment
 * through the OnDateSelectedListener interface.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * Listener for date selection events.
     */
    private OnDateSelectedListener listener;

    /**
     * Called to instantiate the dialog when the fragment is created.
     * Sets the current date as the default date and disables future dates.
     *
     * @param savedInstanceState If the dialog is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * @return A new instance of DatePickerDialog with the current date set as default and future dates disabled.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        // Disable future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return datePickerDialog;
    }

    /**
     * Called when the user has selected a date. This method is invoked by the DatePickerDialog.
     * Passes the selected date to the registered OnDateSelectedListener.
     *
     * @param view  The view associated with this listener.
     * @param year  The year that was selected.
     * @param month The month that was selected (0-11 for compatibility with Calendar).
     * @param day   The day of the month that was selected.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (listener != null) {
            listener.onDateSelected(year + "-" + (month + 1) + "-" + day);
        }
    }

    /**
     * Registers a listener to be notified when the user selects a date.
     *
     * @param listener The listener to register.
     */
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when a date is selected.
     */
    public interface OnDateSelectedListener {
        /**
         * Called when a date is selected.
         *
         * @param date The selected date in the format "yyyy-MM-dd".
         */
        void onDateSelected(String date);
    }
}
