package com.example.kobasgym.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kobasgym.R;
import com.example.kobasgym.adapter.CalendarGridAdapter;
import com.example.kobasgym.adapter.WorkoutLogAdapter;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityCalendarBinding;
import com.example.kobasgym.databinding.DialogLogWorkoutBinding;
import com.example.kobasgym.model.CalendarDay;
import com.example.kobasgym.model.Routine;
import com.example.kobasgym.model.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends BaseActivity implements WorkoutLogAdapter.OnLogClickListener, CalendarGridAdapter.OnDayClickListener {

    private ActivityCalendarBinding binding;
    private WorkoutLogAdapter logAdapter;
    private CalendarGridAdapter calendarGridAdapter;
    private String selectedDateStr; // Format YYYY-MM-DD
    private int currentYear;
    private int currentMonth; // 0-indexed
    private Map<String, WorkoutLog> logsMap = new HashMap<>();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));

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

        setupRecyclerViews();
        setupMonthNavigation();
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

    private void setupRecyclerViews() {
        // Calendar Grid (7 columns, starts on Monday)
        binding.rvCalendarGrid.setLayoutManager(new GridLayoutManager(this, 7));
        calendarGridAdapter = new CalendarGridAdapter(this, new ArrayList<>(), this);
        binding.rvCalendarGrid.setAdapter(calendarGridAdapter);

        // Workout Logs List
        binding.rvWorkoutLogs.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new WorkoutLogAdapter(this, new ArrayList<>(), this);
        binding.rvWorkoutLogs.setAdapter(logAdapter);
    }

    private void setupMonthNavigation() {
        binding.btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            refreshAllData();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            refreshAllData();
        });
    }

    private void refreshAllData() {
        logsMap = WorkoutDataManager.getInstance().getAllWorkoutLogsMap();
        updateMonthTitle();
        generateCalendarGridDays();
        updateSelectedDayInfo();
        updateMonthlyStats();
        loadAllWorkoutLogs();
    }

    private void updateMonthTitle() {
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        String title = monthYearFormat.format(cal.getTime());
        if (!TextUtils.isEmpty(title)) {
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }
        binding.tvMonthYearTitle.setText(title);
    }

    private String getAbbreviationForLog(WorkoutLog log) {
        if (log == null) return "";
        Routine r = WorkoutDataManager.getInstance().getRoutineById(log.getRoutineId());
        if (r != null) {
            return r.getAbbreviation();
        }
        String name = log.getRoutineName();
        if (name == null) return "EX";
        if ("Tren Superior".equalsIgnoreCase(name) || "rutina_tren_superior".equals(log.getRoutineId())) return "SUP";
        if ("Tren Inferior".equalsIgnoreCase(name) || "rutina_tren_inferior".equals(log.getRoutineId())) return "INF";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() >= 3) break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "EX";
    }

    private void generateCalendarGridDays() {
        List<CalendarDay> dayList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);

        // Day of week for 1st of month: Sunday=1, Monday=2 ... Saturday=7
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Offset from Monday (Monday = 0, Tuesday = 1, ... Sunday = 6)
        int offset = (firstDayOfWeek + 5) % 7;

        // Previous month trailing days
        Calendar prevCal = (Calendar) cal.clone();
        prevCal.add(Calendar.MONTH, -1);
        int maxDayPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int prevMonth = prevCal.get(Calendar.MONTH);
        int prevYear = prevCal.get(Calendar.YEAR);

        for (int i = offset - 1; i >= 0; i--) {
            int d = maxDayPrevMonth - i;
            String dateStr = formatDateStr(prevYear, prevMonth, d);
            WorkoutLog log = logsMap.get(dateStr);
            boolean isTrained = (log != null);
            String abbrev = getAbbreviationForLog(log);
            boolean isSelected = dateStr.equals(selectedDateStr);
            dayList.add(new CalendarDay(prevYear, prevMonth, d, false, dateStr, isTrained, isSelected, abbrev));
        }

        // Current month days
        int maxDayCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int d = 1; d <= maxDayCurrentMonth; d++) {
            String dateStr = formatDateStr(currentYear, currentMonth, d);
            WorkoutLog log = logsMap.get(dateStr);
            boolean isTrained = (log != null);
            String abbrev = getAbbreviationForLog(log);
            boolean isSelected = dateStr.equals(selectedDateStr);
            dayList.add(new CalendarDay(currentYear, currentMonth, d, true, dateStr, isTrained, isSelected, abbrev));
        }

        // Next month leading days to complete grid rows (multiple of 7)
        int remainder = dayList.size() % 7;
        if (remainder != 0) {
            int pad = 7 - remainder;
            Calendar nextCal = (Calendar) cal.clone();
            nextCal.add(Calendar.MONTH, 1);
            int nextMonth = nextCal.get(Calendar.MONTH);
            int nextYear = nextCal.get(Calendar.YEAR);
            for (int d = 1; d <= pad; d++) {
                String dateStr = formatDateStr(nextYear, nextMonth, d);
                WorkoutLog log = logsMap.get(dateStr);
                boolean isTrained = (log != null);
                String abbrev = getAbbreviationForLog(log);
                boolean isSelected = dateStr.equals(selectedDateStr);
                dayList.add(new CalendarDay(nextYear, nextMonth, d, false, dateStr, isTrained, isSelected, abbrev));
            }
        }

        calendarGridAdapter.setDays(dayList);
    }

    @Override
    public void onDayClick(CalendarDay day) {
        selectedDateStr = day.getDateStr();
        currentYear = day.getYear();
        currentMonth = day.getMonth();
        updateMonthTitle();
        generateCalendarGridDays();
        updateSelectedDayInfo();
    }

    private void updateSelectedDayInfo() {
        binding.tvSelectedDateText.setText(getDisplayDateText(selectedDateStr));

        WorkoutLog log = logsMap.get(selectedDateStr);
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
            binding.btnLogDay.setText(R.string.btn_edit_workout);
        } else {
            binding.llWorkoutInfo.setVisibility(View.GONE);
            binding.tvNoWorkoutInfo.setVisibility(View.VISIBLE);
            binding.btnLogDay.setText(R.string.btn_log_workout);
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
        int monthlyCount = 0;
        int courseCount = 0;
        String monthPrefix = String.format(Locale.US, "%04d-%02d", currentYear, currentMonth + 1);

        int courseStartYear = currentMonth >= Calendar.SEPTEMBER ? currentYear : currentYear - 1;
        String courseStartDateStr = String.format(Locale.US, "%04d-09-01", courseStartYear);

        for (String date : logsMap.keySet()) {
            if (date.startsWith(monthPrefix)) {
                monthlyCount++;
            }
            if (date.compareTo(courseStartDateStr) >= 0) {
                courseCount++;
            }
        }

        String statsText = getString(R.string.text_monthly_stats, monthlyCount)
                + "\n" + getString(R.string.text_course_stats, courseCount);
        binding.tvMonthlyStats.setText(statsText);
    }

    private void loadAllWorkoutLogs() {
        List<WorkoutLog> logList = new ArrayList<>();
        if (logsMap != null) {
            for (WorkoutLog l : logsMap.values()) {
                if (l != null && l.getDate() != null) {
                    logList.add(l);
                }
            }
        }

        // Sort descending by date safely
        logList.sort((a, b) -> b.getDate().compareTo(a.getDate()));

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
        try {
            String[] parts = targetDateStr.split("-");
            if (parts.length == 3) {
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]) - 1;
                int d = Integer.parseInt(parts[2]);
                Calendar targetCal = Calendar.getInstance();
                targetCal.set(y, m, d, 0, 0, 0);
                targetCal.set(Calendar.MILLISECOND, 0);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (targetCal.after(today)) {
                    Toast.makeText(this, R.string.error_future_date, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (Exception ignored) {}

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
                .setPositiveButton(R.string.btn_save, (dialogInterface, which) -> {
                    try {
                        if (dialogBinding.switchAttended.isChecked()) {
                            int selectedIndex = dialogBinding.spinnerRoutines.getSelectedItemPosition();
                            String routineId = "";
                            String routineName = "";
                            if (routines != null && selectedIndex >= 0 && selectedIndex < routines.size()) {
                                routineId = routines.get(selectedIndex).getId();
                                routineName = routines.get(selectedIndex).getDisplayName(CalendarActivity.this);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refreshAllData();
                })
                .setNegativeButton(R.string.btn_cancel, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialogBinding.btnDeleteLog.setOnClickListener(v -> {
            WorkoutDataManager.getInstance().deleteWorkoutLog(targetDateStr);
            refreshAllData();
            dialog.dismiss();
        });
    }
}