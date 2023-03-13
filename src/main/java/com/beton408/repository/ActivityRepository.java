package com.beton408.repository;

import com.beton408.entity.ActivityEntity;
import com.beton408.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long>, JpaSpecificationExecutor<ActivityEntity> {
    Page<ActivityEntity> findAll(Specification<ActivityEntity> spec, Pageable paging);

}
