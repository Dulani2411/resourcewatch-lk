package lk.resourcewatch.service;

import lk.resourcewatch.model.OutageReport;
import lk.resourcewatch.repository.OutageReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutageReportService {

    private final OutageReportRepository repository;

    public OutageReportService(OutageReportRepository repository) {
        this.repository = repository;
    }

    // Submit a new outage report
    public OutageReport submitReport(OutageReport report) {
        report.setReportedAt(LocalDateTime.now());
        report.setIsResolved(false);
        return repository.save(report);
    }

    // Get all reports
    public List<OutageReport> getAllReports() {
        return repository.findAll();
    }

    // Get reports by district
    public List<OutageReport> getByDistrict(String district) {
        return repository.findByDistrictOrderByReportedAtDesc(district);
    }

    // Get reports by type
    public List<OutageReport> getByType(String type) {
        return repository.findByIssueTypeOrderByReportedAtDesc(type);
    }

    // Get district risk summary for heatmap
    // Returns each district with report counts for last 24 hours
    public List<Map<String, Object>> getDistrictSummary() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Object[]> results = repository.countByDistrictAndTypeSince(since);

        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("district", row[0]);
            map.put("issueType", row[1]);
            map.put("count", row[2]);
            return map;
        }).toList();
    }

    // Count recent water reports (used by WaterRiskService)
    public long countRecentWaterReports() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return repository.countByIssueTypeAndReportedAtAfter("WATER", since);
    }

    // Count recent power reports (used by PowerStressService)
    public long countRecentPowerReports() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return repository.countByIssueTypeAndReportedAtAfter("POWER", since);
    }
}