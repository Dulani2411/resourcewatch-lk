package lk.resourcewatch.service;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.repository.WeatherSnapshotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PowerStressService {

    private final WeatherSnapshotRepository weatherRepository;
    private final OutageReportService outageReportService;

    public PowerStressService(WeatherSnapshotRepository weatherRepository,
                              OutageReportService outageReportService) {
        this.weatherRepository = weatherRepository;
        this.outageReportService = outageReportService;
    }

    public Map<String, Object> calculateStress() {
        Optional<WeatherSnapshot> latest = weatherRepository.findTopByOrderByFetchedAtDesc();
        Map<String, Object> result = new HashMap<>();

        String level = "LOW";
        Map<String, Object> factors = new HashMap<>();

        if (latest.isPresent()) {
            WeatherSnapshot w = latest.get();

            double temp = w.getTemperature() != null ? w.getTemperature().doubleValue() : 0;
            double rainfall = w.getRainfallMm() != null ? w.getRainfallMm().doubleValue() : 0;

            // HIGH stress — very hot + no rain (hydroelectric pressure)
            if (temp > 35 && rainfall < 10) {
                level = "HIGH";
            }
            // MEDIUM stress — hot or dry
            else if (temp > 32 || rainfall < 10) {
                level = "MEDIUM";
            }

            // Community power reports push level up
            long powerReports = outageReportService.countRecentPowerReports();
            if (powerReports > 15 && level.equals("LOW")) {
                level = "MEDIUM";
            } else if (powerReports > 15 && level.equals("MEDIUM")) {
                level = "HIGH";
            }

            factors.put("temperature", temp);
            factors.put("rainfall", rainfall);
            factors.put("communityReports", powerReports);
        }

        // Peak hours
        List<String> peakHours = List.of("06:00-09:00", "18:00-21:00");
        boolean isPeakNow = isPeakHour();

        result.put("level", level);
        result.put("factors", factors);
        result.put("peakHours", peakHours);
        result.put("isPeakHourNow", isPeakNow);

        return result;
    }

    private boolean isPeakHour() {
        LocalTime now = LocalTime.now();
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime morningEnd = LocalTime.of(9, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);
        LocalTime eveningEnd = LocalTime.of(21, 0);

        return (now.isAfter(morningStart) && now.isBefore(morningEnd)) ||
               (now.isAfter(eveningStart) && now.isBefore(eveningEnd));
    }
}