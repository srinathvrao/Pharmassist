package com.example.pharmassist;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivityFragment extends Fragment {
    int sensorFlag = 0;
    int sensorOver = 0;
    static ArrayList<Float> xPoints = new ArrayList<>();
    static ArrayList<Float> yPoints = new ArrayList<>();
    ArrayList<Integer> selectiedPoints = new ArrayList<>();

    static public TextToSpeech tts;

    private Button logout;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;


    public void gotPoints(ArrayList a) {
        Log.i("POINTS",String.valueOf(a.size()));
        for(int i=0;i<a.size();i++)
            Log.i("POINTS",String.valueOf(a.get(i)));
        selectiedPoints.clear();
        sensorFlag = 0;
        Intent intent = new Intent(getContext(),MedicineRecognised.class);
        intent.putExtra("x",xPoints);
        intent.putExtra("y",yPoints);
        startActivity(intent);
        //finish();

    }



    /*public boolean onTouchEvent(MotionEvent event) {
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
        //return super.onTouchEvent(event);
        return true;
    } */




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_activity_fragment,container,false);


        /*RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.main_activity_fragment_layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("nooffingers",String.valueOf(event.getPointerCount()));

                return false;
            }
        }); */
        /*rootView.setOnTouchListener(new View.OnTouchListener() {



            public boolean onTouch(View v, MotionEvent event) {

                Log.i("sensor",String.valueOf(sensorFlag));
                int j = event.getPointerCount();
                Log.i("nooffingers",String.valueOf(j));
                if(event.getAction() != MotionEvent.ACTION_POINTER_UP)
                    selectiedPoints.add(j);
                if(event.getAction() == MotionEvent.ACTION_POINTER_UP) {
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
                if(j==4 && event.getAction() != MotionEvent.ACTION_POINTER_UP ) {
                    sensorFlag = 4;
                    xPoints.clear();
                    Log.i("TEST", String.valueOf(j));
                    for (int i = 0; i < j; i++) {
                        xPoints.add(event.getX(i));
                        yPoints.add(event.getY(i));
                    }
                    //Log.i("TEST", String.valueOf(event.getX(i)));
                }
                if(j==3 && event.getAction() != MotionEvent.ACTION_POINTER_UP) {
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

                if(j==2 && event.getAction() != MotionEvent.ACTION_POINTER_UP) {
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


                return true;
            }
        }); */


        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                    tts.speak("Welcome, Place your prescription bottle on the screen",TextToSpeech.QUEUE_FLUSH,null,null);
                }

            }
        });



        return rootView;
    }
}
