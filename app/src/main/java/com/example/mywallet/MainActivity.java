package com.example.mywallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText u_email, u_password;
    Button btn_login;
    TextView tv_reg, tv_forgot;

    ProgressDialog progressDialog;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.img3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img7, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img5, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        tv_reg = findViewById(R.id.reg_tv);
        tv_forgot = findViewById(R.id.forgot_tv);
        auth = FirebaseAuth.getInstance();

//        if(auth.getCurrentUser()!=null){
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//        }
        progressDialog = new ProgressDialog(this);
        login();
    }
    public void login(){
        u_email = findViewById(R.id.email_input);
        u_password = findViewById(R.id.pass_input);
        btn_login = findViewById(R.id.signin_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= u_email.getText().toString().trim();
                String password = u_password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    u_email.setError("Enter your email!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    u_password.setError(("Enter your password!"));
                    return;
                }
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Logging in..");
                progressDialog.show();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login succesfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
         tv_reg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
             }
         });

         tv_forgot.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                 startActivity(intent);
             }
         });
    }
}