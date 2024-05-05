package com.bermer.rutabelica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private Button btn1, btn2;

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btn1 = findViewById(R.id.button1admin);
        //btn2 = findViewById(R.id.button2admin);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();
        fetchDataFromFirestore();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminActivity.this, AddLocation.class);
                startActivity(i);
            }
        });

    }

    private void fetchDataFromFirestore(){
        Log.e("FetchDataFromFirestore", "Fetcheando resultados de reportes" );
        firestore.collection("lugares")
                .whereEqualTo("reportado", true)
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
                                String razon_reporte = document.getString("razon_reporte");
                                String detalle_reporte = document.getString("detalles_reporte");
                                Boolean reportado = document.getBoolean("reportado");

                                Intent intent = new Intent(AdminActivity.this, DetalleReporteActivity.class);
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
                                intent.putExtra("detalle_reporte", detalle_reporte);
                                intent.putExtra("razon_reporte", razon_reporte);
                                intent.putExtra("reportado", reportado);
                                startActivity(intent);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateRecyclerView(List<DocumentSnapshot> dataList) {
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }
}