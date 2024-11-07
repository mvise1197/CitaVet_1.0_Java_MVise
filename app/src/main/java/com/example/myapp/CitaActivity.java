package com.example.myapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CitaActivity extends AppCompatActivity {

    private EditText editTextName, editTextMotivo;
    private CalendarView calendarView;
    private Button buttonElegirFoto, buttonTomarFoto, buttonSave;
    private ImageView imageViewPhoto;
    private String selectedDate;
    private Uri imageUri;
    private Bitmap photoBitmap;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);

        editTextName = findViewById(R.id.editTextName);
        editTextMotivo = findViewById(R.id.editTextMotivo);
        calendarView = findViewById(R.id.calendarView);
        buttonElegirFoto = findViewById(R.id.buttonElegirFoto);
        buttonTomarFoto = findViewById(R.id.buttonTomarFoto);
        buttonSave = findViewById(R.id.buttonSave);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        buttonElegirFoto.setOnClickListener(v -> openFileChooser());
        buttonTomarFoto.setOnClickListener(v -> openCamera());

        buttonSave.setOnClickListener(v -> saveAppointment());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewPhoto.setImageURI(imageUri);
        }

        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            imageViewPhoto.setImageBitmap(photoBitmap);
        }
    }

    private void saveAppointment() {
        String name = editTextName.getText().toString();
        String reason = editTextMotivo.getText().toString();

        if (name.isEmpty() || reason.isEmpty() || selectedDate == null || (imageUri == null && photoBitmap == null)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image and get URL
        if (imageUri != null) {
            uploadImage(imageUri);
        } else if (photoBitmap != null) {
            uploadImage(photoBitmap);
        }
    }

    private void uploadImage(Uri uri) {
        StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(uri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(this::saveToFirestore));
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(this::saveToFirestore));
    }

    private void saveToFirestore(Uri downloadUri) {
        String name = editTextName.getText().toString();
        String reason = editTextMotivo.getText().toString();

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("name", name);
        appointment.put("reason", reason);
        appointment.put("date", selectedDate);
        appointment.put("photoUrl", downloadUri.toString());

        firestore.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment saved", Toast.LENGTH_SHORT).show();
                    finish();  // Return to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save appointment", Toast.LENGTH_SHORT).show();
                });
    }
}
