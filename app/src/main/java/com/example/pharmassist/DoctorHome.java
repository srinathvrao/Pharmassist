package com.example.pharmassist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorHome extends AppCompatActivity {

    static Handler handler;
    static Runnable runnable;
    static boolean flag = false;

    private int check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText edt = (EditText) findViewById(R.id.emailp);
        final Button butt = (Button) findViewById(R.id.docsub);
        final ProgressBar progbar = (ProgressBar) findViewById(R.id.progbar);
        final TextView textView = (TextView) findViewById(R.id.prog2);
        Button logout = (Button) findViewById(R.id.dlogout);

        String puid="";



        handler = new Handler();

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {

                Log.i("runnablehandler","inside");

                FirebaseDatabase.getInstance().getReference().child("Doctors/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //Tablet t = snapshot.getValue(Tablet.class);

                            //Log.i("docchannelid",snapshot.toString());

                            if(snapshot.getKey().equals("call") && !snapshot.getValue().equals("string")) {
                                String channelname = (String) snapshot.getValue();
                                Log.i("channelid",channelname);
                                Intent intent = new Intent(getApplicationContext(),DoctorCallView.class);
                                intent.putExtra("channelname",channelname);
                                flag = true;
                                //handler.removeCallbacks(runnable);
                                startActivity(intent);
                            }
                        }
                        handler.postDelayed(runnable,1000);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        },1000);



        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),UserLogin.class));
            }
        });

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progbar.setVisibility(View.VISIBLE);
                final String email = edt.getEditableText().toString();
                if(email!=""){


                    Intent i = new Intent(getApplicationContext(),TabletDetails.class);
                    i.putExtra("email",email);
                    startActivity(i);
//                    FirebaseDatabase.getInstance().getReference().child("Users")
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                        User user = snapshot.getValue(User.class);
//                                        if(user.email==email){
//                                             check=1;
//                                            break;
//                                        }
//                                    }
//                                    Log.i("emailfound","WHERE IS THIS");
//                                    if(check==0)
//                                        Log.i("emailfound","email not found");
//                                    else{
//
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                }
//                            });

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
