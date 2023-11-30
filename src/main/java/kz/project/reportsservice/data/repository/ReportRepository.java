package kz.project.reportsservice.data.repository;

import kz.project.reportsservice.data.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    Optional<ReportEntity> findByRequestId(Integer id);
}
