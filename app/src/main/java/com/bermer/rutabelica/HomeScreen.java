package com.bermer.rutabelica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends AppCompatActivity {
    Button iniciarSesion, registrarse, testlugar;
    FloatingActionButton contacto;
    PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        iniciarSesion = findViewById(R.id.btn_iniciarSesion);
        registrarse = findViewById(R.id.btn_registrarse);
        contacto = findViewById(R.id.fab_contacto);
        //testlugar = findViewById(R.id.buttontest);


        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, Registro.class);
                startActivity(i);
            }
        });
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, Login.class);
                startActivity(i);
            }
        });


    }
    private void showPopup(View anchorView){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacto");

        // Establece el contenido del AlertDialog
        builder.setMessage("Email: ejemplo@example.com\nTeléfono: +123456789 \n Cualquier cosa, arreglese con dios");

        // Agrega un botón de "Cerrar"
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Crea y muestra el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}