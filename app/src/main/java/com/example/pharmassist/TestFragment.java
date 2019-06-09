package com.example.pharmassist;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pharmassist.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TestFragment extends Fragment {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Speech recognition demo");
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.test_layout,container,false);


        //MainActivity.fragmentid = 1;



        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.i("VOICE RESULT",matches.get(0).toString());
            if(matches.contains("pharmacy")) {
                //Intent intent = new Intent(getContext(),Maps.class);
                Log.i("voice result","matched");
                //startActivity(new Intent("org.example.MY_ACTION_INTENT"));
                startActivity(new Intent(getContext(),MapsActivity.class));
            }
            else if(matches.contains("call doctor")) {
                startActivity(new Intent(getContext(),VideoChatViewActivity.class));
            }
        }
    }
}
