/**
 * Created by kuba on 07.01.2018
 */

package main;


import javafx.scene.layout.Background;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;


public class Visualiser {
    private Air actual;
    private Air historical;
    private List<Air> histList;
    private static final int pm10Max  = 50;
    private static final int pm25Max  = 25;
    private static final int airQIMax = 25;

    private static final String RESET = "\u001B[0m";
    private static final String HIGH_INTENSITY = "\u001B[1m";

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";

    private static final String RED_BRIGHT = "\033[0;91m";    // RED
    private static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    private static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW


    public Visualiser(Air actual, Air historical) {
        this.actual = actual;
        this.historical = historical;
    }

    public Visualiser(Air actual, List<Air> histList){
        this.actual = actual;
        this.histList = histList;
    }

    private String format(double data){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat df = new DecimalFormat("####0.00", otherSymbols);
        return df.format(data);
    }

    private String setColor(double data, double acceptableLvl, double rate1, double rate2, double rate3, double rate4) {
        String air;
        if (data < acceptableLvl) {
            air = GREEN_BRIGHT + format(data);
        } else if (data < rate1) {
            air = GREEN + format(data);
        } else if (data < rate2) {
            air = YELLOW_BRIGHT + format(data);
        } else if (data < rate3) {
            air = YELLOW + format(data);
        } else if (data < rate4) {
            air = RED_BRIGHT + format(data);
        } else {
            air = RED + format(data);
        }
        return air;
    }

    public void show(Air air) {
        if(air.isActual()){
            System.out.println(HIGH_INTENSITY + "         Actual air quality:" + RESET);
        }else {
            System.out.println(HIGH_INTENSITY + "       Last 24H air quality:" + RESET);
        }
        visualise(air);
    }

    private String makeGap(int length){
        String gap="";
        for(int i=0;i+length<25;i++){
            gap+=" ";
        }
        return gap;
    }

    private void visualise(Air air){
        int percent;
        String colouredGraph;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat df = new DecimalFormat("####0.00", otherSymbols);
        String colouredData;

        if(air.getAirQualityIndex()!=-100) {
            percent = getPercent(air.getAirQualityIndex(), airQIMax);
            colouredGraph = createGraph(percent);
            colouredData = setColor(air.getAirQualityIndex(), airQIMax, 1.5 * airQIMax, 2 * airQIMax, 2.5 * airQIMax, 3 * airQIMax);
            //System.out.println(colouredData.length());
            System.out.println(" Air quality index:  " + RESET + colouredData + makeGap(format(air.getAirQualityIndex()).length()) +
                      colouredGraph + RESET + " " + percent + '%');
        }
        if(air.getPm25()!=-100) {
            percent = getPercent(air.getPm25(), pm25Max);
            colouredGraph = createGraph(percent);
            colouredData = setColor(air.getPm25(), pm25Max, 36, 42, 48, 54) + " \u00b5g/m\u00b3";
            System.out.println("             pm2.5:  " + RESET + colouredData + makeGap(format(air.getPm25()).length()+6) +
                    colouredGraph + RESET + " " + percent + '%');
        }
        if (air.getPm10()!=-100) {
            percent = getPercent(air.getPm10(), pm10Max);
            colouredGraph = createGraph(percent);
            colouredData = setColor(air.getPm10(), pm10Max, 51, 59, 67, 76) + " \u00b5g/m\u00b3";
            System.out.println("              pm10:  " + RESET + colouredData + makeGap(format(air.getPm10()).length()+6) +
                   colouredGraph + RESET + " " + percent + '%' );
        }
        if(air.getTemperature()!=-100)
            System.out.println("       temperature:  " + RESET + df.format(air.getTemperature())+" "+'\u00b0'+'C'+RESET);

        if(air.getHumidity() != -100)
            System.out.println("          humidity:  " + RESET + Math.round(air.getHumidity()) + " %");


        if(air.getPressure() != -100)
            System.out.println("          pressure:  " + RESET + format(air.getPressure()) + " hPa");
        System.out.print("\n");
    }

    public void visualiseHistory(){
        System.out.println(HIGH_INTENSITY+"    History of last 24 hours: "+RESET);
        for(Air air: histList){
            if(air.noData()){
                System.out.println("                "+air.getHour()+": NO DATA");
            }
            else {
                System.out.println("                "+air.getHour()+": ");

                visualise(air);
            }
        }

    }

    private int getPercent(double quality, double norm){
        int percent;
        if(quality==-100) return -100;
        percent = (int)Math.round(quality);
        percent= (int) Math.round((100*percent/norm));
        return percent;

    }

    private String createGraph(int percent){
        if(percent==-100) return "∞";
        StringBuilder colouredGraph;
        colouredGraph = new StringBuilder();
        int visPercent = percent*2/10;// percent %101;
        for(int i=0;i<visPercent;i++){
            colouredGraph.append("█");//("■");
        }
        return colouredGraph.toString();
    }

}