import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI(){
        super("Weather App");


        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGUIComponents();
    }

    private void addGUIComponents(){

        JTextField searchTextField = new JTextField();

        searchTextField.setBounds(15,15,351,45);

        searchTextField.setFont(new Font("Dialog",Font.PLAIN, 24));

        add(searchTextField);

        JLabel weatherImage = new JLabel(loadImage("src/assets/cloudy.png"));

        weatherImage.setBounds(0,125,450,217);
        add(weatherImage);

        JLabel temperatureLabel = new JLabel("12° C");

        temperatureLabel.setBounds(0,350,450,54);
        temperatureLabel.setFont(new Font("Dialog", Font.BOLD,48));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureLabel);

        //Opis pogody
        JLabel weatherConditionLabel = new JLabel("Cloudy");

        weatherConditionLabel.setBounds(0,405,450,36);
        weatherConditionLabel.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionLabel);

        // wilgotnosc img
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //wilgotnosc tekst
        JLabel humidityLabel = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityLabel.setBounds(90,500,85,55);
        humidityLabel.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityLabel);

        //Predkosc wiatru
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        //Predkosc wiatru tekst
        JLabel windspeedLabel = new JLabel("<html><b>Windspeed</b> 100 km/h</html>");
        windspeedLabel.setBounds(310,500,85,55);
        windspeedLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedLabel);


        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = searchTextField.getText();

                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition){
                    case "Clear":
                        weatherImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Snow":
                        weatherImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                    case "Rain":
                        weatherImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureLabel.setText(temperature + "° C");

                weatherConditionLabel.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityLabel.setText("<html><b>Humidity</b><br>" + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedLabel.setText("<html><b>Windspeed</b><br>" + windspeed +  " km/h </html>");


            }
        });
        add(searchButton);


    }


    // tworzy obrazki dla komponentów GUI aplikacji
    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));

            return  new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find resource");

        return null;

    }
}
