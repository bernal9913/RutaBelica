package com.bermer.rutabelica;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetalleReporteActivity extends AppCompatActivity {
    private TextView textViewReason, textViewReportReason, textViewReportDetail, textViewDetailReport,
            textViewAptoDiscapacitados, textViewPetFriendly;
    private TextInputLayout textInputLayoutTitle, textInputLayoutDescription, textInputLayoutCity,
            textInputLayoutState, textInputLayoutLatitud, textInputLayoutLongitud,
            textInputLayoutCategoryReport;
    private EditText editTextTitle, editTextDescription, editTextCity, editTextState,
            editTextLatitud, editTextLongitud;
    private Spinner spinnerCategoryReport;
    private RadioGroup radioGroupAptoDiscapacitados, radioGroupPetFriendly;
    private RadioButton radioButtonSi, radioButtonNo, radioButtonPetFriendlySi, radioButtonPetFriendlyNo;
    private Button buttonSaveChanges;
    private CheckBox checkBoxInexistente;
    private String documentId;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reporte);
        db = FirebaseFirestore.getInstance();

        startEverything();
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxInexistente.isChecked()) {
                    eliminarReporte(documentId);
                }else{
                    verificarDatos();
                    actualizarReporte();
                }

            }
        });
    }
    private void eliminarReporte(String documentId){
        DocumentReference documentReference = db.collection("lugares").document(documentId);
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Eliminación", "Reporte eliminado correctamente");
                Toast.makeText(DetalleReporteActivity.this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al eliminar el reporte", e);
            }
        });
    }
    private void actualizarReporte() {
        DocumentReference documentReference = db.collection("lugares").document(documentId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("titulo", editTextTitle.getText().toString());
        updates.put("descripcion", editTextDescription.getText().toString());
        updates.put("ciudad", editTextCity.getText().toString());
        updates.put("estado", editTextState.getText().toString());
        updates.put("latitud", editTextLatitud.getText().toString());
        updates.put("longitud", editTextLongitud.getText().toString());
        updates.put("categoria", spinnerCategoryReport.getSelectedItem().toString());
        updates.put("apto_discapacitados", radioButtonSi.isChecked());
        updates.put("pet_friendly", radioButtonPetFriendlySi.isChecked());
        updates.put("reportado", false);
        updates.put("razon_reporte", "");
        updates.put("inexistente", checkBoxInexistente.isChecked() ? true : false);

        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Actualización", "Reporte actualizado correctamente");
                Toast.makeText(DetalleReporteActivity.this, "Reporte actualizado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al actualizar el reporte", e);
            }
        });

    }

    private void verificarDatos() {
        if (editTextTitle.getText().toString().isEmpty()) {
            textInputLayoutTitle.setError("El título no puede estar vacío");
            return;
        }
        textInputLayoutTitle.setError(null);
        if (editTextDescription.getText().toString().isEmpty()) {
            textInputLayoutDescription.setError("La descripción no puede estar vacía");
            return;
        }
        textInputLayoutDescription.setError(null);
        if (editTextCity.getText().toString().isEmpty()) {
            textInputLayoutCity.setError("La ciudad no puede estar vacía");
            return;
        }
        textInputLayoutCity.setError(null);
        if (editTextState.getText().toString().isEmpty()) {
            textInputLayoutState.setError("El estado no puede estar vacío");
            return;
        }
        textInputLayoutState.setError(null);
        if (editTextLatitud.getText().toString().isEmpty()) {
            textInputLayoutLatitud.setError("La latitud no puede estar vacía");
            return;
        }
        textInputLayoutLatitud.setError(null);
        if (editTextLongitud.getText().toString().isEmpty()) {
            textInputLayoutLongitud.setError("La longitud no puede estar vacía");
            return;
        }
        textInputLayoutLongitud.setError(null);
        if (spinnerCategoryReport.getSelectedItemPosition() == 0) {
            textInputLayoutCategoryReport.setError("Seleccione una categoría");
            return;
        }
        textInputLayoutCategoryReport.setError(null);
        if (radioGroupAptoDiscapacitados.getCheckedRadioButtonId() == -1) {
            textViewAptoDiscapacitados.setError("Seleccione una opción");
            return;
        }
        textViewAptoDiscapacitados.setError(null);
        if (radioGroupPetFriendly.getCheckedRadioButtonId() == -1) {
            textViewPetFriendly.setError("Seleccione una opción");
            return;
        }
        textViewPetFriendly.setError(null);

    }

    private void startEverything() {
        documentId = getIntent().getStringExtra("documentid");
        Log.d("DocumentId", documentId);
        textViewReason = findViewById(R.id.textViewReason);
        textInputLayoutCategoryReport = findViewById(R.id.textInputLayoutCategoryReport);
        textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        textInputLayoutCity = findViewById(R.id.textInputLayoutCity);
        textInputLayoutState = findViewById(R.id.textInputLayoutState);
        textInputLayoutLatitud = findViewById(R.id.textInputLayoutLatitud);
        textInputLayoutLongitud = findViewById(R.id.textInputLayoutLongitud);
        textViewReportReason = findViewById(R.id.textViewReportReason); // razon del reporte
        textViewReportDetail = findViewById(R.id.textViewReportDetail);
        textViewDetailReport = findViewById(R.id.textViewDetailReport); // detalle del reporte
        textViewAptoDiscapacitados = findViewById(R.id.textViewAptoDiscapacitados);
        textViewPetFriendly = findViewById(R.id.textViewPetFriendly);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextLatitud = findViewById(R.id.editTextLatitud);
        editTextLongitud = findViewById(R.id.editTextLongitud);
        checkBoxInexistente = findViewById(R.id.checkBoxInexistente);
        spinnerCategoryReport = findViewById(R.id.SpinnerCategoryReport);
        radioGroupAptoDiscapacitados = findViewById(R.id.radioGroupAptoDiscapacitados);
        radioGroupPetFriendly = findViewById(R.id.radioGroupPetFriendly);
        radioButtonSi = findViewById(R.id.radioButtonSi);
        radioButtonNo = findViewById(R.id.radioButtonNo);
        radioButtonPetFriendlySi = findViewById(R.id.radioButtonPetFriendlySi);
        radioButtonPetFriendlyNo = findViewById(R.id.radioButtonPetFriendlyNo);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        textViewReportReason.setText(getIntent().getStringExtra("razon_reporte"));
        Log.d("Razon del reporte", getIntent().getStringExtra("razon_reporte"));
        textViewDetailReport.setText(getIntent().getStringExtra("detalle_reporte"));
        Log.d("Detalle del reporte", getIntent().getStringExtra("detalle_reporte"));
        editTextTitle.setText(getIntent().getStringExtra("titulo"));
        Log.d("Titulo", getIntent().getStringExtra("titulo"));
        editTextDescription.setText(getIntent().getStringExtra("descripcion"));
        editTextCity.setText(getIntent().getStringExtra("ciudad"));
        editTextState.setText(getIntent().getStringExtra("estado"));
        editTextLatitud.setText(getIntent().getStringExtra("latitud"));
        editTextLongitud.setText(getIntent().getStringExtra("longitud"));
        spinnerCategoryReport.setSelection(getIntent().getIntExtra("categoria_reporte", 0));

        //booleans
        boolean aptoParaDiscapacitados = getIntent().getBooleanExtra("apto_discapacitados", false);
        boolean petFriendly = getIntent().getBooleanExtra("pet_friendly", false);

        // Establecer el estado de los botones de radio
        radioButtonSi.setChecked(aptoParaDiscapacitados);
        radioButtonNo.setChecked(!aptoParaDiscapacitados);
        radioButtonPetFriendlySi.setChecked(petFriendly);
        radioButtonPetFriendlyNo.setChecked(!petFriendly);
    }
}