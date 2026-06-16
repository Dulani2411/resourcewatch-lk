package lk.resourcewatch.service;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.repository.WeatherSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WaterRiskService {

    private final WeatherSnapshotRepository weatherRepository;
    private final OutageReportService outageReportService;

    public WaterRiskService(WeatherSnapshotRepository weatherRepository,
                            OutageReportService outageReportService) {
        this.weatherRepository = weatherRepository;
        this.outageReportService = outageReportService;
    }

    public Map<String, Object> calculateRisk() {
        Optional<WeatherSnapshot> latest = weatherRepository.findTopByOrderByFetchedAtDesc();
        Map<String, Object> result = new HashMap<>();

        int score = 0;
        Map<String, Object> factors = new HashMap<>();

        if (latest.isPresent()) {
            WeatherSnapshot w = latest.get();

            // Factor 1 — Low rainfall
            double rainfall = w.getRainfallMm() != null ? w.getRainfallMm().doubleValue() : 0;
            if (rainfall < 10) {
                score += 30;
                factors.put("lowRainfall", true);
            }

            // Factor 2 — High temperature
            double temp = w.getTemperature() != null ? w.getTemperature().doubleValue() : 0;
            if (temp > 34) {
                score += 20;
                factors.put("highTemperature", true);
            }

            // Factor 3 — Low humidity
            int humidity = w.getHumidity() != null ? w.getHumidity() : 0;
            if (humidity < 50) {
                score += 10;
                factors.put("lowHumidity", true);
            }

            factors.put("temperature", temp);
            factors.put("rainfall", rainfall);
            factors.put("humidity", humidity);
        }

        // Factor 4 — Community water reports in last 24h
        long waterReports = outageReportService.countRecentWaterReports();
        if (waterReports > 20) {
            score += 15;
        } else if (waterReports > 10) {
            score += 8;
        }
        factors.put("communityReports", waterReports);

        // Cap score at 100
        score = Math.min(score, 100);

        // Determine level
        String level;
        if (score >= 61) {
            level = "HIGH";
        } else if (score >= 31) {
            level = "MEDIUM";
        } else {
            level = "LOW";
        }

        result.put("score", score);
        result.put("level", level);
        result.put("factors", factors);

        return result;
    }
}