package com.bermer.rutabelica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaUbicaciones extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ubicaciones);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFirestore("test");

    }
    private void fetchDataFromFirestore(String city){
        firestore.collection("lugares")
                .whereEqualTo("ciudad", city)
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