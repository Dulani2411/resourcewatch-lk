package lk.resourcewatch.controller;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class WeatherController {

    private final WeatherService weatherService;

    // All 25 official Sri Lankan districts, grouped by province,
    // each mapped to its district-capital city (used for weather lookup)
    private static final Map<String, Map<String, String>> PROVINCE_DISTRICTS = new LinkedHashMap<>();

    static {
        Map<String, String> western = new LinkedHashMap<>();
        western.put("Colombo", "Colombo");
        western.put("Gampaha", "Gampaha");
        western.put("Kalutara", "Kalutara");
        PROVINCE_DISTRICTS.put("Western", western);

        Map<String, String> central = new LinkedHashMap<>();
        central.put("Kandy", "Kandy");
        central.put("Matale", "Matale");
        central.put("Nuwara Eliya", "Nuwara Eliya");
        PROVINCE_DISTRICTS.put("Central", central);

        Map<String, String> southern = new LinkedHashMap<>();
        southern.put("Galle", "Galle");
        southern.put("Matara", "Matara");
        southern.put("Hambantota", "Hambantota");
        PROVINCE_DISTRICTS.put("Southern", southern);

        Map<String, String> northern = new LinkedHashMap<>();
        northern.put("Jaffna", "Jaffna");
        northern.put("Kilinochchi", "Kilinochchi");
        northern.put("Mannar", "Mannar");
        northern.put("Vavuniya", "Vavuniya");
        northern.put("Mullaitivu", "Mullaitivu");
        PROVINCE_DISTRICTS.put("Northern", northern);

        Map<String, String> eastern = new LinkedHashMap<>();
        eastern.put("Trincomalee", "Trincomalee");
        eastern.put("Batticaloa", "Batticaloa");
        eastern.put("Ampara", "Ampara");
        PROVINCE_DISTRICTS.put("Eastern", eastern);

        Map<String, String> northWestern = new LinkedHashMap<>();
        northWestern.put("Kurunegala", "Kurunegala");
        northWestern.put("Puttalam", "Puttalam");
        PROVINCE_DISTRICTS.put("North Western", northWestern);

        Map<String, String> northCentral = new LinkedHashMap<>();
        northCentral.put("Anuradhapura", "Anuradhapura");
        northCentral.put("Polonnaruwa", "Polonnaruwa");
        PROVINCE_DISTRICTS.put("North Central", northCentral);

        Map<String, String> uva = new LinkedHashMap<>();
        uva.put("Badulla", "Badulla");
        uva.put("Monaragala", "Monaragala");
        PROVINCE_DISTRICTS.put("Uva", uva);

        Map<String, String> sabaragamuwa = new LinkedHashMap<>();
        sabaragamuwa.put("Ratnapura", "Ratnapura");
        sabaragamuwa.put("Kegalle", "Kegalle");
        PROVINCE_DISTRICTS.put("Sabaragamuwa", sabaragamuwa);
    }

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(@RequestParam(required = false) String city) {

        if (city != null && !city.isBlank()) {
            Optional<WeatherSnapshot> snapshot = weatherService.getLatest(city);

            if (snapshot.isPresent()) {
                return ResponseEntity.ok(snapshot.get());
            } else {
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

        Optional<WeatherSnapshot> snapshot = weatherService.getLatest();
        if (snapshot.isPresent()) {
            return ResponseEntity.ok(snapshot.get());
        }
        return ResponseEntity.ok(Map.of(
            "message", "No weather data yet. Fetching now...",
            "status", "pending"
        ));
    }

    // GET /api/weather/provinces
    // Returns { "Western": ["Colombo","Gampaha","Kalutara"], "Central": [...], ... }
    @GetMapping("/provinces")
    public ResponseEntity<Map<String, List<String>>> getProvinces() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        PROVINCE_DISTRICTS.forEach((province, districts) ->
            result.put(province, new ArrayList<>(districts.keySet()))
        );
        return ResponseEntity.ok(result);
    }

    // Kept for backward compatibility — flat list of all district capitals
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> all = new ArrayList<>();
        PROVINCE_DISTRICTS.values().forEach(d -> all.addAll(d.values()));
        return ResponseEntity.ok(all);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> triggerFetch(@RequestParam(required = false) String city) {
        WeatherSnapshot result = weatherService.fetchAndSaveWeather(city != null ? city : "Colombo");
        return ResponseEntity.ok(Map.of(
            "message", "Weather fetch triggered successfully",
            "data", result != null ? result : "failed"
        ));
    }
}