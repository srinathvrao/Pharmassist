package com.example.pharmassist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ConsumedActivity extends AppCompatActivity {


    private Context context;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumed);


        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(
                "MAIN_PREF", Context.MODE_PRIVATE);


        Button yes = (Button) findViewById(R.id.yesButton);
        Button no = (Button) findViewById(R.id.noButton);
        while(MainActivity.tts != null && MainActivity.tts.isSpeaking());
        /*MainActivity.tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    MainActivity.tts.setLanguage(Locale.ENGLISH);
                }

            }
        }); */
        MainActivity.tts.speak("Did you consume the pill?",TextToSpeech.QUEUE_FLUSH,null,null);

        final Intent getIntent = getIntent();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("medname",getIntent.getStringExtra("medname"));
                String medname = getIntent.getStringExtra("medname");
                if(getIntent.getIntExtra("mln",-1) == 0) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(medname+"morning", 1);
                    editor.commit();
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tablets")
                            .child(getIntent.getStringExtra("medname")).child("count").setValue(getIntent.getIntExtra("medcount",0) - getIntent.getIntExtra("m",0));
                }
                else if(getIntent.getIntExtra("mln",-1) == 1) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(medname+"afternoon", 1);
                    editor.commit();
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tablets")
                            .child(getIntent.getStringExtra("medname")).child("count").setValue(getIntent.getIntExtra("medcount",0) - getIntent.getIntExtra("l",0));
                }
                else if(getIntent.getIntExtra("mln",-1) == 2) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(medname+"night", 1);
                    editor.commit();
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tablets")
                            .child(getIntent.getStringExtra("medname")).child("count").setValue(getIntent.getIntExtra("medcount",0) - getIntent.getIntExtra("n",0));
                }
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.tts.speak("Please contact your doctor if there are any problems",TextToSpeech.QUEUE_FLUSH,null,null);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.tts.speak("Please choose an option, did you consume your pill?",TextToSpeech.QUEUE_FLUSH,null,null);
    }
}
