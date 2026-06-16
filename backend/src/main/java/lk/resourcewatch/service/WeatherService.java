package lk.resourcewatch.service;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.repository.WeatherSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private final WeatherSnapshotRepository repository;
    private final WebClient webClient;

    @Value("${openweather.api-key}")
    private String apiKey;

    @Value("${openweather.base-url}")
    private String baseUrl;

    @Value("${openweather.city}")
    private String city;

    @Value("${openweather.country-code}")
    private String countryCode;

    public WeatherService(WeatherSnapshotRepository repository) {
        this.repository = repository;
        this.webClient = WebClient.builder().build();
    }

    // Called by scheduler every 3 hours
    public void fetchAndSaveWeather() {
        String url = baseUrl + "/weather?q=" + city + "," + countryCode
                + "&appid=" + apiKey + "&units=metric";

        try {
            Map response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                WeatherSnapshot snapshot = mapToSnapshot(response);
                repository.save(snapshot);
                System.out.println("✅ Weather fetched and saved: " + snapshot.getTemperature() + "°C");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch weather: " + e.getMessage());
        }
    }

    // Get latest saved weather from database
    public Optional<WeatherSnapshot> getLatest() {
        return repository.findTopByOrderByFetchedAtDesc();
    }

    // Map OpenWeather API response to our entity
    @SuppressWarnings("unchecked")
    private WeatherSnapshot mapToSnapshot(Map response) {
        WeatherSnapshot snapshot = new WeatherSnapshot();
        snapshot.setFetchedAt(LocalDateTime.now());
        snapshot.setLocation(city + ", " + countryCode);

        // Main weather data
        Map main = (Map) response.get("main");
        if (main != null) {
            snapshot.setTemperature(toBigDecimal(main.get("temp")));
            snapshot.setFeelsLike(toBigDecimal(main.get("feels_like")));
            snapshot.setHumidity(toInteger(main.get("humidity")));
        }

        // Wind data
        Map wind = (Map) response.get("wind");
        if (wind != null) {
            snapshot.setWindSpeed(toBigDecimal(wind.get("speed")));
        }

        // Rainfall data (may not exist if no rain)
        Map rain = (Map) response.get("rain");
        if (rain != null && rain.get("1h") != null) {
            snapshot.setRainfallMm(toBigDecimal(rain.get("1h")));
        } else {
            snapshot.setRainfallMm(BigDecimal.ZERO);
        }

        // Weather description
        java.util.List weatherList = (java.util.List) response.get("weather");
        if (weatherList != null && !weatherList.isEmpty()) {
            Map weatherItem = (Map) weatherList.get(0);
            snapshot.setDescription((String) weatherItem.get("description"));
        }

        return snapshot;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return new BigDecimal(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null) return 0;
        return Integer.parseInt(value.toString());
    }
}