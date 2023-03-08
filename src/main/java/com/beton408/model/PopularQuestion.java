package com.beton408.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

public class PopularQuestion {
    @Id
    private Long id;

    private String question;

    private String answer;

    private Long count;

    public PopularQuestion() {
    }

    public PopularQuestion(Long id, String question, String answer, Long count) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.count = count;
    }

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

