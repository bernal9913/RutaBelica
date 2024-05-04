package com.bermer.rutabelica;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RecoverPassActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private Button buttonRecover;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonRecover = findViewById(R.id.btnResetPassword);
        mAuth = FirebaseAuth.getInstance();

        buttonRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                if (email.isEmpty()) {
                    editTextEmail.setError("Campo requerido");
                    return;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecoverPassActivity.this, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RecoverPassActivity.this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}