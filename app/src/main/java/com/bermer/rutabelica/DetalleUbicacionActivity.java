package com.bermer.rutabelica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetalleUbicacionActivity extends AppCompatActivity {
    private TextView textTitulo;
    private TextView textDescripcion;
    private TextView textCiudadEstado;
    private TextView textCategoria;
    private TextView textAptoDiscapacitados;
    private TextView textPetFriendly;
    private double latatitu;
    private double longitu;
    //private GoogleMap mMap;
    private String titulo;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        textTitulo = findViewById(R.id.textTitulo);
        textDescripcion = findViewById(R.id.textDescripcion);
        textCiudadEstado = findViewById(R.id.textCiudadEstado);
        textCategoria = findViewById(R.id.textCategoria);
        textAptoDiscapacitados = findViewById(R.id.textAptoDiscapacitados);
        textPetFriendly = findViewById(R.id.textPetFriendly);

        titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String ciudadEstado = getIntent().getStringExtra("ciudad") + ", " + getIntent().getStringExtra("estado");
        String categoria = getIntent().getStringExtra("categoria");
        boolean aptoDiscapacitados = getIntent().getBooleanExtra("aptoDiscapacitados", false);
        boolean petFriendly = getIntent().getBooleanExtra("petFriendly", false);
        String longitud = getIntent().getStringExtra("longitud");
        String latitud = getIntent().getStringExtra("latitud");
        latatitu = Double.parseDouble(latitud);
        longitu = Double.parseDouble(longitud);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                LatLng ubicacion = new LatLng(latatitu, longitu);
                googleMap.addMarker(new MarkerOptions().position(ubicacion).title(titulo));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
            }
        });

        textTitulo.setText(titulo);
        textDescripcion.setText(descripcion);
        textCiudadEstado.setText(ciudadEstado);
        textCategoria.setText(categoria);
        textAptoDiscapacitados.setText("Apto para discapacitados: " + (aptoDiscapacitados ? "Sí" : "No"));
        textPetFriendly.setText("Pet friendly: " + (petFriendly ? "Sí" : "No"));
        FrameLayout mapContainer = findViewById(R.id.map_container);
        mapContainer.addView(mapView);

        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }
    /*
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        LatLng ubicacion = new LatLng(latatitu, longitu);
        mMap.addMarker(new MarkerOptions().position(ubicacion).title(titulo));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15));

    }*/
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}