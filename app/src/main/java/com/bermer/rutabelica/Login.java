package com.bermer.rutabelica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewPassword, textViewCuenta;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // firebase bs
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("rutabelica", MODE_PRIVATE);
        startEverything();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        textViewCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
            }
        });
        textViewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, RecoverPassActivity.class);
                startActivity(i);
            }
        });

    }

    private void startEverything() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        textViewCuenta = findViewById(R.id.textViewRegister);
        textViewPassword = findViewById(R.id.textViewForgotPassword);
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
                            Log.e("ADMIN_DEBUG", "UID: " + userId);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // Boolean admin = false;
                            CollectionReference adminCollection = db.collection("admins");

                            adminCollection.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        // el user es admin
                                        Boolean admin = true;
                                        Log.e("ADMIN_DEBUG", "El usuario es admin: " + admin.toString());

                                        // guardar el id del usuario en shared preferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("userId", userId);
                                        editor.putBoolean("isAdmin",admin);
                                        editor.apply();

                                        Intent i = new Intent(Login.this, AdminActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Boolean admin = false;
                                        Log.e("ADMIN_DEBUG", "El usuario no es admin: " + admin.toString());
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("userId", userId);
                                        editor.putBoolean("isAdmin",admin);
                                        editor.apply();
                                        // TODO: agregar el activity main despues de esto
                                        Intent i = new Intent(Login.this, ListaUbicaciones.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ADMIN_DEBUG", "Error: " + e.getMessage());
                                }
                            });
                        }else {
                            Toast.makeText(Login.this, "Correo y/o contraseña no validos", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}