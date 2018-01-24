/**
 * Created by kuba on 06.01.2018
 */

package main;


public class Air {
    private double pm25;
    private double pm10;
    private double temperature;
    private double humidity;
    private double pressure;
    private double airQualityIndex;
    private Boolean actual;
    private String hour;


    public Air(double pm25, double pm10, double temperature, double humidity, double pressure, double airQualityIndex, Boolean actual){
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.airQualityIndex = airQualityIndex;
        this.actual = actual;
    }

    public Air(String hour, double pm25, double pm10, double temperature, double humidity, double pressure, double airQualityIndex, Boolean actual){
        this.hour = hour;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.temperature = temperature;
        this.humidity=humidity;
        this.pressure = pressure;
        this.airQualityIndex = airQualityIndex;
        this.actual = actual;
    }

    @Override
    public String toString() {
        return "pollutionLevel "+" pm2.5 "+ pm25+" pm10 "+pm10+" temperature "+temperature+" humidity "+humidity+" pressure "+pressure+" airQualityIndex "+airQualityIndex;
    }

    public Boolean isActual() {
        return actual;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getHour() {
        return hour;
    }

    public double getAirQualityIndex() {
        return airQualityIndex;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPressure() {
        return pressure;
    }

    public Boolean noData(){
        return(pm25==-100 && pm10==-100 && airQualityIndex==-100);
    }
}