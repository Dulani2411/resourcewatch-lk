package lk.resourcewatch.controller;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class WeatherController {

    private final WeatherService weatherService;

    // Supported Sri Lanka cities
    private static final List<String> SUPPORTED_CITIES = List.of(
        "Colombo", "Kandy", "Galle", "Jaffna", "Negombo",
        "Kurunegala", "Anuradhapura", "Matara", "Trincomalee", "Batticaloa"
    );

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // GET http://localhost:8080/api/weather/latest
    // GET http://localhost:8080/api/weather/latest?city=Kandy
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(@RequestParam(required = false) String city) {

        if (city != null && !city.isBlank()) {
            Optional<WeatherSnapshot> snapshot = weatherService.getLatest(city);

            if (snapshot.isPresent()) {
                return ResponseEntity.ok(snapshot.get());
            } else {
                // No data yet for this city — fetch it immediately
                WeatherSnapshot fresh = weatherService.fetchAndSaveWeather(city);
                if (fresh != null) {
                    return ResponseEntity.ok(fresh);
                }
                return ResponseEntity.ok(Map.of(
                    "message", "Unable to fetch weather for " + city,
                    "status", "error"
                ));
            }
        }

        // No city given — return default (Colombo)
        Optional<WeatherSnapshot> snapshot = weatherService.getLatest();
        if (snapshot.isPresent()) {
            return ResponseEntity.ok(snapshot.get());
        }
        return ResponseEntity.ok(Map.of(
            "message", "No weather data yet. Fetching now...",
            "status", "pending"
        ));
    }

    // GET http://localhost:8080/api/weather/cities
    // Returns list of supported cities for the dropdown
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(SUPPORTED_CITIES);
    }

    // GET http://localhost:8080/api/weather/fetch
    @GetMapping("/fetch")
    public ResponseEntity<?> triggerFetch(@RequestParam(required = false) String city) {
        WeatherSnapshot result = weatherService.fetchAndSaveWeather(city != null ? city : "Colombo");
        return ResponseEntity.ok(Map.of(
            "message", "Weather fetch triggered successfully",
            "data", result != null ? result : "failed"
        ));
    }
}