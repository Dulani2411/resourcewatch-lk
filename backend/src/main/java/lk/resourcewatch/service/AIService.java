package lk.resourcewatch.service;

import lk.resourcewatch.model.WeatherSnapshot;
import lk.resourcewatch.repository.WeatherSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIService {

    private final WebClient webClient;
    private final WeatherSnapshotRepository weatherRepository;
    private final WaterRiskService waterRiskService;
    private final PowerStressService powerStressService;

    @Value("${ollama.host}")
    private String ollamaHost;

    @Value("${ollama.model}")
    private String ollamaModel;

    public AIService(WeatherSnapshotRepository weatherRepository,
                     WaterRiskService waterRiskService,
                     PowerStressService powerStressService) {
        this.webClient = WebClient.builder().build();
        this.weatherRepository = weatherRepository;
        this.waterRiskService = waterRiskService;
        this.powerStressService = powerStressService;
    }

    public Map<String, Object> ask(String question) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Build context from current conditions
            String systemPrompt = buildSystemPrompt();

            // Call Ollama API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("stream", false);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", question)
            ));

            Map response = webClient.post()
                    .uri(ollamaHost + "/api/chat")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.get("message") != null) {
                Map message = (Map) response.get("message");
                String answer = (String) message.get("content");
                result.put("answer", answer);
                result.put("status", "success");
            } else {
                result.put("answer", getFallbackAnswer(question));
                result.put("status", "fallback");
            }

        } catch (Exception e) {
            System.err.println("❌ AI service error: " + e.getMessage());
            result.put("answer", getFallbackAnswer(question));
            result.put("status", "fallback");
        }

        return result;
    }

    // Build system prompt with current live conditions
    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are ResourceWatch AI, a helpful assistant for a Sri Lanka ");
        prompt.append("resource monitoring platform. ");
        prompt.append("Give practical, concise advice in bullet points under 150 words. ");
        prompt.append("Be specific to Sri Lanka context.\n\n");
        prompt.append("Current conditions:\n");

        // Add weather data
        Optional<WeatherSnapshot> weather = weatherRepository.findTopByOrderByFetchedAtDesc();
        if (weather.isPresent()) {
            WeatherSnapshot w = weather.get();
            prompt.append("- Temperature: ").append(w.getTemperature()).append("°C\n");
            prompt.append("- Humidity: ").append(w.getHumidity()).append("%\n");
            prompt.append("- Rainfall: ").append(w.getRainfallMm()).append("mm\n");
        }

        // Add risk levels
        Map<String, Object> waterRisk = waterRiskService.calculateRisk();
        Map<String, Object> powerStress = powerStressService.calculateStress();

        prompt.append("- Water Risk: ").append(waterRisk.get("level"));
        prompt.append(" (score: ").append(waterRisk.get("score")).append("/100)\n");
        prompt.append("- Grid Stress: ").append(powerStress.get("level")).append("\n");

        return prompt.toString();
    }

    // Fallback answers when Ollama is not running
    private String getFallbackAnswer(String question) {
        String q = question.toLowerCase();

        if (q.contains("water")) {
            return "• Store emergency drinking water (at least 3 days supply)\n" +
                   "• Avoid washing vehicles and watering gardens\n" +
                   "• Fix any leaking taps immediately\n" +
                   "• Use buckets instead of running taps\n" +
                   "• Check local authority announcements for supply schedules";
        } else if (q.contains("power") || q.contains("electricity")) {
            return "• Avoid using high-power appliances during peak hours (18:00-21:00)\n" +
                   "• Charge devices during off-peak hours\n" +
                   "• Keep a torch and power bank ready\n" +
                   "• Unplug non-essential devices\n" +
                   "• Monitor CEB announcements for scheduled cuts";
        } else {
            return "• Monitor the ResourceWatch dashboard for risk updates\n" +
                   "• Store emergency water supplies\n" +
                   "• Avoid peak electricity usage (18:00-21:00)\n" +
                   "• Report outages in your area using the Report tab\n" +
                   "• Check local authority announcements regularly";
        }
    }
}