package com.example.pharmassist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserLogin extends AppCompatActivity implements View.OnClickListener {
    //This activity is now a Listener Activity //

    // Define the Views //
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView doctorLogin;

    //Firebase auth object //
    private FirebaseAuth firebaseAuth;

    //Progress Dialog //
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //FirebaseDatabase.getInstance().getReference("Users").child("pls").setValue("work");

        //getting Firebase auth object //
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getCurrentUser method is not null means user is already logged in //
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity//
            finish();
            //Opening ProfileActivity //
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }

        Intent getIntent = getIntent();
        if(getIntent.getIntExtra("registration",2) == 1) {
            Log.i("register","complete");
            Snackbar.make(buttonSignIn,"Registration Complete! Continue to log in",Snackbar.LENGTH_LONG).show();
        }

        editTextEmail = (EditText) findViewById(R.id.Email);
        editTextPassword = (EditText) findViewById(R.id.Password);
        buttonSignIn = (Button) findViewById(R.id.BSignin);
        textViewSignup  = (TextView) findViewById(R.id.SignUp);
        doctorLogin = (TextView) findViewById(R.id.doctor_login);


        progressDialog = new ProgressDialog(this);

        //attaching onClick listener//
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        doctorLogin.setOnClickListener(this);

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //To Check if Email and Passwords are empty //
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the Email and Password are not empty display a progress dialog //

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        // User Login //
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        Log.i("work","logged in");
                        //if user is logged in //
                        if(task.isSuccessful()){
                            Log.i("work","logged in123");

                            //Start the ProfileActivity //
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == findViewById(R.id.SignUp)) {
            progressDialog.hide();
            //finish();
            startActivity(new Intent(getApplicationContext(),Register.class));
        }
        else if(view == findViewById(R.id.doctor_login)) {
            Intent intent = new Intent(this,DoctorLogIn.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

