/**
 * Created by kuba on 24.01.2018
 */

package main;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

public class Analyser {
    private Air actual;
    private List<Air> histList;
    private JsonParser json;
    private Boolean last24H;

    Analyser(JsonParser jsonParser){
        this.json = jsonParser;
        this.actual = jsonParser.getActual();
        this.histList = jsonParser.getHistorical();
        this.last24H= jsonParser.getLast24H();
    }

    public void analyse(){
        Visualiser visualiser;
        if(last24H){
            Collections.reverse(histList);
            visualiser = new Visualiser(actual,histList);
            visualiser.show(actual);
            visualiser.visualiseHistory();
        }
        else {
            Air historical;
            AtomicInteger nOfPm10 = new AtomicInteger();
            AtomicInteger nOfPm25 = new AtomicInteger();
            AtomicInteger nOfTemp  =new AtomicInteger();
            AtomicInteger nOfH = new AtomicInteger();
            AtomicInteger nOfP = new AtomicInteger();
            AtomicInteger nOfAQI = new AtomicInteger();
            DoubleAdder pm25 = new DoubleAdder();
            DoubleAdder pm10 = new DoubleAdder();
            DoubleAdder temperature= new DoubleAdder();
            DoubleAdder humidity = new DoubleAdder();
            DoubleAdder pressure = new DoubleAdder();
            DoubleAdder airQualityIndex = new DoubleAdder();

            for(Air air: histList){
                checkForDouble(pm10, nOfPm10, air.getPm10());
                checkForDouble(pm25,nOfPm25, air.getPm25());
                checkForDouble(temperature,nOfTemp,air.getTemperature());
                checkForDouble(humidity,nOfH,air.getHumidity());
                checkForDouble(pressure,nOfP,air.getPressure());
                checkForDouble(airQualityIndex,nOfAQI,air.getAirQualityIndex());
            }

            historical = new Air(avg(pm25,nOfPm25), avg(pm10,nOfPm10), avg(temperature, nOfTemp) ,avg(humidity,nOfH), avg(pressure,nOfP), avg(airQualityIndex, nOfAQI), false);

            visualiser = new Visualiser(actual, historical);

            visualiser.show(actual);
            visualiser.show(historical);


        }
    }


    public double avg(DoubleAdder value, AtomicInteger quantity){
        double result;
        result = value.doubleValue() / quantity.intValue();
        return result;
    }

    public void checkForDouble(DoubleAdder value, AtomicInteger quantity, Double data){
        if(data!=-100){
            value.add(data);
            quantity.getAndIncrement();
        }
    }

}