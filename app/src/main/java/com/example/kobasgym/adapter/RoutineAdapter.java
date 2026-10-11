package com.example.kobasgym.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kobasgym.R;
import com.example.kobasgym.databinding.ItemRoutineBinding;
import com.example.kobasgym.model.Routine;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    public interface OnRoutineClickListener {
        void onOpenRoutine(Routine routine);
        void onEditRoutine(Routine routine);
        void onDeleteRoutine(Routine routine);
    }

    private final Context context;
    private List<Routine> routines;
    private final OnRoutineClickListener listener;

    public RoutineAdapter(Context context, List<Routine> routines, OnRoutineClickListener listener) {
        this.context = context;
        this.routines = routines;
        this.listener = listener;
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoutineBinding binding = ItemRoutineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RoutineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        holder.bind(routines.get(position), context, listener);
    }

    @Override
    public int getItemCount() {
        return routines != null ? routines.size() : 0;
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoutineBinding binding;

        public RoutineViewHolder(ItemRoutineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Routine routine, Context context, OnRoutineClickListener listener) {
            binding.tvRoutineName.setText(routine.getDisplayName(context));

            String desc = routine.getDisplayDescription(context);
            String abbrev = routine.getAbbreviation();
            String fullDesc = "";
            if (!TextUtils.isEmpty(abbrev)) {
                fullDesc = "[" + abbrev + "] ";
            }
            if (!TextUtils.isEmpty(desc)) {
                fullDesc += desc;
            }

            if (TextUtils.isEmpty(fullDesc)) {
                binding.tvRoutineDescription.setVisibility(View.GONE);
            } else {
                binding.tvRoutineDescription.setVisibility(View.VISIBLE);
                binding.tvRoutineDescription.setText(fullDesc);
            }

            int exCount = routine.getExercises() != null ? routine.getExercises().size() : 0;
            binding.tvExercisesCount.setText(context.getString(R.string.exercises_count_format, exCount));

            if (routine.getImageResource() != 0 && routine.getImageResource() != R.drawable.ic_dumbbell) {
                binding.ivRoutineIcon.setImageResource(routine.getImageResource());
            } else {
                binding.ivRoutineIcon.setImageResource(R.drawable.red_devil_mad);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onOpenRoutine(routine);
            });

            binding.btnEditRoutine.setOnClickListener(v -> {
                if (listener != null) listener.onEditRoutine(routine);
            });
        }
    }
}