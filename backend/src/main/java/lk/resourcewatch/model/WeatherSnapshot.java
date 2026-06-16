package lk.resourcewatch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "weather_snapshots")
public class WeatherSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "feels_like")
    private BigDecimal feelsLike;

    @Column(name = "humidity")
    private Integer humidity;

    @Column(name = "rainfall_mm")
    private BigDecimal rainfallMm;

    @Column(name = "wind_speed")
    private BigDecimal windSpeed;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;
}