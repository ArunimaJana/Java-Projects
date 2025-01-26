import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WeatherForcast {
    private static JFrame frame;
    private static JTextField locationField;
    private static JTextArea weatherDisplay;
    private static JButton fetchButton;
    private static JLabel weatherIcon;
    private static String apiKey = "78841f92802ba3bf153b9b5408841e53"; // uniquely generated api key

    // Fetch weather data
    private static String fetchWeatherData(String city) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = "", line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            reader.close();

            JSONObject jsonnObject = (JSONObject) JSONValue.parse(response);
            JSONObject mainObj = (JSONObject) jsonnObject.get("main");
            double temperatureKelvin = (double) mainObj.get("temp");
            long humidity = (long) mainObj.get("humidity");

            double temperatureCelsius = temperatureKelvin - 273.15;

            // Retrieve weather description
            JSONArray weatherArray = (JSONArray) jsonnObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String description = (String) weather.get("description");

            // weather icon
            String iconCode = (String) weather.get("icon");
            updateWeatherIcon(iconCode);

            return "Description: " + description + "\nTemperature: " + String.format("%.2f", temperatureCelsius) + "°C\nHumidity: " + humidity + "%";
        } catch (Exception e) {
            return "Failed to fetch weather data.";
        }
    }

    // Update the weather icon based on the weather condition
    private static void updateWeatherIcon(String iconCode) {
        String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        try {
            ImageIcon icon = new ImageIcon(new URL(iconUrl));
            weatherIcon.setIcon(icon);
        } catch (Exception e) {
            weatherIcon.setIcon(null);
        }
    }

    public static void main(String[] args) {
        // Frame setup
        frame = new JFrame("Weather Forecast App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false); 

     
        frame.setLocationRelativeTo(null);

        // Panel for input and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(70, 130, 180)); // Muted blue

        JLabel cityLabel = new JLabel("Enter City Name:");
        cityLabel.setForeground(Color.WHITE);

        locationField = new JTextField(15);
        fetchButton = new JButton("Fetch Weather");
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setBackground(new Color(0, 123, 255));

        inputPanel.add(cityLabel);
        inputPanel.add(locationField);
        inputPanel.add(fetchButton);

        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridLayout(1, 2));
        weatherPanel.setBackground(new Color(135, 206, 250)); 

        
        weatherIcon = new JLabel();
        weatherIcon.setHorizontalAlignment(JLabel.CENTER);
        weatherIcon.setPreferredSize(new Dimension(150, 150));

        // Weather details 
        weatherDisplay = new JTextArea();
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        weatherDisplay.setLineWrap(true);
        weatherDisplay.setWrapStyleWord(true);
        weatherDisplay.setBackground(new Color(245, 245, 245)); 
        weatherDisplay.setMargin(new Insets(10, 10, 10, 10));

        // Add weather icon and display to the weather panel
        weatherPanel.add(weatherIcon); 
        weatherPanel.add(new JScrollPane(weatherDisplay)); 

        // Footer label
        JLabel footerLabel = new JLabel("Powered by OpenWeatherMap API", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(weatherPanel, BorderLayout.CENTER);
        frame.add(footerLabel, BorderLayout.SOUTH);

        // ActionListener for fetchButton
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = locationField.getText().trim();
                if (!city.isEmpty()) {
                    String weatherInfo = fetchWeatherData(city);
                    weatherDisplay.setText(weatherInfo);
                } else {
                    weatherDisplay.setText("Please enter a valid city name.");
                }
            }
        });

        frame.setVisible(true);
    }
}
