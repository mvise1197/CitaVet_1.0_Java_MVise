package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class InicioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton fab, fabNewRecord, fabViewRecords, fabSignOut;
    private View fabOptionsLayout;
    private boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar los FloatingActionButtons
        fab = findViewById(R.id.fab);
        fabNewRecord = findViewById(R.id.fabNewRecord);
        fabViewRecords = findViewById(R.id.fabViewRecords);
        fabSignOut = findViewById(R.id.fabSignOut);
        fabOptionsLayout = findViewById(R.id.fabOptionsLayout);

        // Configurar el botón principal para mostrar/ocultar el menú
        fab.setOnClickListener(v -> toggleFabMenu());

        // Configurar los botones del menú
        fabNewRecord.setOnClickListener(v -> {
            toggleFabMenu();
            Intent intent = new Intent(InicioActivity.this, CitaActivity.class);
            startActivity(intent);
        });

        fabViewRecords.setOnClickListener(v -> {
            toggleFabMenu();
            Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        fabSignOut.setOnClickListener(v -> {
            toggleFabMenu();
            mAuth.signOut();
            Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Método para mostrar/ocultar el menú
    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            fabOptionsLayout.setVisibility(View.GONE);
            isFabMenuOpen = false;
        } else {
            fabOptionsLayout.setVisibility(View.VISIBLE);
            isFabMenuOpen = true;
        }
    }
}
