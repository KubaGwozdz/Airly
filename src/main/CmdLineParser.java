/**
 * Created by kuba on 22.01.2018
 */

package main;


import java.io.IOException;

public class CmdLineParser {
    private double longitude;
    private double latitude;
    private boolean isLng = false;
    private boolean isLat = false;
    private boolean last24H = false;
    private JsonParser readJSON;
    private int id;
    private Boolean singleSensor = false;


    public void parse (String[] args) throws IOException {
        try {
            if (args.length == 0) {
                throw new IOException();
            }
            for(String arg: args){
                if(arg.contains("-longitude=")){
                    if(arg.length()==11)
                        throw new IOException();
                    longitude= Double.parseDouble(arg.substring(11));
                    isLng = true;
                }
                if(arg.contains("-latitude=")){
                    if(arg.length()==10)
                        throw new IOException();
                    latitude= Double.parseDouble(arg.substring(10));
                    isLat = true;
                }
                if(arg.contains("-h")){
                    last24H = true;
                }
                if(arg.contains("-id=")){
                    if(arg.length()==4)
                        throw new IOException();
                    id = Integer.parseInt(arg.substring(4));
                    if(id<0)
                        throw new IOException();
                    singleSensor = true;

                }
            }
            String location = "";
            if(isLat || isLng){
                if(isLng && isLat) {
                    readJSON = new JsonParser(longitude, latitude, last24H);
                }else throw new IOException();
            }
            else if(singleSensor){
                readJSON = new JsonParser(id, last24H);
            }
            else {
                for (String arg : args) {
                    if(!arg.contains("-h")) {
                        location = location + " " + arg;
                    }
                }
                readJSON = new JsonParser(location,last24H);
                //System.out.println(location);
            }
        }
        catch (IOException ex){
            throw new IOException("wrong arguments");
        }
        catch (NumberFormatException ex){
            throw new IOException("wrong arguments");
        }
    }

    public JsonParser getJsonParser(){
        return readJSON;
    }

    public boolean isLast24H() {
        return last24H;
    }
}