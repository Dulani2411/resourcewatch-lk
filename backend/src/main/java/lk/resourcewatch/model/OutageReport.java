package lk.resourcewatch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "outage_reports")
public class OutageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "issue_type", nullable = false)
    private String issueType;   // WATER or POWER

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "area")
    private String area;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "is_resolved")
    private Boolean isResolved = false;
}