package com.example.pharmassist;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    Context context;
    public GetNearbyPlaces(Context context) {
        this.context = context;
    }

    GoogleMap nMap;
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    String data;
    StringBuilder stringBuilder;



    @Override
    protected String doInBackground(Object... params) {

        nMap = (GoogleMap)params[0];
        url = (String)params[1];

        try {
            URL myURL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)myURL.openConnection();
            httpURLConnection.connect();

            is = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(is));
            stringBuilder = new StringBuilder();

            String line ="";
            while ((line=bufferedReader.readLine()) != null){
                stringBuilder.append(line);

            }

            data = stringBuilder.toString();


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


        return data;
    }

    @Override
    protected void onPostExecute(String s){

        try {
            Log.i("Debug","Inside execute");
            JSONObject parentObject = new JSONObject(s);
            Log.i("Debug",parentObject.toString());
            JSONArray resultArray = parentObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                Log.i("XLoop","For Loop");
                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject locationObject = jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude = locationObject.getString("lat");
                String longitude = locationObject.getString("lng");

                JSONObject nameObject = resultArray.getJSONObject(i);

                String name_pharmacy = nameObject.getString("name");
                String vicinity = nameObject.getString("vicinity");


                LatLng latLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(vicinity);
                markerOptions.position(latLng);

                nMap.addMarker(markerOptions);


            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
