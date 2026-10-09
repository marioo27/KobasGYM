package com.example.kobasgym.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kobasgym.R;
import com.example.kobasgym.databinding.ItemExerciseBinding;
import com.example.kobasgym.model.Exercise;
import com.example.kobasgym.model.ExerciseVariation;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    public interface OnExerciseClickListener {
        void onOpenExercise(Exercise exercise);
        void onEditExercise(Exercise exercise);
        void onDeleteExercise(Exercise exercise);
    }

    private final Context context;
    private List<Exercise> exercises;
    private final OnExerciseClickListener listener;

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseClickListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBinding binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position), context, listener);
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseBinding binding;

        public ExerciseViewHolder(ItemExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Exercise exercise, Context context, OnExerciseClickListener listener) {
            binding.tvExerciseName.setText(exercise.getDisplayName(context));

            if (exercise.isExtra()) {
                binding.tvExtraBadge.setVisibility(View.VISIBLE);
            } else {
                binding.tvExtraBadge.setVisibility(View.GONE);
            }

            int varCount = exercise.getVariations() != null ? exercise.getVariations().size() : 0;
            binding.tvVariationsCount.setText(context.getString(R.string.variations_count_format, varCount));

            if (exercise.getVariations() != null && !exercise.getVariations().isEmpty()) {
                ExerciseVariation firstVar = exercise.getVariations().get(0);
                String summary = context.getString(R.string.sets_reps_format, firstVar.getSets(), firstVar.getRepetitions())
                        + " (" + firstVar.getDisplayWeights() + " " + firstVar.getWeightUnit() + ")";
                binding.tvSetsRepsSummary.setText(summary);
                binding.tvSetsRepsSummary.setVisibility(View.VISIBLE);
            } else {
                binding.tvSetsRepsSummary.setVisibility(View.GONE);
            }

            if (exercise.getImageResource() != 0) {
                binding.ivExerciseImage.setImageResource(exercise.getImageResource());
            } else {
                binding.ivExerciseImage.setImageResource(R.drawable.image_placeholder);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onOpenExercise(exercise);
            });

            binding.btnEditExercise.setOnClickListener(v -> {
                if (listener != null) listener.onEditExercise(exercise);
            });
        }
    }
}