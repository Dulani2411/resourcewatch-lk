package lk.resourcewatch.scheduler;

import lk.resourcewatch.service.WeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherScheduler {

    private final WeatherService weatherService;

    public WeatherScheduler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Runs every 3 hours automatically
    // Also runs once when the app starts
    @Scheduled(cron = "${weather.fetch.cron}")
    public void fetchWeather() {
        System.out.println("⏰ Scheduled weather fetch started...");
        weatherService.fetchAndSaveWeather();
    }

    // Fetch immediately when app starts (don't wait 3 hours)
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void fetchOnStartup() {
        System.out.println("🚀 Fetching weather on startup...");
        weatherService.fetchAndSaveWeather();
    }
}