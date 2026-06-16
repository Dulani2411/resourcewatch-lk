package lk.resourcewatch.controller;

import lk.resourcewatch.model.OutageReport;
import lk.resourcewatch.service.OutageReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class OutageReportController {

    private final OutageReportService service;

    public OutageReportController(OutageReportService service) {
        this.service = service;
    }

    // POST http://localhost:8080/api/reports
    @PostMapping
    public ResponseEntity<OutageReport> submit(@RequestBody OutageReport report) {
        OutageReport saved = service.submitReport(report);
        return ResponseEntity.ok(saved);
    }

    // GET http://localhost:8080/api/reports
    @GetMapping
    public ResponseEntity<List<OutageReport>> getAll(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String type) {

        if (district != null) {
            return ResponseEntity.ok(service.getByDistrict(district));
        }
        if (type != null) {
            return ResponseEntity.ok(service.getByType(type));
        }
        return ResponseEntity.ok(service.getAllReports());
    }

    // GET http://localhost:8080/api/districts/risk-summary
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(service.getDistrictSummary());
    }
}