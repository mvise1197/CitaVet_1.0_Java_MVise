package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btnlogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btnRegistroActivity = findViewById(R.id.btnregistrar);
        btnRegistroActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("Main Activity", "onRestart");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("Main Activity", "onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("Main Activity", "onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("Main Activity", "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Main Activity", "onStop");
    }
}
