package com.beton408.model;
import com.beton408.entity.FaqEntity;
import com.beton408.entity.UserEntity;
import com.beton408.entity.HistoryEntity;

import java.time.LocalDateTime;

public class HistoryDto {
    private Long id;
    private String answer;
    private String question;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HistoryDto(Long id, String answer, String question, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.answer = answer;
        this.question = question;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

public static HistoryDto fromEntity(HistoryEntity historyEntity) {
    FaqEntity faq = historyEntity.getFaq();
    UserEntity user = historyEntity.getUser();  // Thêm đoạn code này để lấy UserEntity
    String name = user != null ? user.getName() : null;  // Lấy username từ UserEntity (nếu có)
    return new HistoryDto(
            historyEntity.getId(),
            faq != null ? faq.getAnswer() : null,
            faq != null ? faq.getQuestion() : null,
            name,  // Thêm username vào trong FeedbackDto
            historyEntity.getCreatedAt(),
            historyEntity.getUpdatedAt()
    );
}
}
