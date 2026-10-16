package com.example.monstergym.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monstergym.databinding.ItemWorkoutLogBinding;
import com.example.monstergym.model.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WorkoutLogAdapter extends RecyclerView.Adapter<WorkoutLogAdapter.LogViewHolder> {

    public interface OnLogClickListener {
        void onEditLog(WorkoutLog log);
        void onDeleteLog(WorkoutLog log);
    }

    private final Context context;
    private List<WorkoutLog> logs;
    private final OnLogClickListener listener;
    private final SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displaySdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    public WorkoutLogAdapter(Context context, List<WorkoutLog> logs, OnLogClickListener listener) {
        this.context = context;
        this.logs = logs;
        this.listener = listener;
    }

    public void setLogs(List<WorkoutLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutLogBinding binding = ItemWorkoutLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(logs.get(position), context, listener, parseSdf, displaySdf);
    }

    @Override
    public int getItemCount() {
        return logs != null ? logs.size() : 0;
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutLogBinding binding;

        public LogViewHolder(ItemWorkoutLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutLog log, Context context, OnLogClickListener listener,
                         SimpleDateFormat parseSdf, SimpleDateFormat displaySdf) {
            String formattedDate = log.getDate();
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(parseSdf.parse(log.getDate()));
                String text = displaySdf.format(cal.getTime());
                if (!TextUtils.isEmpty(text)) {
                    formattedDate = text.substring(0, 1).toUpperCase() + text.substring(1);
                }
            } catch (Exception ignored) {}

            binding.tvLogDate.setText(formattedDate);
            binding.tvLogRoutine.setText(log.getRoutineName());

            if (!TextUtils.isEmpty(log.getNotes())) {
                binding.tvLogNotes.setVisibility(View.VISIBLE);
                binding.tvLogNotes.setText(log.getNotes());
            } else {
                binding.tvLogNotes.setVisibility(View.GONE);
            }

            binding.btnEditLog.setOnClickListener(v -> {
                if (listener != null) listener.onEditLog(log);
            });

            binding.btnDeleteLogItem.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteLog(log);
            });
        }
    }
}