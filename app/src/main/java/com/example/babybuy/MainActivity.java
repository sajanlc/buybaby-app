package com.example.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.babybuy.activities.NavigationDrawerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public  ProgressBar loaderLogin;
    public  EditText email ;
    public  EditText password;
    public boolean isLoginSuccess;
    private  Button btnRegisterNow;
    private  Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }

        loaderLogin = (ProgressBar)findViewById(R.id.loaderLogin);
        email = (EditText)findViewById(R.id.inpLoginEmail);
        password = (EditText) findViewById(R.id.inpLoginPassword) ;
        btnRegisterNow = (Button) findViewById(R.id.btnRegisterNow);
        //goto register activity
        btnRegisterNow.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        //Login
        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                if(userEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your email.",
                            Toast.LENGTH_SHORT).show();
                }else if (userPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your password.",
                            Toast.LENGTH_SHORT).show();
                }else{

                    userSignIn(userEmail , userPassword);

                }
            }
        });

    }
    protected void userSignIn(String email ,String password){
        try {
            disableButton();
            loaderLogin.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext() ,"Login Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, NavigationDrawerActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext() ,"User name or Password is wrong", Toast.LENGTH_SHORT).show();
                        }
                        loaderLogin.setVisibility(View.INVISIBLE);
                        enableButton();
                    }
                });
        }catch(Exception e){
            Toast.makeText(getApplicationContext() ,"Internal Error", Toast.LENGTH_SHORT).show();
            isLoginSuccess = false;
        }
    }

    protected void disableButton(){
        btnLogin.setEnabled(false);
        btnRegisterNow.setEnabled(false);
    }

    protected void enableButton(){
        btnLogin.setEnabled(true);
        btnRegisterNow.setEnabled(true);
    }






}