package com.example.mywallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    EditText u_email, u_password;
    Button reg_btn;

    FirebaseAuth auth;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        register();
    }
    public void register(){
        u_email = findViewById(R.id.email_reg);
        u_password = findViewById(R.id.password_reg);
        reg_btn = findViewById(R.id.reg_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = u_email.getText().toString().trim();
                String password = u_password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    u_email.setError("Please enter email!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    u_password.setError("Please enter password!");
                }
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Logging in..");
                progressDialog.show();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration succesfull!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}