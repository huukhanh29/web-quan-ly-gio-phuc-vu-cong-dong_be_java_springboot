package com.beton408.repository;

import com.beton408.entity.FeedbackEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long>, JpaSpecificationExecutor<FeedbackEntity> {
    // Không cần định nghĩa thêm phương thức nào
    FeedbackEntity findByContent(String content);

    Page<FeedbackEntity> findAll(Specification<FeedbackEntity> spec, Pageable pageable);
    @Query("SELECT ua FROM FeedbackEntity ua WHERE ua.faq.id = :faqId")
    FeedbackEntity findByFaqId(@Param("faqId") Long faqId);
    @Transactional
    @Modifying
    @Query("DELETE FROM FeedbackEntity f WHERE f.faq IS NOT NULL")
    void deleteByFaqNotNull();
    List<FeedbackEntity> findAllByFaqNotNull();
}

