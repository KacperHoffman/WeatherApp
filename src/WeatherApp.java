//pobranie aktualnych danych pogodowych z Api

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {

    public static JSONObject getWeatherData(String locationName){
        // get - koordynatów lokalizacji z użyciem API
        JSONArray locationData = getLocationData(locationName);

        //pobranie szer. i wys. geograficznej miejsca
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{

            HttpURLConnection connection = fetchApiResponse(urlString);

            if(connection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }


            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());

            while(scanner.hasNext()){

                resultJson.append(scanner.nextLine());

            }

            scanner.close();

            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // temperatura
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            //Zwracamy obiekt JSON z danymi dla GUI aplikacji
            return weatherData;

        }catch (Exception e){
            e.printStackTrace();

        }

        return null;
    }

    public static JSONArray getLocationData(String locationName){
        //formatowanie do formatu API
        locationName = locationName.replaceAll(" ", "+");

        // utworzenie endpointu API
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            //zawolanie API
            HttpURLConnection connection = fetchApiResponse(urlString);

            if(connection.getResponseCode() != 200){

                System.out.println("Error: could not connect to API");

                return null;

            }else{

                StringBuilder resultJson = new StringBuilder();

                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObject.get("results");

                return locationData;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{

            URL url = new URL(urlString);

            //url.openConnection(): Ta metoda otwiera połączenie z określonym adresem URL. Obiekt URL (url) reprezentuje zasób,
            // z którym chcesz się połączyć.
            //(HttpURLConnection): To jest rzutowanie typu. Rzutuje zwrócony obiekt URLConnection na HttpURLConnection,
            // co pozwala na użycie metod i właściwości specyficznych dla protokołu HTTP
            // udostępnianych przez HttpURLConnection.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
            return connection;

        }catch (IOException e){
            e.printStackTrace();
        }
        // Błąd fetcha
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++){
           String time = (String) timeList.get(i);

           if(time.equalsIgnoreCase(currentTime)){
               return i;
           }
        }

        return 0;
    }

    public static String getCurrentTime(){

        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";

        if (weatherCode == 0L){
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L)
                        || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }

        return  weatherCondition;
    }

}
