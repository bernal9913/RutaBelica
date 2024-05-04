package com.bermer.rutabelica;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListaUbicaciones extends AppCompatActivity {

    private ArrayList<LatLng> ubicaciones = new ArrayList<>();
    private ArrayList<String> titulos = new ArrayList<>();
    private FirebaseFirestore firestore;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView textViewCity;
    private EditText editTextCity;
    private Button buttonSearch;
    private Button buttonViewRoute;
    private Spinner spinnerCategory;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private static final int DETALLE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ubicaciones);


        textViewCity = findViewById(R.id.textViewCity);
        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerView);
        buttonViewRoute = findViewById(R.id.buttonViewRoute);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();
                if (!city.isEmpty()) {
                    fetchCity(city, category);
                }
            }
        });
        buttonViewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ubicaciones.size() < 2){
                    Toast.makeText(ListaUbicaciones.this , "Selecciona al menos dos ubicaciones", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(ListaUbicaciones.this, rutaActivity.class);
                    intent.putExtra("ubicaciones", ubicaciones);
                    intent.putExtra("titulos", titulos);
                    startActivity(intent);

                }
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }
    private void fetchDataFromFirestore(String city, String category){
        Log.e("FetchDataFromFirestore", "Ciudad: " + city);
        //city = city.toUpperCase();
        String cityCapitalized = capitalize(city);
        System.out.println("City: " + cityCapitalized);
        //city = "test";
        if (category.equals("Todas las categorias")){
            firestore.collection("lugares")
                    .whereEqualTo("ciudad", cityCapitalized)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dataList = new ArrayList<>();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                dataList.add(snapshot);
                            }
                            adapter = new MyAdapter(dataList);
                            recyclerView.setAdapter(adapter);
                            updateRecyclerView(dataList);

                            adapter.setOnItemClickListener(new MyAdapter.onItemClickListener() {
                                @Override
                                public void onItemClick(DocumentSnapshot document) {
                                    // Crear un Intent para abrir la actividad DetalleActivity
                                    // Pasar el documento seleccionado a la actividad DetalleActivity
                                    // Iniciar la actividad DetalleActivity
                                    String documentid = document.getId();
                                    String titulo = document.getString("titulo");
                                    String descripcion = document.getString("descripcion");
                                    String ciudad = document.getString("ciudad");
                                    String estado = document.getString("estado");
                                    Boolean apto_discapacitados = document.getBoolean("apto_discapacitados");
                                    String categoria = document.getString("categoria");
                                    String latitud = document.getString("latitud");
                                    String longitud = document.getString("longitud");
                                    Boolean pet_friendly = document.getBoolean("pet_friendly");

                                    Intent intent = new Intent(ListaUbicaciones.this, DetalleUbicacionActivity.class);
                                    intent.putExtra("documentid", documentid);
                                    intent.putExtra("titulo", titulo);
                                    intent.putExtra("descripcion", descripcion);
                                    intent.putExtra("ciudad", ciudad);
                                    intent.putExtra("estado", estado);
                                    intent.putExtra("apto_discapacitados", apto_discapacitados);
                                    intent.putExtra("categoria", categoria);
                                    intent.putExtra("latitud", latitud);
                                    intent.putExtra("longitud", longitud);
                                    intent.putExtra("pet_friendly", pet_friendly);
                                    intent.putExtra("ubicaciones", ubicaciones);
                                    startActivityForResult(intent, DETALLE_REQUEST_CODE);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ListaUbicaciones.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            firestore.collection("lugares")
                    .whereEqualTo("ciudad", cityCapitalized).whereEqualTo("categoria", category)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dataList = new ArrayList<>();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                dataList.add(snapshot);
                            }
                            adapter = new MyAdapter(dataList);
                            recyclerView.setAdapter(adapter);
                            updateRecyclerView(dataList);

                            adapter.setOnItemClickListener(new MyAdapter.onItemClickListener() {
                                @Override
                                public void onItemClick(DocumentSnapshot document) {
                                    // Crear un Intent para abrir la actividad DetalleActivity
                                    // Pasar el documento seleccionado a la actividad DetalleActivity
                                    // Iniciar la actividad DetalleActivity
                                    String documentid = document.getId();
                                    String titulo = document.getString("titulo");
                                    String descripcion = document.getString("descripcion");
                                    String ciudad = document.getString("ciudad");
                                    String estado = document.getString("estado");
                                    Boolean apto_discapacitados = document.getBoolean("apto_discapacitados");
                                    String categoria = document.getString("categoria");
                                    String latitud = document.getString("latitud");
                                    String longitud = document.getString("longitud");
                                    Boolean pet_friendly = document.getBoolean("pet_friendly");

                                    Intent intent = new Intent(ListaUbicaciones.this, DetalleUbicacionActivity.class);
                                    intent.putExtra("documentid", documentid);
                                    intent.putExtra("titulo", titulo);
                                    intent.putExtra("descripcion", descripcion);
                                    intent.putExtra("ciudad", ciudad);
                                    intent.putExtra("estado", estado);
                                    intent.putExtra("apto_discapacitados", apto_discapacitados);
                                    intent.putExtra("categoria", categoria);
                                    intent.putExtra("latitud", latitud);
                                    intent.putExtra("longitud", longitud);
                                    intent.putExtra("pet_friendly", pet_friendly);
                                    intent.putExtra("ubicaciones", ubicaciones);
                                    startActivityForResult(intent, DETALLE_REQUEST_CODE);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ListaUbicaciones.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Obtener nombre de la ciudad a partir de la ubicación
                            String cityName = getCityName(location.getLatitude(), location.getLongitude());
                            textViewCity.setText("Te encuentras actualmente en: " + cityName);
                            String category = "Todas las categorias";
                            // Llamar al método para cargar los datos desde Firestore con la ciudad actual
                            fetchDataFromFirestore(cityName, category);
                        }else{
                            textViewCity.setText("No se pudo obtener la ubicación actual");
                        }
                    }
                });
    }
    private void fetchCity(String city, String category){
        textViewCity.setText("Te encuentras actualmente en: " + city);
        fetchDataFromFirestore(city, category);
    }

    private String getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String cityName = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }

    private void updateRecyclerView(List<DocumentSnapshot> dataList) {
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETALLE_REQUEST_CODE && resultCode == RESULT_OK) {
           if (data != null) {
               String latitud = data.getStringExtra("latitud");
               String longitud = data.getStringExtra("longitud");
               String titulot = data.getStringExtra("titulo");
               Log.d("Mapa", "Titulo: " + titulot + " Latitud: " + latitud + " Longitud: " + longitud);
               LatLng nuevaUbicacion = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
               ubicaciones.add(nuevaUbicacion);
               titulos.add(titulot);
           }
        }
    }
}