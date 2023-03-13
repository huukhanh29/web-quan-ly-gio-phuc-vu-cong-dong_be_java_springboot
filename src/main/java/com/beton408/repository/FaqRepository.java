package com.beton408.repository;


import com.beton408.entity.FaqEntity;
import com.beton408.model.PopularQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Long>, JpaSpecificationExecutor<FaqEntity> {
    FaqEntity findByQuestion(String question);

    List<FaqEntity> findAll();

    Boolean existsByQuestion(String question);

    //Tìm kiếm theo câu hỏi:
    public List<FaqEntity> findByQuestionContainingIgnoreCase(String keyword);

    //Tìm kiếm theo câu trả lời:
    public List<FaqEntity> findByAnswerContainingIgnoreCase(String keyword);

    //Tìm kiếm theo câu hỏi hoặc câu trả lời:
    public List<FaqEntity> findByQuestionContainingIgnoreCaseOrAnswerContainingIgnoreCase(String keyword1, String keyword2);

    //Xóa các câu hỏi dựa trên danh sách các ID:
    public void deleteAllByIdIn(List<Long> ids);

    //tìm kiếm phần tử có id lớn nhất
    FaqEntity findTopByOrderByIdDesc();

    List<FaqEntity> findAllByOrderByCreatedAtDesc();

    List<FaqEntity> findAllByOrderByUpdatedAtDesc();

    Page<FaqEntity> findAll(Specification<FaqEntity> spec, Pageable pageable);
    @Query("SELECT f FROM FaqEntity f WHERE f.id = :id")
    FaqEntity fetchById(@Param("id") Long id);

}
