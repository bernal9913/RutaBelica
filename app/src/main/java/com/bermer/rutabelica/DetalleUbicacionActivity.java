package com.bermer.rutabelica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DetalleUbicacionActivity extends AppCompatActivity {
    private ArrayList<LatLng> ubicaciones = new ArrayList<>();
    private TextView textTitulo;
    private TextView textDescripcion;
    private TextView textCiudadEstado;
    private TextView textCategoria;
    private TextView textAptoDiscapacitados;
    private TextView textPetFriendly;
    private Button btnAgregarRuta;
    private Button btnReportar;
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
        btnAgregarRuta = findViewById(R.id.btnAgregar);
        btnReportar = findViewById(R.id.btnReportar);


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
        ubicaciones = getIntent().getParcelableArrayListExtra("ubicaciones");

        Log.d("Mapa", "Latitud: " + latatitu + " Longitud: " + longitu);

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

        btnAgregarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ListaUbicaciones.ubicaciones.add(new LatLng(latatitu, longitu));
                // crear el punto
                LatLng ubisend = new LatLng(latatitu, longitu);
                // agregar a la lista de ubicaciones
                ubicaciones.add(ubisend);
                // enviar la lista de ubicaciones
                Intent intent = new Intent();
                //intent.putParcelableArrayListExtra("ubicaciones", ubicaciones);
                intent.putExtra("longitud", longitud);
                intent.putExtra("latitud", latitud);
                intent.putExtra("titulo", titulo);
                setResult(RESULT_OK, intent);
                Toast.makeText(DetalleUbicacionActivity.this, titulo.toString() + " ha sido agregado a la ruta!", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        btnReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleUbicacionActivity.this, ReportActivity.class);
                intent.putExtra("titulo", titulo);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("ciudad", ciudadEstado);
                intent.putExtra("categoria", categoria);
                intent.putExtra("aptoDiscapacitados", aptoDiscapacitados);
                intent.putExtra("petFriendly", petFriendly);
                intent.putExtra("longitud", longitud);
                intent.putExtra("latitud", latitud);
                startActivity(intent);
                finish();
            }
        });

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