package com.beton408.repository;


import com.beton408.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Long> {
    FaqEntity findByQuestion(String question);
    List<FaqEntity> findAll();

}
