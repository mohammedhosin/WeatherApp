package com.example.weatherapp;

public class Weather {

    private double temp;
    private String DateTime;
    private int tccMean;

    public Weather(double temp, String time, int tcc) {
        this.temp = temp;
        this.DateTime = time;
        this.tccMean = tcc;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public int getTccMean() {
        return tccMean;
    }

    public void setTccMean(int tccMean) {
        this.tccMean = tccMean;
    }
}
