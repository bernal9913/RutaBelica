package com.bermer.rutabelica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetalleUbicacionActivity extends AppCompatActivity {
    private TextView textTitulo;
    private TextView textDescripcion;
    private TextView textCiudadEstado;
    private TextView textCategoria;
    private TextView textAptoDiscapacitados;
    private TextView textPetFriendly;

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

        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String ciudadEstado = getIntent().getStringExtra("ciudad") + ", " + getIntent().getStringExtra("estado");
        String categoria = getIntent().getStringExtra("categoria");
        boolean aptoDiscapacitados = getIntent().getBooleanExtra("aptoDiscapacitados", false);
        boolean petFriendly = getIntent().getBooleanExtra("petFriendly", false);

        textTitulo.setText(titulo);
        textDescripcion.setText(descripcion);
        textCiudadEstado.setText(ciudadEstado);
        textCategoria.setText(categoria);
        textAptoDiscapacitados.setText("Apto para discapacitados: " + (aptoDiscapacitados ? "Sí" : "No"));
        textPetFriendly.setText("Pet friendly: " + (petFriendly ? "Sí" : "No"));
    }
}