package com.example.pharmassist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class doctor_log_in extends AppCompatActivity {
    private EditText dEmail;
    private EditText dPassword;
    private Button dLogIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_log_in);

        dEmail = (EditText) findViewById(R.id.DEmail);
        dPassword = (EditText) findViewById(R.id.DPassword);
        dLogIn = (Button) findViewById(R.id.DSignin);

        //getting Firebase auth object //
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getCurrentUser method is not null means user is already logged in //
        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity//
            finish();
            //Opening ProfileActivity //
            //startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }

        final ProgressDialog progressDialog = new ProgressDialog(this);


        dLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(dEmail.getEditableText().toString(),dPassword.getEditableText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    progressDialog.hide();
                                    startActivity(new Intent(getApplicationContext(), DoctorHome.class));
                                }
                                //TODO:
                                //Change to proper activity
                            }
                        });
            }
        });


    }
}
