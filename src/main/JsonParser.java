/**
 * Created by kuba on 01.01.2018
 */

package main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class JsonParser {
    private Air actual;
    private String longitude;
    private String latitude;
    private Boolean singleSensor = false;
    private String urlString;
    private List<Air> histList = new LinkedList<>();
    private Boolean last24H = false;
    private int id;

    public JsonParser(String city, Boolean last24H) throws IOException {
        Location location;
        location = new Location(city);
        location.findLocation();
        this.longitude = location.longitude;
        this.latitude = location.latitude;
        this.last24H = last24H;
    }

    public JsonParser(double lng, double lat, Boolean last24H) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("####0.00", otherSymbols);
        this.longitude = df.format(lng);
        this.latitude = df.format(lat);
        this.last24H = last24H;
    }

    public JsonParser(int id, Boolean last24H) {
        this.id = id;
        this.last24H = last24H;
        singleSensor = true;
    }

    public void setUrl() {
        if (singleSensor) {
            this.urlString = "https://airapi.airly.eu/v1/sensor/measurements?sensorId=" + id +
                    "&historyHours=24&historyResolutionHours=1&Accept: application/json";
        } else {
            this.urlString = "https://airapi.airly.eu/v1/mapPoint/measurements?latitude=" +
                    latitude + "&longitude=" + longitude + "&historyResolutionHours=24&Accept: application/json";
        }
    }

    public void getMeasurements() throws IOException {
        double pm25;
        double pm10;
        double temperature;
        double humidity;
        double pressure;
        double airQualityIndex;

        JSONObject currentAir;
        JSONArray last24H;

        //String urlKrk = ("https://airapi.airly.eu/v1/mapPoint/measurements?latitude=50.06&longitude=19.93");

        setUrl();
        JSONObject measurement = readJsonFromUrl(urlString);
        if (!(measurement.has("currentMeasurements"))) {
            throw new IOException("airly does not reply");
        }
        currentAir = measurement.getJSONObject("currentMeasurements");

        if (currentAir.has("pm25")) {
            pm25 = currentAir.getDouble("pm25");
        } else pm25 = -100;

        if (currentAir.has("pm10")) {
            pm10 = currentAir.getDouble("pm10");
        } else pm10 = -100;

        if (currentAir.has("temperature")) {
            temperature = currentAir.getDouble("temperature");
        } else temperature = -100;

        if (currentAir.has("humidity")) {
            humidity = currentAir.getDouble("humidity");
        } else humidity = -100;

        if (currentAir.has("pressure")) {
            pressure = currentAir.getDouble("pressure");
            pressure /= 100;
        } else pressure = -100;

        if (currentAir.has("airQualityIndex")) {
            airQualityIndex = currentAir.getDouble("airQualityIndex");
        } else airQualityIndex = -100;

        actual = new Air(pm25, pm10, temperature, humidity, pressure, airQualityIndex, true);
        last24H = measurement.getJSONArray("history");

        for (int hour = 0; hour < 24; hour++) {
            JSONObject atHour = last24H.getJSONObject(hour);
            JSONObject time = last24H.getJSONObject(hour);

            if (!atHour.has("measurements")) {
                throw new IOException("No historical data");
            }
            atHour = atHour.getJSONObject("measurements");

            if (atHour.has("pm10")) {
                pm10 = atHour.getDouble("pm10");
            } else pm10 = -100;

            if (atHour.has("pm25")) {
                pm25 = atHour.getDouble("pm25");
            } else pm25 = -100;

            if (atHour.has("temperature")) {
                temperature = atHour.getDouble("temperature");
            } else temperature = -100;

            if (atHour.has("humidity")) {
                humidity = atHour.getDouble("humidity");
            } else humidity = -100;

            if (atHour.has("pressure")) {
                pressure = atHour.getDouble("pressure");
            } else pressure = -10000;

            if (atHour.has("airQualityIndex")) {
                airQualityIndex = atHour.getDouble("airQualityIndex");
            } else airQualityIndex = -100;

            String fromHour = time.getString("fromDateTime");
            fromHour = fromHour.substring(11, 13);
            Air hist = new Air(fromHour, pm10, pm25, temperature, humidity, pressure / 100, airQualityIndex, false);
            histList.add(hist);

        }

    }

    public Air getActual() {
        return actual;
    }

    public List<Air> getHistorical() {
        return histList;
    }

    public Boolean getLast24H() {
        return last24H;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


//-------------------- LOCATION --------------------

    private class Location {
        private String city;
        String latitude;
        String longitude;
        Double lng;
        Double lat;

        Location(String city) {
            this.city = city;
        }

        private void fixLocationString() {
            final String[] args = city.split(" ");
            String newCity = "";
            for (String arg : args) {
                newCity = newCity + arg;
                if (arg != args[args.length - 1]) newCity += "%20";
            }
            city = newCity;
        }

        private String getLocation() {
            fixLocationString();
            String location = "https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=AIzaSyCtsPG6-Nc-I7NgzPqo9LvZF7pOXX_as1E";
            return location;
        }

        private void findLocation() throws IOException {
            InputStream is = new URL(getLocation()).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                String status = (String) json.get("status");
                if (status.equals("ZERO_RESULTS")) {
                    throw new IOException();
                }
                JSONObject geo = (JSONObject) json.getJSONArray("results").getJSONObject(0).get("geometry");
                JSONObject location = (JSONObject) geo.get("location");
                lng = (Double) location.get("lng");
                lat = (Double) location.get("lat");
                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
                otherSymbols.setDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("####0.00", otherSymbols);
                longitude = df.format(lng);
                latitude = df.format(lat);
                //System.out.println(longitude);
                //System.out.println(latitude);
            } catch (IOException ex) {
                throw new IOException("Localisation not found");
            } finally {
                is.close();
            }

        }
    }

//--------------------------------------------------


    private static JSONObject readJsonFromUrl(String urlString) throws IOException {
        String token = "e4fb07371db34865a3b09495c963217b";
        urlString = urlString + "&apikey=" + token;
        JSONObject json;

        InputStream is = new URL(urlString).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            if (json.has("errors")) {
                throw new IOException();
            }
            //System.out.println(json.getJSONObject("currentMeasurements"));
            return json;
        } catch (IOException ex) {
            throw new IOException("airly does not reply");
        } finally {
            is.close();
        }
    }

}