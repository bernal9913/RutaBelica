package com.bermer.rutabelica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // firebase bs
        mAuth = FirebaseAuth.getInstance();
        startEverything();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void startEverything() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btn_iniciarsesion2);
    }
    private void loginUser(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(Login.this, "Ingresa tu correo electrónico y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            System.out.println("tofain");
                            Toast.makeText(Login.this, "Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();
                            // Obtener el ID del usuario autenticado
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            // Almacenar el ID del usuario en las preferencias compartidas
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userId", userId);
                            editor.apply();
                            finish();
                        }else {
                            Toast.makeText(Login.this, ":(", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}