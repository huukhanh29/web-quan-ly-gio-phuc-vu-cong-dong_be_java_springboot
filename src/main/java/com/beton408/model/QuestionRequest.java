package com.beton408.model;

public class QuestionRequest {
    private final String question;

    public QuestionRequest(String question) {
        this.question = question;
    }
    public QuestionRequest() {
        this.question ="";
    }
    public String getQuestion() {
        return question;
    }
}
