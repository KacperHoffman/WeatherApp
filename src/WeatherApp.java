//pobranie aktualnych danych pogodowych z Api

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

}
