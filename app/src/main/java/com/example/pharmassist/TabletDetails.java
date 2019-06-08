package com.example.pharmassist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TabletDetails extends AppCompatActivity {

    private EditText nameOfMedicine;
    private EditText morningCount;
    private EditText afternoonCount;
    private EditText nightCount;
    private EditText noOfDays;
    private Button submit;
    private DatabaseReference dbref;
    Tablet t = new Tablet();
    Intent i;
    private String uid;

    public class dbvalue {
        String UID,email;
    }

    void setEmpty() {
        nameOfMedicine.setText("");
        morningCount.setText("");
        afternoonCount.setText("");
        nightCount.setText("");
        noOfDays.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_details);

        nameOfMedicine = (EditText) findViewById(R.id.Name_medicine);
        morningCount = (EditText) findViewById(R.id.morning_pill);
        afternoonCount = (EditText) findViewById(R.id.afternoon_pill);
        nightCount = (EditText) findViewById(R.id.night_pill);
        noOfDays = (EditText) findViewById(R.id.NoOfDays);
        submit = (Button) findViewById(R.id.submitMedicalForm);
        i = getIntent();

        dbref = FirebaseDatabase.getInstance().getReference("medicinecode");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseDatabase.getInstance().getReference("UserDetails").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String email = dataSnapshot.getValue().toString();
                        if(email.equals(i.getStringExtra("email"))) {
                            uid = dataSnapshot.getKey();

                            dbref.orderByKey().addChildEventListener(new ChildEventListener() {


                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                                    if(dataSnapshot.getKey().equals(nameOfMedicine.getEditableText().toString())) {



                                        t.nopoints = Integer.parseInt(dataSnapshot.getValue().toString());
                                        t.name = nameOfMedicine.getEditableText().toString();
                                        t.noofdays = Integer.parseInt(noOfDays.getEditableText().toString());
                                        t.m = Integer.parseInt(morningCount.getEditableText().toString());
                                        t.l = Integer.parseInt(afternoonCount.getEditableText().toString());
                                        t.n = Integer.parseInt(nightCount.getEditableText().toString());
                                        t.perday = t.l + t.m + t.n;
                                        t.count = t.perday * t.noofdays;


                                        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("tablets").child(nameOfMedicine.getEditableText().toString()).setValue(t);
                                        setEmpty();
                                        Snackbar.make(submit,"Successfully Submitted",Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),DoctorHome.class));
    }
}
