package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private ArrayList<Weather> mWeatherList;

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mTextView3 = itemView.findViewById(R.id.textViewCC);
        }
    }

    public WeatherAdapter(ArrayList<Weather> weathers){
        this.mWeatherList = weathers;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weatherobject,parent,false);
        WeatherViewHolder vwh = new WeatherViewHolder(v);
        return vwh;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        Weather weather = mWeatherList.get(position);
        holder.mTextView1.setText(String.valueOf(weather.getTemp()) +" °C" );
        holder.mTextView3.setText("Cloud Cover: " + weather.getTccMean() + " Octas");
        holder.mTextView2.setText(weather.getDateTime());

    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }
}
