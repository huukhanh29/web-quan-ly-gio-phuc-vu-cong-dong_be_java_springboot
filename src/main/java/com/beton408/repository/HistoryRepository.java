package com.beton408.repository;

import com.beton408.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface HistoryRepository extends JpaRepository<HistoryEntity, Long>, JpaSpecificationExecutor<HistoryEntity> {
        List<HistoryEntity> findByUserId(Long userId);
        Page<HistoryEntity> findAll(Specification<HistoryEntity> spec, Pageable pageable);
        @Query("SELECT h FROM HistoryEntity h WHERE h.createdAt BETWEEN :startDate AND :endDate")
        List<HistoryEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
        @Query("SELECT DISTINCT YEAR(h.createdAt) FROM HistoryEntity h")
        List<Integer> findDistinctYear();

}

