package com.example.weatherapp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherRespListener implements Response.Listener<JSONObject> {

    String approvedTime;

    public WeatherRespListener() {
        this.approvedTime = new String();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            approvedTime = response.getString("approvedTime").replace("T"," ").replace("Z", " ");
            JSONArray timeSeries = response.getJSONArray("timeSeries");
            String validTime = new String();
            String parameterName = new String();
            double t = 0;
            int tccMean = 0;

            for (int i = 0; i < timeSeries.length(); i++) {
                JSONObject parametersAtTime = timeSeries.getJSONObject(i);

                validTime = parametersAtTime.getString("validTime").replace("T"," ").replace("Z"," ");

                JSONArray parameters = parametersAtTime.getJSONArray("parameters");

                for (int j = 0; j < parameters.length(); j++) {
                    JSONObject parameter = parameters.getJSONObject(j);
                    parameterName = parameter.getString("name");

                    if(parameterName.matches("t")){
                        JSONArray tValues = parameter.getJSONArray("values");
                        t = tValues.getDouble(0);
                    }
                    if(parameterName.matches("tcc_mean")){
                        JSONArray tValues = parameter.getJSONArray("values");
                        tccMean = tValues.getInt(0);
                    }


                }

                Weather w1 = new Weather(t,validTime,tccMean);
                MainActivity.getmWeatherObjects().add(w1);
            }
            MainActivity.getApprovedTime().setText("Approved Time: " + approvedTime);
            MainActivity.getRecyclerView().getAdapter().notifyDataSetChanged();
            MainActivity.getmInstanceActivity().saveToFile(MainActivity.getRecyclerView());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
