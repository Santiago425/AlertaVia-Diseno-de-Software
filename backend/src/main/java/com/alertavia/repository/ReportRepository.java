package com.alertavia.repository;

import com.alertavia.model.Report;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {

    List<Report> findByCitizenIdOrderByCreatedAtDesc(String citizenId);

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    long countByStatus(ReportStatus status);

    long countByType(ReportType type);
}
