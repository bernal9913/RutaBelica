package com.bermer.rutabelica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private TextView textTitulo, textDescripcion, textCiudadEstado, textCategoria, textAptoDiscapacitados, textPetFriendly;
    private TextInputLayout textInputLayoutDetails;
    private TextInputEditText editTextDetails;
    private Button buttonSubmit;
    private RadioGroup radioGroupReasons;
    private RadioButton radioButtonIncorrectLocation, radioButtonIncorrectInfo, radioButtonNotExists;
    private DatabaseReference databaseReference;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        textTitulo = findViewById(R.id.textTitulo);
        textDescripcion = findViewById(R.id.textDescripcion);
        textCiudadEstado = findViewById(R.id.textCiudadEstado);
        textCategoria = findViewById(R.id.textCategoria);
        textAptoDiscapacitados = findViewById(R.id.textAptoDiscapacitados);
        textPetFriendly = findViewById(R.id.textPetFriendly);
        textInputLayoutDetails = findViewById(R.id.textInputLayoutDetails);
        editTextDetails = findViewById(R.id.editTextDetails);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        radioGroupReasons = findViewById(R.id.radioGroupReasons);
        radioButtonIncorrectLocation = findViewById(R.id.radioButtonIncorrectLocation);
        radioButtonIncorrectInfo = findViewById(R.id.radioButtonIncorrectInfo);
        radioButtonNotExists = findViewById(R.id.radioButtonNotExists);

        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String ciudadEstado = getIntent().getStringExtra("ciudad") + ", " + getIntent().getStringExtra("estado");
        String categoria = getIntent().getStringExtra("categoria");
        boolean aptoDiscapacitados = getIntent().getBooleanExtra("aptoDiscapacitados", false);
        boolean petFriendly = getIntent().getBooleanExtra("petFriendly", false);
        String longitud = getIntent().getStringExtra("longitud");
        String latitud = getIntent().getStringExtra("latitud");

        textTitulo.setText(titulo);
        textDescripcion.setText(descripcion);
        textCiudadEstado.setText(ciudadEstado);
        textCategoria.setText(categoria);
        textAptoDiscapacitados.setText(aptoDiscapacitados ? "Discapacitados: Sí" : "Discapacitados: No");
        textPetFriendly.setText(petFriendly ? "Mascotas: Sí" : "Mascotas: No");
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupReasons.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                String reason = radioButton.getText().toString();
                String details = editTextDetails.getText().toString();

                CollectionReference cr = db.collection("lugares");

                Query query = cr.whereEqualTo("latitud", latitud).whereEqualTo("longitud", longitud);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener la referencia al documento encontrado
                                DocumentReference docRef = document.getReference();

                                // Crear un mapa con los campos que deseas actualizar
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("razon_reporte", reason);
                                updates.put("detalles_reporte", details);
                                updates.put("reportado", true);

                                // Actualizar el documento con los nuevos campos
                                docRef.update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // La actualización se realizó exitosamente
                                                Log.d("Reports", "Documento actualizado correctamente");
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al intentar actualizar el documento
                                                Log.w("Reports", "Error al actualizar el documento", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("Reports", "Error obteniendo documentos: ", task.getException());
                        }
                    }
                });
            }
        });

    }
}