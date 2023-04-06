package com.example.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnGoToLogin;
    private Button btnRegister;
    public ProgressBar loaderRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnGoToLogin = (Button)findViewById(R.id.btnGoToLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        loaderRegister =(ProgressBar)findViewById(R.id.loaderRegister);
        EditText inpEmail = (EditText)findViewById(R.id.inpEmail);
        EditText inpPassword = (EditText)findViewById(R.id.inpPassword);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //goto login
        btnGoToLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });


         btnRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v ){
                String email = inpEmail.getText().toString();
                String password = inpPassword.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext() ,"Please enter your email", Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext() ,"Please enter a password", Toast.LENGTH_SHORT).show();
                }else{
                    disableButton();
                    loaderRegister.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "User created successfully.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Something went wrong.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            enableButton();
                            loaderRegister.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    protected void disableButton(){
        btnRegister.setEnabled(false);
        btnGoToLogin.setEnabled(false);
    }

    protected void enableButton(){
        btnRegister.setEnabled(true);
        btnGoToLogin.setEnabled(true);
    }
}