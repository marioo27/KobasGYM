package com.example.kobasgym.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kobasgym.R;
import com.example.kobasgym.databinding.ItemCalendarDayBinding;
import com.example.kobasgym.model.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarGridAdapter extends RecyclerView.Adapter<CalendarGridAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day);
    }

    private final Context context;
    private List<CalendarDay> days;
    private final OnDayClickListener listener;

    public CalendarGridAdapter(Context context, List<CalendarDay> days, OnDayClickListener listener) {
        this.context = context;
        this.days = days;
        this.listener = listener;
    }

    public void setDays(List<CalendarDay> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarDayBinding binding = ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DayViewHolder(binding);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(days.get(position), context, listener);
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        private final ItemCalendarDayBinding binding;

        public DayViewHolder(ItemCalendarDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CalendarDay day, Context context, OnDayClickListener listener) {
            if (day.isTrained() && day.getAbbreviation() != null && !day.getAbbreviation().isEmpty()) {
                binding.tvDayNumber.setText(day.getAbbreviation());
                binding.tvDayNumber.setTextSize(11);
            } else {
                binding.tvDayNumber.setText(String.valueOf(day.getDay()));
                binding.tvDayNumber.setTextSize(15);
            }

            String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
            boolean isToday = day.getDateStr().equals(todayStr);
            boolean isFuture = day.getDateStr().compareTo(todayStr) > 0;

            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);

            if (day.isTrained()) {
                // Orange highlight for trained days with abbreviation
                background.setColor(Color.parseColor("#FF9800"));
                binding.tvDayNumber.setTextColor(Color.WHITE);
                binding.tvDayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
                if (isToday) {
                    // Today is trained: render blue border stroke on top of the orange background
                    int strokeWidth = (int) (3 * context.getResources().getDisplayMetrics().density);
                    background.setStroke(strokeWidth, ContextCompat.getColor(context, R.color.blue_primary));
                }
            } else if (isToday) {
                // Unfilled blue circle border (stroke only) for current day
                background.setColor(Color.TRANSPARENT);
                int strokeWidth = (int) (2 * context.getResources().getDisplayMetrics().density);
                background.setStroke(strokeWidth, ContextCompat.getColor(context, R.color.blue_primary));
                binding.tvDayNumber.setTextColor(Color.WHITE);
                binding.tvDayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                background.setColor(Color.TRANSPARENT);
                if (isFuture) {
                    // Future days that haven't arrived yet: lighter grayish
                    binding.tvDayNumber.setTextColor(Color.parseColor("#78909C"));
                    binding.tvDayNumber.setTypeface(null, android.graphics.Typeface.NORMAL);
                } else if (day.isCurrentMonth()) {
                    // Past/present days without training: white
                    binding.tvDayNumber.setTextColor(Color.WHITE);
                    binding.tvDayNumber.setTypeface(null, android.graphics.Typeface.NORMAL);
                } else {
                    // Prev/next month padding days: muted gray
                    binding.tvDayNumber.setTextColor(Color.parseColor("#546E7A"));
                    binding.tvDayNumber.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
            }

            binding.tvDayNumber.setBackground(background);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onDayClick(day);
            });
        }
    }
}