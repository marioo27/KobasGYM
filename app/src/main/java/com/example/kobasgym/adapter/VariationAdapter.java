package com.example.kobasgym.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kobasgym.R;
import com.example.kobasgym.databinding.ItemVariationBinding;
import com.example.kobasgym.model.ExerciseVariation;

import java.util.List;

public class VariationAdapter extends RecyclerView.Adapter<VariationAdapter.VariationViewHolder> {

    public interface OnVariationClickListener {
        void onOpenVariation(ExerciseVariation variation);
        void onEditVariation(ExerciseVariation variation);
        void onDeleteVariation(ExerciseVariation variation);
    }

    private final Context context;
    private List<ExerciseVariation> variations;
    private final OnVariationClickListener listener;

    public VariationAdapter(Context context, List<ExerciseVariation> variations, OnVariationClickListener listener) {
        this.context = context;
        this.variations = variations;
        this.listener = listener;
    }

    public void setVariations(List<ExerciseVariation> variations) {
        this.variations = variations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VariationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVariationBinding binding = ItemVariationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VariationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VariationViewHolder holder, int position) {
        holder.bind(variations.get(position), context, listener);
    }

    @Override
    public int getItemCount() {
        return variations != null ? variations.size() : 0;
    }

    public static class VariationViewHolder extends RecyclerView.ViewHolder {
        private final ItemVariationBinding binding;

        public VariationViewHolder(ItemVariationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExerciseVariation variation, Context context, OnVariationClickListener listener) {
            binding.tvVariationName.setText(variation.getDisplayName(context));

            String setsRepsWeight = context.getString(R.string.sets_reps_format, variation.getSets(), variation.getRepetitions())
                    + " • " + variation.getDisplayWeights() + " " + variation.getWeightUnit();
            binding.tvSetsRepsWeight.setText(setsRepsWeight);

            String notes = variation.getDisplayNotes(context);
            if (TextUtils.isEmpty(notes)) {
                binding.tvVariationNotes.setVisibility(View.GONE);
            } else {
                binding.tvVariationNotes.setVisibility(View.VISIBLE);
                binding.tvVariationNotes.setText(notes);
            }

            if (variation.getImageResource() != 0) {
                binding.ivVariationImage.setImageResource(variation.getImageResource());
            } else {
                binding.ivVariationImage.setImageResource(R.drawable.image_placeholder);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onOpenVariation(variation);
            });

            binding.btnEditVariation.setOnClickListener(v -> {
                if (listener != null) listener.onEditVariation(variation);
            });
        }
    }
}