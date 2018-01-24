/**
 * Created by kuba on 01.01.2018
 */

package main;

import java.io.IOException;

public class MainSystem {
    public static void main(String[] args) {
        Analyser airAnalysis;
        try {
            //System.out.println((char) 27 + "[2J");            //clear screen
            CmdLineParser cmdLine = new CmdLineParser();
            cmdLine.parse(args);
            cmdLine.getJsonParser();
            JsonParser parseJson = cmdLine.getJsonParser();
            parseJson.getMeasurements();
            airAnalysis = new Analyser(parseJson);
            airAnalysis.analyse();
        }catch (IOException ex){
            if(ex.getMessage().equals("Localisation not found")){
                System.out.println("Localisation not found");
                showOptions();
                System.exit(1);
            }else if(ex.getMessage().equals("airly does not reply") || ex.getMessage().equals("No historical data")){
                System.out.println("airly does not reply");
                showOptions();
                System.exit(1);
            }
            else if(ex.getMessage().equals("wrong arguments")){
                System.out.println("Wrong arguments");
                showOptions();
                System.exit(1);
            }
            else
                showOptions();
        }
    }

    private static void showOptions(){
        System.out.println("Please enter your localisation or put arguments as:" + "\n" +
                "  -longitude=... -latitude=... " + "\n" +
                "  -id=... to get data from the specific sensor" + "\n" +
                "  to show data by the hour add -h option");
    }

}