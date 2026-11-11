package com.alertavia.repository;

import com.alertavia.model.StatusChange;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusChangeRepository extends JpaRepository<StatusChange, Long> {

    List<StatusChange> findByReportIdOrderByChangedAtAsc(Long reportId);
}
