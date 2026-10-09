package com.example.kobasgym.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import com.example.kobasgym.R;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityCalendarBinding;
import com.example.kobasgym.databinding.DialogLogWorkoutBinding;
import com.example.kobasgym.model.Routine;
import com.example.kobasgym.model.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends BaseActivity {

    private ActivityCalendarBinding binding;
    private String selectedDateStr; // Format YYYY-MM-DD
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        Calendar today = Calendar.getInstance();
        selectedDateStr = dateFormat.format(today.getTime());

        setupCalendarView();
        updateSelectedDayInfo();
        updateMonthlyStats(today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        binding.btnLogDay.setOnClickListener(v -> showLogWorkoutDialog());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.calendar_title);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCalendarView() {
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDateStr = dateFormat.format(cal.getTime());
            updateSelectedDayInfo();
            updateMonthlyStats(year, month);
        });
    }

    private void updateSelectedDayInfo() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(selectedDateStr));
            String displayDate = displayDateFormat.format(cal.getTime());
            if (!TextUtils.isEmpty(displayDate)) {
                displayDate = displayDate.substring(0, 1).toUpperCase() + displayDate.substring(1);
            }
            binding.tvSelectedDateText.setText(displayDate);
        } catch (Exception e) {
            binding.tvSelectedDateText.setText(selectedDateStr);
        }

        WorkoutLog log = WorkoutDataManager.getInstance().getWorkoutLog(selectedDateStr);
        if (log != null) {
            binding.llWorkoutInfo.setVisibility(View.VISIBLE);
            binding.tvNoWorkoutInfo.setVisibility(View.GONE);
            binding.tvLoggedRoutineName.setText(log.getRoutineName());

            if (!TextUtils.isEmpty(log.getNotes())) {
                binding.tvLoggedNotes.setVisibility(View.VISIBLE);
                binding.tvLoggedNotes.setText(log.getNotes());
            } else {
                binding.tvLoggedNotes.setVisibility(View.GONE);
            }
        } else {
            binding.llWorkoutInfo.setVisibility(View.GONE);
            binding.tvNoWorkoutInfo.setVisibility(View.VISIBLE);
        }
    }

    private void updateMonthlyStats(int year, int month) {
        Map<String, WorkoutLog> logs = WorkoutDataManager.getInstance().getAllWorkoutLogsMap();
        int monthlyCount = 0;
        String prefix = String.format(Locale.US, "%04d-%02d", year, month + 1);

        for (String date : logs.keySet()) {
            if (date.startsWith(prefix)) {
                monthlyCount++;
            }
        }

        binding.tvMonthlyStats.setText(getString(R.string.text_monthly_stats, monthlyCount));
    }

    private void showLogWorkoutDialog() {
        DialogLogWorkoutBinding dialogBinding = DialogLogWorkoutBinding.inflate(LayoutInflater.from(this));

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(selectedDateStr));
            String displayDate = displayDateFormat.format(cal.getTime());
            if (!TextUtils.isEmpty(displayDate)) {
                displayDate = displayDate.substring(0, 1).toUpperCase() + displayDate.substring(1);
            }
            dialogBinding.tvDialogDateTitle.setText(displayDate);
        } catch (Exception e) {
            dialogBinding.tvDialogDateTitle.setText(selectedDateStr);
        }

        List<Routine> routines = WorkoutDataManager.getInstance().getRoutines();
        List<String> routineNames = new ArrayList<>();
        for (Routine r : routines) {
            routineNames.add(r.getDisplayName(this));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routineNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerRoutines.setAdapter(adapter);

        WorkoutLog existingLog = WorkoutDataManager.getInstance().getWorkoutLog(selectedDateStr);
        if (existingLog != null) {
            dialogBinding.switchAttended.setChecked(true);
            dialogBinding.llRoutineDetails.setVisibility(View.VISIBLE);
            dialogBinding.btnDeleteLog.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(existingLog.getNotes())) {
                dialogBinding.etLogNotes.setText(existingLog.getNotes());
            }

            int index = -1;
            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i).getId().equals(existingLog.getRoutineId())
                        || routines.get(i).getDisplayName(this).equalsIgnoreCase(existingLog.getRoutineName())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                dialogBinding.spinnerRoutines.setSelection(index);
            }
        } else {
            dialogBinding.switchAttended.setChecked(false);
            dialogBinding.llRoutineDetails.setVisibility(View.GONE);
            dialogBinding.btnDeleteLog.setVisibility(View.GONE);
        }

        dialogBinding.switchAttended.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dialogBinding.llRoutineDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.calendar_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialogBinding.btnDeleteLog.setOnClickListener(v -> {
            WorkoutDataManager.getInstance().deleteWorkoutLog(selectedDateStr);
            updateSelectedDayInfo();
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(dateFormat.parse(selectedDateStr));
                updateMonthlyStats(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            } catch (Exception ignored) {}
            dialog.dismiss();
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (dialogBinding.switchAttended.isChecked()) {
                int selectedIndex = dialogBinding.spinnerRoutines.getSelectedItemPosition();
                String routineId = "";
                String routineName = "";
                if (selectedIndex >= 0 && selectedIndex < routines.size()) {
                    routineId = routines.get(selectedIndex).getId();
                    routineName = routines.get(selectedIndex).getDisplayName(this);
                } else if (!routineNames.isEmpty()) {
                    routineName = routineNames.get(0);
                }

                String notes = dialogBinding.etLogNotes.getText() != null ? dialogBinding.etLogNotes.getText().toString().trim() : "";

                WorkoutLog log = new WorkoutLog(
                        selectedDateStr,
                        routineId,
                        routineName,
                        notes,
                        System.currentTimeMillis()
                );
                WorkoutDataManager.getInstance().saveWorkoutLog(log);
            } else {
                WorkoutDataManager.getInstance().deleteWorkoutLog(selectedDateStr);
            }

            updateSelectedDayInfo();
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(selectedDateStr));
                updateMonthlyStats(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            } catch (Exception ignored) {}

            dialog.dismiss();
        });
    }
}