package com.bermer.rutabelica;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddLocation extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner categorySpinner;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button uploadButton;
    private Button getLocationButton;

    private DatabaseReference databaseReference;

    private FirebaseFirestore db;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locations);

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        categorySpinner = findViewById(R.id.spinnerCategory);
        cityEditText = findViewById(R.id.editTextCity);
        stateEditText = findViewById(R.id.editTextState);
        latitudeEditText = findViewById(R.id.editTextLatitude);
        longitudeEditText = findViewById(R.id.editTextLongitude);
        uploadButton = findViewById(R.id.buttonUpload);
        getLocationButton = findViewById(R.id.buttonUseLocation);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPlaceInfoToFirebase();
            }
        });
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitudeEditText.setText(String.valueOf(location.getLatitude()));
                            longitudeEditText.setText(String.valueOf(location.getLongitude()));
                            cityEditText.setText(getCityName(location.getLatitude(), location.getLongitude()));
                            stateEditText.setText(getStateName(location.getLatitude(), location.getLongitude()));

                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private String getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String cityName = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
                Log.d("Location get", "Ciudad: " + cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }
    private String getStateName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String stateName = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                stateName = addresses.get(0).getAdminArea();

                Log.d("Location get", "estado: " + stateName );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateName;
    }


    private void uploadPlaceInfoToFirebase() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();
        //Double latitude = Double.parseDouble(latitudeEditText.getText().toString());
        //Double longitude = Double.parseDouble(longitudeEditText.getText().toString());

        if (latitude.isEmpty() || longitude.isEmpty() || city.isEmpty() || state.isEmpty()){
            Toast.makeText(this, "Por favor presiona el botón de obtener ubicación", Toast.LENGTH_SHORT).show();
            return;
        }
        // Obtener el ID del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("UPLOAD_DEBUG", "Título: " + title);
        Log.d("UPLOAD_DEBUG", "Descripción: " + description);
        Log.d("UPLOAD_DEBUG", "Categoría: " + category);
        Log.d("UPLOAD_DEBUG", "Ciudad: " + city);
        Log.d("UPLOAD_DEBUG", "Estado: " + state);
        Log.d("UPLOAD_DEBUG", "Latitud: " + latitude);
        Log.d("UPLOAD_DEBUG", "Longitud: " + longitude);

        // Verificar que todos los campos estén completos
        if (title.isEmpty() || description.isEmpty() || category.isEmpty() ) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con la información del lugar
        Map<String, Object> placeInfo = new HashMap<>();
        placeInfo.put("titulo", title);
        placeInfo.put("descripcion", description);
        placeInfo.put("categoria", category);
        placeInfo.put("ciudad", city);
        placeInfo.put("estado", state);
        placeInfo.put("latitud", latitude);
        placeInfo.put("longitud", longitude);

        // Obtener referencia a la colección "lugares" en Firebase
        // Obtener una referencia a la colección "lugares" en Firestore
        CollectionReference lugaresRef = db.collection("lugares");

        // Subir los datos del lugar a Firestore
        lugaresRef.add(placeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UPLOAD_DEBUG", "Documento subido correctamente a Firestore con ID: " + documentReference.getId());
                        Toast.makeText(AddLocation.this, "Información del lugar subida exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent i = new Intent(AddLocation.this, ListaUbicaciones.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UPLOAD_DEBUG", "Error al subir datos a Firestore: " + e.getMessage());
                        Toast.makeText(AddLocation.this, "Error al subir información del lugar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}