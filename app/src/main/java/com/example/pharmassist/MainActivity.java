package com.example.pharmassist;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int sensorFlag = 0;
    int sensorOver = 0;
    static ArrayList<Float> xPoints = new ArrayList<>();
    static ArrayList<Float> yPoints = new ArrayList<>();
    ArrayList<Integer> selectiedPoints = new ArrayList<>();
    Handler handler = new Handler();

    FirebaseAuth firebaseAuth;

    static public TextToSpeech tts;

    private Button logout;

    private NfcAdapter nfcAdapter;

    public void gotPoints(ArrayList a) {
        Log.i("POINTS",String.valueOf(a.size()));
        for(int i=0;i<a.size();i++)
            Log.i("POINTS",String.valueOf(a.get(i)));
        selectiedPoints.clear();
        sensorFlag = 0;
        Intent intent = new Intent(getApplicationContext(),MedicineRecognised.class);
        intent.putExtra("x",xPoints);
        intent.putExtra("y",yPoints);
        startActivity(intent);
        //finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("sensor",String.valueOf(sensorFlag));

        logout = (Button) findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                    tts.speak("Welcome, Place your prescription bottle on the screen",TextToSpeech.QUEUE_FLUSH,null,null);
                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                tts.stop();
                startActivity(new Intent(getApplicationContext(),UserLogin.class));

            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("sensor",String.valueOf(sensorFlag));
        int j = event.getPointerCount();
        if(event.getActionMasked() != MotionEvent.ACTION_POINTER_UP)
            selectiedPoints.add(j);
        if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            Log.i("action","finger removed");
            //if(j == Collections.max(selectiedPoints)) {
            //sensorOver = 1;
            sensorFlag = 0;
            Log.i("sensor",String.valueOf(sensorFlag));
            selectiedPoints.clear();
            //if(xPoints.size() != 0)
            gotPoints(xPoints);
            //}

        }
        if(j==4 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP ) {
            sensorFlag = 4;
            xPoints.clear();
            Log.i("TEST", String.valueOf(j));
            for (int i = 0; i < j; i++) {
                xPoints.add(event.getX(i));
                yPoints.add(event.getY(i));
            }
            //Log.i("TEST", String.valueOf(event.getX(i)));
        }
        if(j==3 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
            if(sensorFlag<j) {
                sensorFlag = j;
                xPoints.clear();
                Log.i("TEST", String.valueOf(j));
                for (int i = 0; i < j; i++) {
                    xPoints.add(event.getX(i));
                    //Log.i("TEST", String.valueOf(event.getX(i)));
                }
            }
        }

        if(j==2 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
            if(sensorFlag<j) {
                sensorFlag = j;
                xPoints.clear();
                Log.i("TEST", String.valueOf(j));
                for (int i = 0; i < j; i++) {
                    xPoints.add(event.getX(i));
                    //Log.i("TEST", String.valueOf(event.getX(i)));
                }
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        tts.stop();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
