package com.example.kobasgym.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kobasgym.R;
import com.example.kobasgym.adapter.WorkoutLogAdapter;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityCalendarBinding;
import com.example.kobasgym.databinding.DialogLogWorkoutBinding;
import com.example.kobasgym.model.Routine;
import com.example.kobasgym.model.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends BaseActivity implements WorkoutLogAdapter.OnLogClickListener {

    private ActivityCalendarBinding binding;
    private WorkoutLogAdapter logAdapter;
    private String selectedDateStr; // Format YYYY-MM-DD
    private int currentYear;
    private int currentMonth; // 0-indexed
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        Calendar today = Calendar.getInstance();
        currentYear = today.get(Calendar.YEAR);
        currentMonth = today.get(Calendar.MONTH);
        selectedDateStr = formatDateStr(currentYear, currentMonth, today.get(Calendar.DAY_OF_MONTH));

        setupRecyclerView();
        setupCalendarView();
        refreshAllData();

        binding.btnLogDay.setOnClickListener(v -> showLogWorkoutDialog(selectedDateStr));
    }

    private String formatDateStr(int year, int monthZeroBased, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.calendar_title);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        binding.rvWorkoutLogs.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new WorkoutLogAdapter(this, new ArrayList<>(), this);
        binding.rvWorkoutLogs.setAdapter(logAdapter);
    }

    private void setupCalendarView() {
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            selectedDateStr = formatDateStr(year, month, dayOfMonth);
            updateSelectedDayInfo();
            updateMonthlyStats();
        });
    }

    private void refreshAllData() {
        updateSelectedDayInfo();
        updateMonthlyStats();
        loadAllWorkoutLogs();
    }

    private void updateSelectedDayInfo() {
        binding.tvSelectedDateText.setText(getDisplayDateText(selectedDateStr));

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

    private String getDisplayDateText(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]) - 1;
                int d = Integer.parseInt(parts[2]);
                Calendar cal = Calendar.getInstance();
                cal.set(y, m, d);
                String displayDate = displayDateFormat.format(cal.getTime());
                if (!TextUtils.isEmpty(displayDate)) {
                    return displayDate.substring(0, 1).toUpperCase() + displayDate.substring(1);
                }
            }
        } catch (Exception ignored) {}
        return dateStr;
    }

    private void updateMonthlyStats() {
        Map<String, WorkoutLog> logs = WorkoutDataManager.getInstance().getAllWorkoutLogsMap();
        int monthlyCount = 0;
        String prefix = String.format(Locale.US, "%04d-%02d", currentYear, currentMonth + 1);

        for (String date : logs.keySet()) {
            if (date.startsWith(prefix)) {
                monthlyCount++;
            }
        }

        binding.tvMonthlyStats.setText(getString(R.string.text_monthly_stats, monthlyCount));
    }

    private void loadAllWorkoutLogs() {
        Map<String, WorkoutLog> logsMap = WorkoutDataManager.getInstance().getAllWorkoutLogsMap();
        List<WorkoutLog> logList = new ArrayList<>(logsMap.values());

        // Sort descending by date
        Collections.sort(logList, (a, b) -> b.getDate().compareTo(a.getDate()));

        if (logList.isEmpty()) {
            binding.tvEmptyLogs.setVisibility(View.VISIBLE);
            binding.rvWorkoutLogs.setVisibility(View.GONE);
            logAdapter.setLogs(new ArrayList<>());
        } else {
            binding.tvEmptyLogs.setVisibility(View.GONE);
            binding.rvWorkoutLogs.setVisibility(View.VISIBLE);
            logAdapter.setLogs(logList);
        }
    }

    @Override
    public void onEditLog(WorkoutLog log) {
        if (log != null) {
            showLogWorkoutDialog(log.getDate());
        }
    }

    @Override
    public void onDeleteLog(WorkoutLog log) {
        if (log != null && log.getDate() != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.calendar_title)
                    .setMessage("¿Deseas eliminar el registro del " + getDisplayDateText(log.getDate()) + "?")
                    .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                        WorkoutDataManager.getInstance().deleteWorkoutLog(log.getDate());
                        refreshAllData();
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        }
    }

    private void showLogWorkoutDialog(String targetDateStr) {
        DialogLogWorkoutBinding dialogBinding = DialogLogWorkoutBinding.inflate(LayoutInflater.from(this));

        dialogBinding.tvDialogDateTitle.setText(getDisplayDateText(targetDateStr));

        List<Routine> routines = WorkoutDataManager.getInstance().getRoutines();
        List<String> routineNames = new ArrayList<>();
        if (routines != null && !routines.isEmpty()) {
            for (Routine r : routines) {
                routineNames.add(r.getDisplayName(this));
            }
        } else {
            routineNames.add("Entrenamiento General");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routineNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerRoutines.setAdapter(adapter);

        WorkoutLog existingLog = WorkoutDataManager.getInstance().getWorkoutLog(targetDateStr);
        if (existingLog != null) {
            dialogBinding.switchAttended.setChecked(true);
            dialogBinding.llRoutineDetails.setVisibility(View.VISIBLE);
            dialogBinding.btnDeleteLog.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(existingLog.getNotes())) {
                dialogBinding.etLogNotes.setText(existingLog.getNotes());
            }

            int index = -1;
            if (routines != null) {
                for (int i = 0; i < routines.size(); i++) {
                    if (routines.get(i).getId().equals(existingLog.getRoutineId())
                            || routines.get(i).getDisplayName(this).equalsIgnoreCase(existingLog.getRoutineName())) {
                        index = i;
                        break;
                    }
                }
            }
            if (index >= 0) {
                dialogBinding.spinnerRoutines.setSelection(index);
            }
        } else {
            dialogBinding.switchAttended.setChecked(true); // Default to ON when registering
            dialogBinding.llRoutineDetails.setVisibility(View.VISIBLE);
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
            WorkoutDataManager.getInstance().deleteWorkoutLog(targetDateStr);
            refreshAllData();
            dialog.dismiss();
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (dialogBinding.switchAttended.isChecked()) {
                int selectedIndex = dialogBinding.spinnerRoutines.getSelectedItemPosition();
                String routineId = "";
                String routineName = "";
                if (routines != null && selectedIndex >= 0 && selectedIndex < routines.size()) {
                    routineId = routines.get(selectedIndex).getId();
                    routineName = routines.get(selectedIndex).getDisplayName(this);
                } else if (!routineNames.isEmpty()) {
                    routineName = routineNames.get(0);
                }

                String notes = dialogBinding.etLogNotes.getText() != null ? dialogBinding.etLogNotes.getText().toString().trim() : "";

                WorkoutLog log = new WorkoutLog(
                        targetDateStr,
                        routineId,
                        routineName,
                        notes,
                        System.currentTimeMillis()
                );
                WorkoutDataManager.getInstance().saveWorkoutLog(log);
            } else {
                WorkoutDataManager.getInstance().deleteWorkoutLog(targetDateStr);
            }

            refreshAllData();
            dialog.dismiss();
        });
    }
}