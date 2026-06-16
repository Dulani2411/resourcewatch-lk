package lk.resourcewatch.controller;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // GET http://localhost:8080/api/weather/latest
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest() {
        Optional<WeatherSnapshot> snapshot = weatherService.getLatest();

        if (snapshot.isPresent()) {
            return ResponseEntity.ok(snapshot.get());
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "No weather data yet. Fetching now...",
                "status", "pending"
            ));
        }
    }

    // GET http://localhost:8080/api/weather/fetch
    // Manually trigger a weather fetch (useful for testing)
    @GetMapping("/fetch")
    public ResponseEntity<?> triggerFetch() {
        weatherService.fetchAndSaveWeather();
        return ResponseEntity.ok(Map.of(
            "message", "Weather fetch triggered successfully"
        ));
    }
}