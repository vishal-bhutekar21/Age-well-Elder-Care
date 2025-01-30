package com.chaitany.agewell;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class AddAppointmentDialog extends DialogFragment {
    private AppointmentAddedListener listener;
    private EditText doctorInput, typeInput;
    private TextView dateText, timeText;
    private String selectedDate, selectedTime;

    public interface AppointmentAddedListener {
        void onAppointmentAdded(Appointment appointment);
    }

    public void setAppointmentAddedListener(AppointmentAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_appointment, null);

        // Initialize views
        doctorInput = view.findViewById(R.id.input_doctor);
        typeInput = view.findViewById(R.id.input_type);
        dateText = view.findViewById(R.id.text_date);
        timeText = view.findViewById(R.id.text_time);

        // Setup date picker
        dateText.setOnClickListener(v -> showDatePicker());
        timeText.setOnClickListener(v -> showTimePicker());

        builder.setView(view)
                .setTitle("Add New Appointment")
                .setPositiveButton("Add", (dialog, id) -> {
                    if (validateInputs()) {
                        Appointment appointment = new Appointment(
                                0,
                                selectedDate,
                                selectedTime,
                                doctorInput.getText().toString(),
                                typeInput.getText().toString()
                        );
                        listener.onAppointmentAdded(appointment);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                    dateText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    timeText.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private boolean validateInputs() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedTime == null || selectedTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (doctorInput.getText().toString().isEmpty()) {
            doctorInput.setError("Please enter doctor's name");
            return false;
        }
        if (typeInput.getText().toString().isEmpty()) {
            typeInput.setError("Please enter appointment type");
            return false;
        }
        return true;
    }
}