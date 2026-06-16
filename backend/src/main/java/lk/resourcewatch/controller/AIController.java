package lk.resourcewatch.controller;

import lk.resourcewatch.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    // POST http://localhost:8080/api/ai/ask
    // Body: { "question": "What should I do during high water risk?" }
    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, String> body) {
        String question = body.get("question");

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Question cannot be empty"
            ));
        }

        Map<String, Object> response = aiService.ask(question);
        return ResponseEntity.ok(response);
    }
}