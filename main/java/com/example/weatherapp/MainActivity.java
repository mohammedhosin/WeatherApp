package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;
import org.w3c.dom.Text;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    private static ArrayList<Weather> mWeatherObjects;
    private static TextView mTextViewApproved;
    private static final String FILE_NAME ="weather.txt";
    public static WeakReference<MainActivity> weakActivity;
    private Button buttonSearch;
    private static EditText editTextLat;
    private static EditText editTextLong;
    private TextView mTextViewLatitude;
    private TextView mTextViewLongitude;
    private ConnectivityManager manager;
    private NetworkInfo activeNetwork;

    private static RecyclerView mRecyclerView;
    private RequestQueue mQueue;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager= (ConnectivityManager)
                this.getSystemService(this.CONNECTIVITY_SERVICE);

        weakActivity = new WeakReference<>(MainActivity.this);
        mTextViewApproved = findViewById(R.id.textViewApprovedTime);
        mTextViewLatitude = findViewById(R.id.textViewLatitude);
        mTextViewLongitude = findViewById(R.id.textViewLongitude);
        buttonSearch = findViewById(R.id.button_parse);
        editTextLat = findViewById(R.id.editText_lat);
        editTextLong = findViewById(R.id.editText_lot);

        mQueue = Volley.newRequestQueue(this);
        mWeatherObjects = new ArrayList<>();


        RecyclerView();

        readFromFile(mRecyclerView);
        mRecyclerView.getAdapter().notifyDataSetChanged();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeNetwork =
                        manager.getActiveNetworkInfo();

                if (editTextLong.getText().toString().isEmpty() == true) {
                    editTextLong.setError("Missing input");
                }
                if (editTextLat.getText().toString().isEmpty() == true) {
                    editTextLat.setError("Missing input");
                }

                if(activeNetwork !=null && activeNetwork.isConnected()){
                    if(editTextLong.getText().toString().isEmpty() == false && editTextLat.getText().toString().isEmpty() == false ) {
                        mWeatherObjects.clear();
                        mRecyclerView.getAdapter().notifyDataSetChanged();

                        mTextViewLatitude.setText("Latitude: " + editTextLat.getText());
                        mTextViewLongitude.setText("Longitude: " + editTextLong.getText());
                        getWeatherData();
                    }

                } else{
                    openDialog();
                    readFromFile(mRecyclerView);
                }
                }
        });
    }

    public void openDialog(){
        WeatherDialog wd = new WeatherDialog();
        wd.show(getSupportFragmentManager(),"Info");
    }

    private void RecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new WeatherAdapter(mWeatherObjects);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static ArrayList<Weather> getmWeatherObjects(){
        return mWeatherObjects;
    }

    public static TextView getApprovedTime(){
        return mTextViewApproved;
    }

    public static RecyclerView getRecyclerView(){
        return mRecyclerView;
    }
    public static MainActivity getmInstanceActivity() {
        return weakActivity.get();
    }

    public static EditText getEditTextLat(){
        return editTextLat;
    }

    public static EditText getEditTextLong(){
        return editTextLong;
    }


    private void getWeatherData() {
        String lat = editTextLat.getText().toString();
        String lon = editTextLong.getText().toString();
        String u1 = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/" + lon +"/lat/" + lat + "/data.json" ;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, u1, null, new WeatherRespListener(), new WeatherErrorListener());
        mQueue.add(request);
    }


    public void saveToFile(View v){
        String textToPrint = mTextViewApproved.getText().toString() + "\n" +   mTextViewLatitude.getText().toString()+ "\n" + mTextViewLongitude.getText().toString() + "\n";
        for(int i =0;i<mWeatherObjects.size();i++){
            textToPrint += mWeatherObjects.get(i).getDateTime() + "\n" + mWeatherObjects.get(i).getTemp() + "\n" + mWeatherObjects.get(i).getTccMean() + "\n";
        }
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(textToPrint.getBytes());

            Toast.makeText(this,"Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos !=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readFromFile(View v){
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String textInput;
            String approv = new String();
            String lat = new String();
            String lot = new String();

            while((textInput =br.readLine()) !=null){
                sb.append(textInput).append("\n");
            }

            Scanner s = new Scanner(sb.toString());
            int numLine = 0;

            try {

                while (s.hasNext()) {
                    approv = s.nextLine();
                    numLine++;
                    lat = s.nextLine();
                    numLine++;
                    lot = s.nextLine();
                    numLine++;
                    if (numLine == 3) {
                        mTextViewApproved.setText(approv);
                        mTextViewLatitude.setText(lat);
                        mTextViewLongitude.setText(lot);
                    }

                    String date = s.nextLine();
                    double T = Double.parseDouble(s.nextLine());
                    int TccMean = Integer.parseInt(s.nextLine());

                    Weather w1 = new Weather(T, date, TccMean);
                    mWeatherObjects.add(w1);
                }
            }catch(Exception e){
                    e.printStackTrace();
                }


            mRecyclerView.getAdapter().notifyDataSetChanged();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis !=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}


class WeatherErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        try{
            if(error.getMessage().contains("Value Requested of type java.lang.String cannot be converted to JSONObject")==true){
                MainActivity.getEditTextLat().setError("Erroneous input");
                MainActivity.getEditTextLong().setError("Erroneous input");
            }
        }catch (Exception e) {
            MainActivity.getEditTextLat().setError("Erroneous input");
            MainActivity.getEditTextLong().setError("Erroneous input");
            e.printStackTrace();
        }

        error.printStackTrace();
    }

}

