package com.beton408.controller;


import com.beton408.entity.FaqEntity;
import com.beton408.model.QuestionRequest;
import com.beton408.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.beton408.security.Helpers.calculateSimilarity;
import static com.beton408.security.Helpers.createSlug;


@RestController
@RequestMapping("/faq")
@CrossOrigin(value ="*")
public class FaqController {
    @Autowired
    private FaqRepository faqRepository;

    @PostMapping("/question")
    public ResponseEntity<?> getAnswer(@RequestBody QuestionRequest request) {
        String question = request.getQuestion().toLowerCase();
        List<FaqEntity> faqList = faqRepository.findAll();
        List<Float> percSame = new ArrayList<>();
        List<Float> same = new ArrayList<>();
        for (FaqEntity faq : faqList) {
            String query = faq.getQuestion();
            String q = query.toLowerCase();
            float a = calculateSimilarity(createSlug(question),createSlug(q));
            percSame.add(a);
        }
        for (FaqEntity faq : faqList) {
            String query = faq.getQuestion();
            String q = query.toLowerCase();
            float b = calculateSimilarity(createSlug(question),createSlug(q));
            same.add(b);
            float maxSame = Collections.max(same);
            float maxPercSame = Collections.max(percSame);
            if (maxSame == maxPercSame && maxPercSame != 0 && b >= 0.5) {
                question = faq.getQuestion();
                break;
            }
        }
        FaqEntity fa = faqRepository.findByQuestion(question);
        FaqEntity faq = new FaqEntity();
        if (fa != null) {
            faq.setId(fa.getId());
            faq.setQuestion(fa.getQuestion());
            faq.setAnswer(fa.getAnswer());
            return ResponseEntity.ok(faq);
        } else {
            return new ResponseEntity<String>(new String("404"),HttpStatus.OK);
        }
    }
}
