package com.puj.taller02_permission.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.puj.taller02_permission.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.botonCamara.setOnClickListener(view -> startActivity(new Intent(this, CamaraActivity.class)));
        binding.botonContactos.setOnClickListener(view -> startActivity(new Intent(this, ContactosActivity.class)));
        binding.botonMapa.setOnClickListener(view -> startActivity(new Intent(this, MapsActivity.class)));

    }
}