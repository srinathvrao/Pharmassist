package com.example.pharmassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicineRecognised extends AppCompatActivity {


    /*public class Tablet {

        public String name, dateregd;
        public int count, noofdays, nopoints,perday,m,l,n;

        Tablet() {
            m=0;
            l=0;
            n=0;
            name="0";
            dateregd = "0";
            count = 0;
            noofdays = 0;
            nopoints = 0;
            perday = 0;

        }


    }*/



    ArrayList<Float> xPoints = new ArrayList<>();
    ArrayList<Float> yPoints = new ArrayList<>();
    float def = (float)0.0;

    private TextView morning;
    private TextView afternoon;
    private TextView night;
    private Context context;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_recognised);
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(
                "MAIN_PREF", Context.MODE_PRIVATE);



        if(MainActivity.tts != null) {
            MainActivity.tts.stop();
        }

        morning = (TextView) findViewById(R.id.morning_dose);
        afternoon = (TextView) findViewById(R.id.afternoon_dose);
        night = (TextView) findViewById(R.id.night_dose);

        MainActivity.tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    MainActivity.tts.setLanguage(Locale.ENGLISH);
                }

            }
        });


        final Intent getIntent = getIntent();
        xPoints = (ArrayList<Float>)getIntent.getSerializableExtra("x");
        yPoints = (ArrayList<Float>)getIntent.getSerializableExtra("y");
        /*for(int i=0;i<MainActivity.xPoints.size();i++)
            Log.i("helloworld",String.valueOf(xPoints.get(i))); */

        /*tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });*/

        final TextView tv = (TextView) findViewById(R.id.medicine_name);
        final TextView tv2 = (TextView) findViewById(R.id.dosage);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Fetching details..");
        progressDialog.show();




        FirebaseDatabase.getInstance().getReference().child("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/tablets")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Tablet t = snapshot.getValue(Tablet.class);

                            if(getIntent.getIntExtra("nfc",-1) == 1) {
                                if(t.name.equals(getIntent.getStringExtra("nfcMsg"))) {

                                    progressDialog.hide();
                                    tv.setText(t.name);
                                    tv2.setText(t.perday+" per day");
                                    morning.setText("Morning: "+t.m+" pills");
                                    afternoon.setText("Afternoon: "+t.l+" pills");
                                    night.setText("Night: "+t.n+" pills");
                                    Log.i("testtablet",t.name);
                                    if(t.dateregd.equals("0")){
                                        MainActivity.tts.speak("The medicine is " + t.name +".You are requested to use it "+t.perday+" times per day."+t.m+" times in the moring, "+t.l+" times in the afternoon and "+t.n+" times per day at night for the next "+t.noofdays+ " days.",TextToSpeech.QUEUE_FLUSH,null,null);
                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tablets").child(t.name).child("dateregd").setValue(date);
                                    }
                                    else {
                                        Date currentTime = Calendar.getInstance().getTime();
                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                        String defval = "-1";
                                        String cheval = sharedPref.getString("sysdate", defval);
                                        if(!cheval.equals(defval)){
                                            if(!cheval.equals(date)){
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.clear();
                                                editor.commit();
                                                Log.i("addeddate","cleared all data");
                                            }
                                        }
                                        else{

                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("sysdate",date);
                                            editor.commit();
                                            Log.i("addeddate","added "+date);
                                        }
                                        if (/*currentTime.getHours()>7 &&*/ currentTime.getHours()<11) {
                                            if(t.m == 0)
                                                MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                            else {
                                                int defaultValue = -1;
                                                int checkval = sharedPref.getInt(t.name+"morning", defaultValue);
                                                if(checkval!=defaultValue){
                                                    MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills  in the morning.", TextToSpeech.QUEUE_FLUSH, null, null);
                                                }
                                                else{
                                                    if(t.count == 0)
                                                        MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                    else {
                                                        if (t.count < 4)
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        else
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                        intent.putExtra("mln", 0);
                                                        intent.putExtra("medname", t.name);
                                                        intent.putExtra("medcount", t.count);
                                                        intent.putExtra("m", t.m);
                                                        startActivity(intent);
                                                    }
                                                }

                                            }
                                        }
                                        else if (currentTime.getHours()>12 && currentTime.getHours()<15) {
                                            if(t.l == 0)
                                                MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                            else {
                                                int defaultValue = -1;
                                                int checkval = sharedPref.getInt(t.name+"afternoon", defaultValue);
                                                if(checkval!=defaultValue){
                                                    MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills in the afternoon.", TextToSpeech.QUEUE_FLUSH, null, null);
                                                }
                                                else{
                                                    if(t.count == 0)
                                                        MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                    else {
                                                        if (t.count < 4)
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        else
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.l + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                        intent.putExtra("mln", 1);
                                                        intent.putExtra("medname", t.name);
                                                        intent.putExtra("medcount", t.count);
                                                        intent.putExtra("m", t.l);
                                                        startActivity(intent);
                                                    }
                                                }

                                            }
                                        }
                                        else if (currentTime.getHours()>19 && currentTime.getHours()<21) {
                                            if(t.n == 0)
                                                MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                            else {
                                                int defaultValue = -1;
                                                int checkval = sharedPref.getInt(t.name+"night", defaultValue);
                                                if(checkval!=defaultValue){
                                                    MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills in the night.", TextToSpeech.QUEUE_FLUSH, null, null);
                                                }
                                                else{
                                                    if(t.count == 0)
                                                        MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                    else {
                                                        if (t.count < 4)
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        else
                                                            MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.n + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                        Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                        intent.putExtra("mln", 2);
                                                        intent.putExtra("medname", t.name);
                                                        intent.putExtra("medcount", t.count);
                                                        intent.putExtra("m", t.n);
                                                        startActivity(intent);
                                                    }
                                                }

                                            }
                                        }
                                    }





                                }
                            }


                            //Log.i("tablets",t.nopoints+" .. "+xPoints.size());
                            else if(t.nopoints==xPoints.size()){

//                                SharedPreferences.Editor editor = sharedPref.edit();
//                                editor.putInt(t.name,1);
//                                editor.commit();
                                //Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                                tv.setText(t.name);
                                tv2.setText(t.perday+" per day");
                                morning.setText("Morning: "+t.m+" pills");
                                afternoon.setText("Afternoon: "+t.l+" pills");
                                night.setText("Night: "+t.n+" pills");
                                Log.i("testtablet",t.name);
                                if(t.dateregd.equals("0")){
                                    MainActivity.tts.speak("The medicine is " + t.name +".You are requested to use it "+t.perday+" times per day."+t.m+" times in the moring, "+t.l+" times in the afternoon and "+t.n+" times per day at night for the next "+t.noofdays+ " days.",TextToSpeech.QUEUE_FLUSH,null,null);
                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tablets").child(t.name).child("dateregd").setValue(date);
                                }
                                else {
                                    Date currentTime = Calendar.getInstance().getTime();
                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    String defval = "-1";
                                    String cheval = sharedPref.getString("sysdate", defval);
                                    if(!cheval.equals(defval)){
                                        if(!cheval.equals(date)){
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.clear();
                                            editor.commit();
                                            Log.i("addeddate","cleared all data");
                                        }
                                    }
                                    else{

                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("sysdate",date);
                                        editor.commit();
                                        Log.i("addeddate","added "+date);
                                    }
                                    if (/*currentTime.getHours()>7 &&*/ currentTime.getHours()<11) {
                                        if(t.m == 0)
                                            MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                        else {
                                            int defaultValue = -1;
                                            int checkval = sharedPref.getInt(t.name+"morning", defaultValue);
                                            if(checkval!=defaultValue){
                                                MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills  in the morning.", TextToSpeech.QUEUE_FLUSH, null, null);
                                            }
                                            else{
                                                if(t.count == 0)
                                                    MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                else {
                                                    if (t.count < 4)
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    else
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                    intent.putExtra("mln", 0);
                                                    intent.putExtra("medname", t.name);
                                                    intent.putExtra("medcount", t.count);
                                                    intent.putExtra("m", t.m);
                                                    startActivity(intent);
                                                }
                                            }

                                        }
                                    }
                                    else if (currentTime.getHours()>12 && currentTime.getHours()<15) {
                                        if(t.l == 0)
                                            MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                        else {
                                            int defaultValue = -1;
                                            int checkval = sharedPref.getInt(t.name+"afternoon", defaultValue);
                                            if(checkval!=defaultValue){
                                                MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills in the afternoon.", TextToSpeech.QUEUE_FLUSH, null, null);
                                            }
                                            else{
                                                if(t.count == 0)
                                                    MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                else {
                                                    if (t.count < 4)
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    else
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.l + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                    intent.putExtra("mln", 1);
                                                    intent.putExtra("medname", t.name);
                                                    intent.putExtra("medcount", t.count);
                                                    intent.putExtra("m", t.l);
                                                    startActivity(intent);
                                                }
                                            }

                                        }
                                    }
                                    else if (currentTime.getHours()>19 && currentTime.getHours()<21) {
                                        if(t.n == 0)
                                            MainActivity.tts.speak("The medicine is " + t.name +".Please do not consume the medicine now",TextToSpeech.QUEUE_FLUSH,null,null);
                                        else {
                                            int defaultValue = -1;
                                            int checkval = sharedPref.getInt(t.name+"night", defaultValue);
                                            if(checkval!=defaultValue){
                                                MainActivity.tts.speak("The medicine is " + t.name + ". You have already consumed these pills in the night.", TextToSpeech.QUEUE_FLUSH, null, null);
                                            }
                                            else{
                                                if(t.count == 0)
                                                    MainActivity.tts.speak("The medicine is " + t.name +".You are out of pills. Please visit your doctor for the next appointment",TextToSpeech.QUEUE_FLUSH,null,null);
                                                else {
                                                    if (t.count < 4)
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You have less than four pills remaining and are requested to consume " + t.m + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    else
                                                        MainActivity.tts.speak("The medicine is " + t.name + ".You  are requested to consume " + t.n + " pills", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    Intent intent = new Intent(getApplicationContext(), ConsumedActivity.class);
                                                    intent.putExtra("mln", 2);
                                                    intent.putExtra("medname", t.name);
                                                    intent.putExtra("medcount", t.count);
                                                    intent.putExtra("m", t.n);
                                                    startActivity(intent);
                                                }
                                            }

                                        }
                                    }
                                }
                                break;
                            }
                            else
                                Log.i("testtablet","Tablet not found "+ xPoints.size());

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    @Override
    public void onBackPressed() {
        Intent intent =  new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
