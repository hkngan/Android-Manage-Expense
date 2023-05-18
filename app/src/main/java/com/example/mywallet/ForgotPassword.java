package com.example.mywallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    Button submit_pw;
    EditText email_pw;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email_pw = findViewById(R.id.input_email_fg);
        submit_pw = findViewById(R.id.submit_fg_btn);
        submit_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }

        });
    }
    private void forgotPassword(){
        String email;
        email = String.valueOf(email_pw.getText().toString().trim());
        if(TextUtils.isEmpty(email)){
            email_pw.setText("Enter your email!!");
        }else{
            progressDialog.create();
            progressDialog.show();
            progressDialog.setMessage("Sending email..");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this, "Email reset password has been sent", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ForgotPassword.this, "Email reset password has been failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();                        }
                    });

        }
    }
}