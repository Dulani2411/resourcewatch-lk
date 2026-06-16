package lk.resourcewatch.controller;

import lk.resourcewatch.service.PowerStressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/power")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PowerStressController {

    private final PowerStressService powerStressService;

    public PowerStressController(PowerStressService powerStressService) {
        this.powerStressService = powerStressService;
    }

    // GET http://localhost:8080/api/power/stress
    @GetMapping("/stress")
    public ResponseEntity<Map<String, Object>> getStress() {
        return ResponseEntity.ok(powerStressService.calculateStress());
    }
}