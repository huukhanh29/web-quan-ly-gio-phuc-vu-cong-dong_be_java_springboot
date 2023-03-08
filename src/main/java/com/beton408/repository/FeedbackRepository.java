package com.beton408.repository;

import com.beton408.entity.FaqEntity;
import com.beton408.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long>, JpaSpecificationExecutor<FeedbackEntity> {
    // Không cần định nghĩa thêm phương thức nào
    FeedbackEntity findByContent(String content);
    Page<FeedbackEntity> findAll(Specification<FeedbackEntity> spec, Pageable pageable);

}

