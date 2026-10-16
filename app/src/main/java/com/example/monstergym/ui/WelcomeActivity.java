package com.example.monstergym.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.monstergym.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends BaseActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainOptionsActivity.class);
            startActivity(intent);
        });
    }
}