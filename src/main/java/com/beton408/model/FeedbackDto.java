package com.beton408.model;
import com.beton408.entity.FaqEntity;
import com.beton408.entity.FeedbackEntity;
import com.beton408.entity.UserEntity;

import java.time.LocalDateTime;

public class FeedbackDto {
    private Long id;
    private String content;
    private String question;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FeedbackDto(Long id, String content, String question, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

//    public static FeedbackDto fromEntity(FeedbackEntity feedbackEntity) {
//        FaqEntity faq = feedbackEntity.getFaq();
//        return new FeedbackDto(
//                feedbackEntity.getId(),
//                feedbackEntity.getContent(),
//                faq != null ? faq.getQuestion() : null,
//                feedbackEntity.getCreatedAt(),
//                feedbackEntity.getUpdatedAt()
//        );
//    }
public static FeedbackDto fromEntity(FeedbackEntity feedbackEntity) {
    FaqEntity faq = feedbackEntity.getFaq();
    UserEntity user = feedbackEntity.getUser();  // Thêm đoạn code này để lấy UserEntity
    String name = user != null ? user.getName() : null;  // Lấy username từ UserEntity (nếu có)
    return new FeedbackDto(
            feedbackEntity.getId(),
            feedbackEntity.getContent(),
            faq != null ? faq.getQuestion() : null,
            name,  // Thêm username vào trong FeedbackDto
            feedbackEntity.getCreatedAt(),
            feedbackEntity.getUpdatedAt()
    );
}
}
