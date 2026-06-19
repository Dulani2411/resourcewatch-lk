package lk.resourcewatch.service;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.repository.WeatherSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private String defaultCity;

    @Value("${openweather.country-code}")
    private String countryCode;

    public WeatherService(WeatherSnapshotRepository repository) {
        this.repository = repository;
        this.webClient = WebClient.builder().build();
    }

    // Called by scheduler every 3 hours — uses default city (Colombo)
    public void fetchAndSaveWeather() {
        fetchAndSaveWeather(defaultCity);
    }

    // Fetch weather for ANY city
    public WeatherSnapshot fetchAndSaveWeather(String city) {
        String url = baseUrl + "/weather?q=" + city + "," + countryCode
                + "&appid=" + apiKey + "&units=metric";

        try {
            Map response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                WeatherSnapshot snapshot = mapToSnapshot(response, city);
                repository.save(snapshot);
                System.out.println("✅ Weather fetched for " + city + ": " + snapshot.getTemperature() + "°C");
                return snapshot;
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch weather for " + city + ": " + e.getMessage());
        }
        return null;
    }

    // Get latest saved weather for a specific city (or default city if none given)
    public Optional<WeatherSnapshot> getLatest(String city) {
        String location = city + ", " + countryCode;
        return repository.findTopByLocationOrderByFetchedAtDesc(location);
    }

    // Get latest saved weather (default city — used by scheduler / backward compatibility)
    public Optional<WeatherSnapshot> getLatest() {
        return repository.findTopByOrderByFetchedAtDesc();
    }

    @SuppressWarnings("unchecked")
    private WeatherSnapshot mapToSnapshot(Map response, String city) {
        WeatherSnapshot snapshot = new WeatherSnapshot();
        snapshot.setFetchedAt(LocalDateTime.now());
        snapshot.setLocation(city + ", " + countryCode);

        Map main = (Map) response.get("main");
        if (main != null) {
            snapshot.setTemperature(toBigDecimal(main.get("temp")));
            snapshot.setFeelsLike(toBigDecimal(main.get("feels_like")));
            snapshot.setHumidity(toInteger(main.get("humidity")));
        }

        Map wind = (Map) response.get("wind");
        if (wind != null) {
            snapshot.setWindSpeed(toBigDecimal(wind.get("speed")));
        }

        Map rain = (Map) response.get("rain");
        if (rain != null && rain.get("1h") != null) {
            snapshot.setRainfallMm(toBigDecimal(rain.get("1h")));
        } else {
            snapshot.setRainfallMm(BigDecimal.ZERO);
        }

        List weatherList = (List) response.get("weather");
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