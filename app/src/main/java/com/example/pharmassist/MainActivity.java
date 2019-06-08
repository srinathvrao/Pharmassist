package com.example.pharmassist;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity /*implements NfcAdapter.CreateNdefMessageCallback */ {

    static int fragmentid = 0;
    static int sensorFlag = 0;
    int sensorOver = 0;
    static ArrayList<Float> xPoints = new ArrayList<>();
    static ArrayList<Float> yPoints = new ArrayList<>();
    ArrayList<Integer> selectiedPoints = new ArrayList<>();
    GestureDetector gestureDetector;

    static FirebaseAuth firebaseAuth;

    static public TextToSpeech tts;

    private Button logout;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;


    private FragmentAdapter mAdapter;
    private VerticalViewPager mPager;

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

        //Window window = new Activity().getWindow();

        //window.setStatusBarColor();


        //logout = (Button) findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();

        /*RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_activity_layout);


        gestureDetector=new GestureDetector(this,new OnSwipeListener(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction==Direction.up){
                    //do your stuff
                    Log.i("swipeevent", "onSwipe: up");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,new TestFragment()).commit();

                }

                if (direction==Direction.down){
                    //do your stuff
                    Log.i("swipeevent", "onSwipe: down");
                }
                return true;
            }


        });
        layout.setOnTouchListener(this); */

        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);



       /* NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        mNfcAdapter = nfcManager.getDefaultAdapter();

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
        }); */

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Log.i("nooffingers",String.valueOf(event.getPointerCount()));
        Log.i("fragmentid",String.valueOf(fragmentid));
        //if(fragmentid == 0) {
            Log.i("sensor", String.valueOf(sensorFlag));
            int j = event.getPointerCount();
            Log.i("nooffingers",String.valueOf(j));
            if (event.getActionMasked() != MotionEvent.ACTION_POINTER_UP)
                selectiedPoints.add(j);
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
                Log.i("action", "finger removed");
                //if(j == Collections.max(selectiedPoints)) {
                //sensorOver = 1;
                sensorFlag = 0;
                Log.i("sensor", String.valueOf(sensorFlag));
                selectiedPoints.clear();
                //if(xPoints.size() != 0)
                gotPoints(xPoints);
                //}

            }
            if (j == 4 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
                sensorFlag = 4;
                xPoints.clear();
                Log.i("TEST", String.valueOf(j));
                for (int i = 0; i < j; i++) {
                    xPoints.add(event.getX(i));
                    yPoints.add(event.getY(i));
                }
                //Log.i("TEST", String.valueOf(event.getX(i)));
            }
            if (j == 3 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
                if (sensorFlag < j) {
                    sensorFlag = j;
                    xPoints.clear();
                    Log.i("TEST", String.valueOf(j));
                    for (int i = 0; i < j; i++) {
                        xPoints.add(event.getX(i));
                        //Log.i("TEST", String.valueOf(event.getX(i)));
                    }
                }
            }

            if (j == 2 && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
                if (sensorFlag < j) {
                    sensorFlag = j;
                    xPoints.clear();
                    Log.i("TEST", String.valueOf(j));
                    for (int i = 0; i < j; i++) {
                        xPoints.add(event.getX(i));
                        //Log.i("TEST", String.valueOf(event.getX(i)));
                    }
                }
            }

        //}
        return super.onTouchEvent(event);
        //return false;

    }


    /*@Override
    public void onBackPressed() {
        tts.stop();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private static NdefMessage getTestMessage() {
        byte[] mimeBytes = "application/com.android.cts.verifier.nfc"
                .getBytes(Charset.forName("US-ASCII"));
        byte[] id = new byte[] {1, 3, 3, 7};
        byte[] payload = "CTS Verifier NDEF Push Tag".getBytes(Charset.forName("US-ASCII"));
        return new NdefMessage(new NdefRecord[] {
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, payload)
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    // sending message
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return getTestMessage();
    }


    private NdefMessage[] getNdefMessages(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }

    static String displayByteArray(byte[] bytes) {
        String res="";
        StringBuilder builder = new StringBuilder().append("[");
        for (int i = 7; i < bytes.length; i++) {
            res+=(char)bytes[i];
        }
        return res;
    }

    // displaying message
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        NdefMessage[] messages = getNdefMessages(intent);
        Log.i("blah",displayByteArray(messages[0].toByteArray()));
        Intent nfcIntent = new Intent(getApplicationContext(),MedicineRecognised.class);

        nfcIntent.putExtra("nfc",1);
        nfcIntent.putExtra("nfcMsg",displayByteArray(messages[0].toByteArray()));

        startActivity(nfcIntent);
        //tv.setText(displayByteArray(messages[0].toByteArray()));
    } */


}
