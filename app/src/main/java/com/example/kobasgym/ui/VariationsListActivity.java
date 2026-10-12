package com.example.kobasgym.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kobasgym.R;
import com.example.kobasgym.adapter.VariationAdapter;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityVariationsListBinding;
import com.example.kobasgym.model.Exercise;
import com.example.kobasgym.model.ExerciseVariation;

import java.util.ArrayList;

public class VariationsListActivity extends BaseActivity implements VariationAdapter.OnVariationClickListener {

    private ActivityVariationsListBinding binding;
    private VariationAdapter adapter;
    private String routineId;
    private String exerciseId;
    private Exercise currentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVariationsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        routineId = getIntent().getStringExtra("ROUTINE_ID");
        exerciseId = getIntent().getStringExtra("EXERCISE_ID");

        setupToolbar();
        setupRecyclerView();

        binding.fabAddVariation.setOnClickListener(v -> {
            Intent intent = new Intent(VariationsListActivity.this, VariationDetailActivity.class);
            intent.putExtra("ROUTINE_ID", routineId);
            intent.putExtra("EXERCISE_ID", exerciseId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExerciseData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.variations_title);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        binding.rvVariations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VariationAdapter(this, new ArrayList<>(), this);
        binding.rvVariations.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                adapter.onItemMove(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                WorkoutDataManager.getInstance().reorderVariations(routineId, exerciseId, adapter.getVariations());
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.rvVariations);
        adapter.setOnStartDragListener(itemTouchHelper::startDrag);
    }

    private void loadExerciseData() {
        currentExercise = WorkoutDataManager.getInstance().getExerciseById(routineId, exerciseId);
        if (currentExercise == null) {
            finish();
            return;
        }

        binding.tvHeaderExerciseName.setText(currentExercise.getDisplayName(this));

        if (currentExercise.getImageResource() != 0) {
            binding.ivHeaderExerciseImage.setImageResource(currentExercise.getImageResource());
        } else {
            binding.ivHeaderExerciseImage.setImageResource(R.drawable.image_placeholder);
        }

        binding.fabAddVariation.setVisibility(View.VISIBLE);
        binding.tvEmptyVariations.setText(R.string.empty_variations);

        if (currentExercise.getVariations() == null || currentExercise.getVariations().isEmpty()) {
            binding.tvEmptyVariations.setVisibility(View.VISIBLE);
            binding.rvVariations.setVisibility(View.GONE);
            adapter.setVariations(new ArrayList<>());
        } else {
            binding.tvEmptyVariations.setVisibility(View.GONE);
            binding.rvVariations.setVisibility(View.VISIBLE);
            adapter.setVariations(currentExercise.getVariations());
        }
    }

    @Override
    public void onOpenVariation(ExerciseVariation variation) {
        onEditVariation(variation);
    }

    @Override
    public void onEditVariation(ExerciseVariation variation) {
        Intent intent = new Intent(this, VariationDetailActivity.class);
        intent.putExtra("ROUTINE_ID", routineId);
        intent.putExtra("EXERCISE_ID", exerciseId);
        intent.putExtra("VARIATION_ID", variation.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteVariation(ExerciseVariation variation) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_variation_title)
                .setMessage(getString(R.string.dialog_delete_variation_msg, variation.getDisplayName(this)))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    WorkoutDataManager.getInstance().deleteVariation(routineId, exerciseId, variation.getId());
                    loadExerciseData();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
