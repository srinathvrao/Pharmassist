package com.example.pharmassist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    public class User {
        String UID,email,password;

        public User() {
            email = registerEmail.getEditableText().toString();
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseDatabase.getInstance().getReference().child("kay").setValue("HELLO TESTING");


        registerEmail = (EditText) findViewById(R.id.REmail);
        registerPassword = (EditText) findViewById(R.id.RPassword);
        registerButton = (Button) findViewById(R.id.RegisterButton);

        firebaseAuth = FirebaseAuth.getInstance();

        final ProgressDialog progressDialog = new ProgressDialog(this);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Creating account...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(registerEmail.getEditableText().toString(),registerPassword.getEditableText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("signed","hello whatttt 1231312312");
                                if(task.isSuccessful()) {
                                    Log.i("signed","kahfkajdsjslkjd");
                                    User user = new User();

                                    if(FirebaseAuth.getInstance().getCurrentUser()==null)
                                        Log.i("signed","hello whatttt");
                                    else
                                        Log.i("signed","world world world");
                                    Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                    //FirebaseDatabase.getInstance().getReference("Users").child("yay").setValue("HELLO TESTING");
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(registerEmail.getEditableText().toString());

                                    FirebaseDatabase.getInstance().getReference("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(registerEmail.getEditableText().toString());

                                    Tablet t = new Tablet();
                                    //FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Tablets").setValue(t);

                                    progressDialog.hide();
                                    intent = new Intent(getApplicationContext(), UserLogin.class);
                                    intent.putExtra("registration", 1);
                                    firebaseAuth.getInstance().signOut();
                                    startActivity(intent);
                                }

                            }
                        });

            }
        });






    }
}
