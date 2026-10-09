package com.example.kobasgym.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.kobasgym.R;
import com.example.kobasgym.adapter.CalendarGridAdapter;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityMainOptionsBinding;
import com.example.kobasgym.databinding.DialogCreateRoutineBinding;
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
import java.util.UUID;

public class MainOptionsActivity extends BaseActivity implements CalendarGridAdapter.OnDayClickListener {

    private ActivityMainOptionsBinding binding;
    private CalendarGridAdapter calendarGridAdapter;
    private String selectedDateStr; // Format YYYY-MM-DD
    private int currentYear;
    private int currentMonth; // 0-indexed
    private Map<String, WorkoutLog> logsMap = new HashMap<>();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        Calendar today = Calendar.getInstance();
        currentYear = today.get(Calendar.YEAR);
        currentMonth = today.get(Calendar.MONTH);
        selectedDateStr = formatDateStr(currentYear, currentMonth, today.get(Calendar.DAY_OF_MONTH));

        setupCalendarRecyclerView();
        setupMonthNavigation();

        binding.cardSeeRoutines.setOnClickListener(v -> {
            Intent intent = new Intent(MainOptionsActivity.this, RoutinesListActivity.class);
            startActivity(intent);
        });

        binding.cardStartNew.setOnClickListener(v -> showCreateRoutineDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllData();
    }

    private String formatDateStr(int year, int monthZeroBased, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCalendarRecyclerView() {
        binding.rvCalendarGrid.setLayoutManager(new GridLayoutManager(this, 7));
        calendarGridAdapter = new CalendarGridAdapter(this, new ArrayList<>(), this);
        binding.rvCalendarGrid.setAdapter(calendarGridAdapter);
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
        updateCounters();
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
        if ("Tren Superior".equalsIgnoreCase(name) || "rutina_tren_superior".equals(log.getRoutineId())) return "TS";
        if ("Tren Inferior".equalsIgnoreCase(name) || "rutina_tren_inferior".equals(log.getRoutineId())) return "TI";
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

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDayOfWeek + 5) % 7; // Monday = 0

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

        int maxDayCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int d = 1; d <= maxDayCurrentMonth; d++) {
            String dateStr = formatDateStr(currentYear, currentMonth, d);
            WorkoutLog log = logsMap.get(dateStr);
            boolean isTrained = (log != null);
            String abbrev = getAbbreviationForLog(log);
            boolean isSelected = dateStr.equals(selectedDateStr);
            dayList.add(new CalendarDay(currentYear, currentMonth, d, true, dateStr, isTrained, isSelected, abbrev));
        }

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
        showLogWorkoutDialog(selectedDateStr);
    }

    private void updateCounters() {
        // 1. This week count (Monday to Sunday)
        int thisWeekCount = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysSinceMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : (dayOfWeek - Calendar.MONDAY);
        cal.add(Calendar.DATE, -daysSinceMonday);
        long mondayMillis = cal.getTimeInMillis();

        cal.add(Calendar.DATE, 7);
        long nextMondayMillis = cal.getTimeInMillis();

        for (String dateStr : logsMap.keySet()) {
            try {
                java.util.Date d = dateFormat.parse(dateStr);
                if (d != null) {
                    long t = d.getTime();
                    if (t >= mondayMillis && t < nextMondayMillis) {
                        thisWeekCount++;
                    }
                }
            } catch (Exception ignored) {}
        }

        // 2. Course count (since September 1st)
        int courseCount = 0;
        int courseStartYear = currentMonth >= Calendar.SEPTEMBER ? currentYear : currentYear - 1;
        String courseStartDateStr = String.format(Locale.US, "%04d-09-01", courseStartYear);

        for (String date : logsMap.keySet()) {
            if (date.compareTo(courseStartDateStr) >= 0) {
                courseCount++;
            }
        }

        binding.tvThisWeekCount.setText(thisWeekCount + " días");
        binding.tvCourseCount.setText(courseCount + " días");
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
            dialogBinding.switchAttended.setChecked(true);
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
                                routineName = routines.get(selectedIndex).getDisplayName(MainOptionsActivity.this);
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
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE));
        }

        dialogBinding.btnDeleteLog.setOnClickListener(v -> {
            WorkoutDataManager.getInstance().deleteWorkoutLog(targetDateStr);
            refreshAllData();
            dialog.dismiss();
        });
    }

    private void showCreateRoutineDialog() {
        DialogCreateRoutineBinding dialogBinding = DialogCreateRoutineBinding.inflate(LayoutInflater.from(this));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_create_routine_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_create, null) // Set null to handle validation override
                .setNegativeButton(R.string.btn_cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String name = dialogBinding.etRoutineName.getText() != null ? dialogBinding.etRoutineName.getText().toString().trim() : "";
                    String desc = dialogBinding.etRoutineDescription.getText() != null ? dialogBinding.etRoutineDescription.getText().toString().trim() : "";
                    String abbrev = dialogBinding.etRoutineAbbreviation.getText() != null ? dialogBinding.etRoutineAbbreviation.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(name)) {
                        dialogBinding.tilRoutineName.setError(getString(R.string.error_empty_routine_name));
                        return;
                    } else {
                        dialogBinding.tilRoutineName.setError(null);
                    }

                    Routine newRoutine = new Routine(
                            UUID.randomUUID().toString(),
                            name, 0,
                            desc, 0,
                            "", 0,
                            R.drawable.ic_dumbbell,
                            abbrev,
                            new ArrayList<>()
                    );

                    WorkoutDataManager.getInstance().addRoutine(newRoutine);
                    dialog.dismiss();

                    Intent intent = new Intent(MainOptionsActivity.this, RoutineDetailActivity.class);
                    intent.putExtra("ROUTINE_ID", newRoutine.getId());
                    startActivity(intent);
                });
            }
        });

        dialog.show();
    }
}