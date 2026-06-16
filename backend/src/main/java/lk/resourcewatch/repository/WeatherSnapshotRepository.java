package lk.resourcewatch.repository;

import lk.resourcewatch.model.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherSnapshotRepository extends JpaRepository<WeatherSnapshot, Long> {

    // Get the most recent weather snapshot
    Optional<WeatherSnapshot> findTopByOrderByFetchedAtDesc();
}