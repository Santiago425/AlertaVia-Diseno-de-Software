package com.alertavia.repository;

import com.alertavia.model.MobilityEntity;
import com.alertavia.model.Report;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByCitizenIdOrderByCreatedAtDesc(Long citizenId);

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    long countByStatus(ReportStatus status);

    long countByType(ReportType type);

    @Query("select m from MobilityEntity m order by m.name")
    List<MobilityEntity> findMobilityEntities();

    @Query("select m from MobilityEntity m where m.id = :id")
    Optional<MobilityEntity> findMobilityEntityById(Long id);
}
