package lk.resourcewatch.controller;

import lk.resourcewatch.service.WaterRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/water")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class WaterRiskController {

    private final WaterRiskService waterRiskService;

    public WaterRiskController(WaterRiskService waterRiskService) {
        this.waterRiskService = waterRiskService;
    }

    // GET http://localhost:8080/api/water/risk
    @GetMapping("/risk")
    public ResponseEntity<Map<String, Object>> getRisk() {
        return ResponseEntity.ok(waterRiskService.calculateRisk());
    }
}