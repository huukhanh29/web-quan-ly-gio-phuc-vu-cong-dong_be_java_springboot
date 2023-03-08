package com.beton408.repository;

import com.beton408.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface HistoryRepository extends JpaRepository<HistoryEntity, Long>, JpaSpecificationExecutor<HistoryEntity> {
        List<HistoryEntity> findByUserId(Long userId);
        Page<HistoryEntity> findAll(Specification<HistoryEntity> spec, Pageable pageable);

}

