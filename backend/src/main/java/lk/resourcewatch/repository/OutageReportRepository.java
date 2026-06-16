package lk.resourcewatch.repository;

import lk.resourcewatch.model.OutageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutageReportRepository extends JpaRepository<OutageReport, Long> {

    // Get all reports for a specific district
    List<OutageReport> findByDistrictOrderByReportedAtDesc(String district);

    // Get all reports by type (WATER or POWER)
    List<OutageReport> findByIssueTypeOrderByReportedAtDesc(String issueType);

    // Count reports by district and type in last 24 hours
    @Query("SELECT r.district, r.issueType, COUNT(r) as total " +
           "FROM OutageReport r " +
           "WHERE r.reportedAt >= :since " +
           "GROUP BY r.district, r.issueType " +
           "ORDER BY total DESC")
    List<Object[]> countByDistrictAndTypeSince(LocalDateTime since);

    // Count total reports since a given time
    long countByIssueTypeAndReportedAtAfter(String issueType, LocalDateTime since);
}