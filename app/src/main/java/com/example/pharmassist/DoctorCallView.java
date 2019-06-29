package com.example.pharmassist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmassist.Math.Fft;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import io.agora.rtc.video.VideoEncoderConfiguration; // 2.3.0 and later

import static java.lang.Math.ceil;

public class DoctorCallView extends AppCompatActivity {

    int count = 1;
    Bitmap b;

    String channelname;

    static Handler handler;
    static Runnable runnable;
    public ArrayList<Double> GreenAvgList=new ArrayList<Double>();
    public ArrayList<Double> RedAvgList=new ArrayList<Double>();public int counter = 0;
    private double SamplingFreq;
    public int Beats=0;
    public double Q =4.5;
    public double Wei=60, Agg=20,Hei=162;
    private static int SP = 0, DP = 0;
    public double bufferAvgB=0;
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static long startTime = 0;
    public boolean chatting = false;

    TextView sp,bp,bpm;

    private static final String LOG_TAG = VideoChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    SurfaceView surfaceView;

    // permission WRITE_EXTERNAL_STORAGE is not mandatory for Agora RTC SDK, just incase if you wanna save logs to external sdcard
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_call_view);

        DoctorHome.handler.removeCallbacks(DoctorHome.runnable);

        sp = (TextView) findViewById(R.id.sp);
        bp = (TextView) findViewById(R.id.bp);
        bpm = (TextView) findViewById(R.id.bpm);



        Log.i("doctorview","inside");

        Intent intent = getIntent();
        channelname = intent.getStringExtra("channelname");




        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initAgoraEngineAndJoinChannel();
        }

        /*FirebaseDatabase.getInstance().getReference().child("Doctors/"+ FirebaseAuth.getInstance().getUid()).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("heartbeat","inside firebase list");
                if(dataSnapshot.getKey().equals("sp") && Integer.parseInt(dataSnapshot.getValue().toString()) != 0)
                    sp.setText("SP: " + dataSnapshot.getValue());
                if(dataSnapshot.getKey().equals("bp") && Integer.parseInt(dataSnapshot.getValue().toString()) != 0)
                    bp.setText("DP: " + dataSnapshot.getValue());
                if(dataSnapshot.getKey().equals("bpm") && Integer.parseInt(dataSnapshot.getValue().toString()) != 0)
                    bpm.setText("BPM: " + dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
        /*handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {

            }
        },100); */





    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    finish();
                    break;
                }

                initAgoraEngineAndJoinChannel();
                break;
            }
        }
    }

    private final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        //FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        //SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        //surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        //surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEncCallClicked(View view) {
        chatting = false;
        FirebaseDatabase.getInstance().getReference().child("Doctors/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("call").setValue("string");
        FirebaseDatabase.getInstance().getReference().child("Doctors/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sp").setValue("0");
        FirebaseDatabase.getInstance().getReference().child("Doctors/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("dp").setValue("0");
        FirebaseDatabase.getInstance().getReference().child("Doctors/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bpm").setValue("0");

        sp.setText("");
        bp.setText("");
        bpm.setText("");


        DoctorHome.handler.postDelayed(DoctorHome.runnable,1000);
        finish();

    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

//      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false); // Earlier than 2.3.0
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void takeScreenshot() {
        try {
            // image naming and path  to include sd card  appending name you choose for file
            final String mPath = Environment.getExternalStorageDirectory().toString() + "/capture0" + ".jpg";
            Log.v("heartbeat",mPath);
            //count++;

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().findViewById(R.id.remote_video_view_container);

            b = Bitmap.createBitmap(surfaceView.getWidth() , surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

            /*Canvas c = new Canvas(b);
            v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
            v.draw(c);
            View v1 = surfaceView;
            //surfaceView.setDra
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false); */
            PixelCopy.request(surfaceView, b, new PixelCopy.OnPixelCopyFinishedListener() {
                @Override
                public void onPixelCopyFinished(int copyResult) {
                    File imageFile = new File(mPath);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(imageFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    int quality = 100;
                    b.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    process();



                }
            },new Handler());



            Log.i("heartbeat","saved");

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public void process() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String photoPath = Environment.getExternalStorageDirectory().toString() + "/capture0"+ ".jpg";
        count++;
        Log.i("heartbeat",photoPath);
        Log.v("heartbeat",photoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //if (!processing.compareAndSet(false, true)) return;

        double GreenAvg;
        double RedAvg;
        GreenAvg=ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(bitmap, height, width,3); //1 stands for red intensity, 2 for blue, 3 for green
        RedAvg=ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(bitmap, height, width,1); //1 stands for red intensity, 2 for blue, 3 for green
        GreenAvgList.add(GreenAvg);
        RedAvgList.add(RedAvg);
        Log.v("heartbeat",GreenAvg+", avgs, "+RedAvg);
//        for(int i=0;i<GreenAvgList.size();i++)
//            Log.v("heartbeat",GreenAvgList.get(i)+"");
//        for(int i=0;i<RedAvgList.size();i++)
//            Log.v("heartbeat",RedAvgList.get(i)+"");
        Beats = (int) GreenAvg;
        double ROB = 18.5;
        double ET = (364.5-1.23*Beats);
        double BSA = 0.007184*(Math.pow(Wei,0.425))*(Math.pow(Hei,0.725));
        double SV = (-6.6 + (0.25*(ET-35)) - (0.62*Beats) + (40.4*BSA) - (0.51*Agg));
        double PP = SV / ((0.013*Wei - 0.007*Agg-0.004*Beats)+1.307);
        double MPP = Q*ROB; // for man - 5, for women - 4.5
        SP = (int) (MPP + 3/2*PP);
        DP = (int) (MPP - PP/3);
        // SP, DP, GreenAvg

        Log.i("heartneat",String.valueOf(SP)+String.valueOf(DP));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sp.setText("SYS: " + String.valueOf(SP));
                bp.setText("DIA: " + String.valueOf(DP));
                bpm.setText("BPM: " + String.valueOf(Beats));
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Doctors/"+FirebaseAuth.getInstance().getUid()).child("sp").setValue(SP);
        FirebaseDatabase.getInstance().getReference().child("Doctors/"+FirebaseAuth.getInstance().getUid()).child("dp").setValue(DP);
        FirebaseDatabase.getInstance().getReference().child("Doctors/"+FirebaseAuth.getInstance().getUid()).child("bpm").setValue(Beats);

        ++counter;
        if (RedAvg < 200) {
            counter=0;
            processing.set(false);
        }
        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d; //to convert time to seconds
        processing.set(true);
        Log.v("heartbeat",totalTimeInSecs+" =====");
        if (totalTimeInSecs <= 30) {
            Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
            Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);
            SamplingFreq =  (counter/totalTimeInSecs);
            double HRFreq = Fft.FFT(Green,GreenAvgList.size(), SamplingFreq); // send the green array and get its fft then return the amount of heartrate per second
            double bpm=(int)ceil(HRFreq*60);
            double HR1Freq = Fft.FFT(Red, RedAvgList.size(), SamplingFreq);  // send the red array and get its fft then return the amount of heartrate per second
            double bpm1=(int)ceil(HR1Freq*60);

            Log.v("heartbeat",bpm+"<--bpm, bpm1--> "+bpm1);
            if((bpm > 45 || bpm < 200) )
            {
                if((bpm1 > 45 || bpm1 < 200)) {

                    bufferAvgB = (bpm+bpm1)/2;
                }
                else{
                    bufferAvgB = bpm;
                }
            }
            else if((bpm1 > 45 || bpm1 < 200)){
                bufferAvgB = bpm1;
            }

            if (bufferAvgB < 45 || bufferAvgB > 200) { //if the heart beat wasn't reasonable after all reset the progresspag and restart measuring
                startTime = System.currentTimeMillis();
                counter=0;
                processing.set(false);
                return;
            }


            processing.set(false);


        }

    }

    /*class SavePics extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(!processing.compareAndSet(true,false)){
                process();
                Log.v("heartbeat","processing. "+bufferAvgB);
            }
//                mediaDataObserverPlugin.saveCaptureVideoSnapshot("/sdcard/raw-data-test/capture" + count + ".jpg");
//                Log.v("yay","after");

            return null;
        }
    } */

    private void setupLocalVideo() {
        /*FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));*/
    }

    private void joinChannel() {

        Log.i("channelid","inside join channel");

        mRtcEngine.joinChannel(null, channelname, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
        chatting = true;
        Log.i("savepics","handler running");
                    Log.i("savepics","handler");
                    handler = new Handler();
                    handler.postDelayed(runnable = new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            takeScreenshot();
                            Log.i("savepics","handler running");
                            //SavePics savePics = new SavePics();
                            //savePics.execute();
                            if(!chatting)
                                handler.removeCallbacks(runnable);
                            else
                                handler.postDelayed(runnable,1000);

                        }
                    },1000);

    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

        surfaceView.setTag(uid); // for mark purpose

    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();


    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }
}

