package com.example.kobasgym.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.kobasgym.R;
import com.example.kobasgym.data.WorkoutDataManager;
import com.example.kobasgym.databinding.ActivityMainOptionsBinding;
import com.example.kobasgym.databinding.DialogCreateRoutineBinding;
import com.example.kobasgym.model.Routine;

import java.util.ArrayList;
import java.util.UUID;

public class MainOptionsActivity extends BaseActivity {

    private ActivityMainOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        binding.cardSeeRoutines.setOnClickListener(v -> {
            Intent intent = new Intent(MainOptionsActivity.this, RoutinesListActivity.class);
            startActivity(intent);
        });

        binding.cardStartNew.setOnClickListener(v -> showCreateRoutineDialog());

        binding.cardCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainOptionsActivity.this, CalendarActivity.class);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void showCreateRoutineDialog() {
        DialogCreateRoutineBinding dialogBinding = DialogCreateRoutineBinding.inflate(LayoutInflater.from(this));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_create_routine_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_create, null) // Set null to handle validation override
                .setNegativeButton(R.string.btn_cancel, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = dialogBinding.etRoutineName.getText() != null ? dialogBinding.etRoutineName.getText().toString().trim() : "";
            String desc = dialogBinding.etRoutineDescription.getText() != null ? dialogBinding.etRoutineDescription.getText().toString().trim() : "";

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
                    new ArrayList<>()
            );

            WorkoutDataManager.getInstance().addRoutine(newRoutine);
            dialog.dismiss();

            Intent intent = new Intent(MainOptionsActivity.this, RoutineDetailActivity.class);
            intent.putExtra("ROUTINE_ID", newRoutine.getId());
            startActivity(intent);
        });
    }
}