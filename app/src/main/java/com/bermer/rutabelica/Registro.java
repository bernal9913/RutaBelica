package com.bermer.rutabelica;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
//import

public class Registro extends AppCompatActivity {
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPhoneN;
    private Spinner spinnerCountry;
    private EditText editTextDateOfBirth;
    private CheckBox terms, comms;
    private Button buttonRegister;
    private DatePickerDialog picker;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        // iniciar la db
        db = FirebaseFirestore.getInstance();
        // referenciar los elementos
        startEverything();
        editTextDateOfBirth.setInputType(InputType.TYPE_NULL);
        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(Registro.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String str = dayOfMonth + "/" + (month+1) + "/" + year;
                        String str1 = year + "-" + (month+1)+ "-"+ dayOfMonth;
                        editTextDateOfBirth.setText(str1);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clic boton registro");
                registerUser();
            }
        });

    }
    private void startEverything(){
        System.out.println("start registro");
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneN = findViewById(R.id.editTextPhoneNumber);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        terms = findViewById(R.id.checkBoxTerms);
        comms = findViewById(R.id.checkBoxCommunication);
        buttonRegister = findViewById(R.id.buttonRegister);
    }
    private void registerUser(){
        System.out.println("registerUser");
        String fn = editTextFullName.getText().toString();
        String e = editTextEmail.getText().toString();
        String p = editTextPassword.getText().toString();
        String rp = editTextConfirmPassword.getText().toString();
        String pn = editTextPhoneN.getText().toString();
        String dob = editTextDateOfBirth.getText().toString().trim();
        String c = spinnerCountry.getSelectedItem().toString();

        if (TextUtils.isEmpty(fn) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p) ||
                TextUtils.isEmpty(rp) || TextUtils.isEmpty(pn) || TextUtils.isEmpty(dob) ||
                TextUtils.isEmpty(c) || !terms.isChecked()) {
            System.out.println("campos llenos");
            Toast.makeText(Registro.this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show();
        } else {
            if (!p.equals(rp)) {
                System.out.println("contraseña no coincidente");
                Toast.makeText(Registro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else if (!terms.isChecked()) {
                Toast.makeText(Registro.this, "Acepta los términos y condiciones", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("creacion del user");
                Map<String, Object> user = new HashMap<>();
                user.put("fullname", fn);
                user.put("email", e);
                user.put("password", p);
                user.put("phone", pn);
                user.put("dob", dob);

                db.collection("users").document(e)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("todo salio bien ");
                                    Toast.makeText(Registro.this, "El registro fue exitoso?", Toast.LENGTH_SHORT);
                                }else {
                                    System.out.println("todo salio mal?");
                                    Toast.makeText(Registro.this, "Error: " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                // TODO: (24.10.3) [WriteStream]: (af06997) Stream closed with status: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}.
                // 2024-02-28 22:14:59.792  6949-7011  Firestore               com.bermer.rutabelica                W  (24.10.3) [Firestore]: Write failed at users/meraz@gmail.com: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}
                // 2024-02-28 22:14:59.793  6949-6949  System.out              com.bermer.rutabelica                I  todo salio mal?
            }
        }


    }
}