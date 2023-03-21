package com.beton408.controller;


import com.beton408.entity.FaqEntity;
import com.beton408.entity.FeedbackEntity;
import com.beton408.entity.HistoryEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.MessageResponse;
import com.beton408.model.QuestionRequest;
import com.beton408.repository.FaqRepository;
import com.beton408.repository.FeedbackRepository;
import com.beton408.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import static com.beton408.security.Helpers.calculateSimilarity;
import static com.beton408.security.Helpers.createSlug;

@RestController
@RequestMapping("/faq")
@CrossOrigin(value ="*")
public class FaqController {
    @Autowired
    private FaqRepository faqRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private HistoryRepository historyRepository;
    //lấy câu hỏi và đưa ra câu trả lời
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
            return new ResponseEntity<String>(new String("unknown"),HttpStatus.OK);
        }
    }
    //tạo faq mới
    @PostMapping("/create")
    public ResponseEntity<?> createFaq(@RequestBody FaqEntity faq) {
        if(faqRepository.findByQuestion(faq.getQuestion()) != null){
            return new ResponseEntity(new MessageResponse("Question already exists"), HttpStatus.CONFLICT);
        }else{
            faqRepository.save(faq);
            return ResponseEntity.ok(faq);
        }

    }
    //lấy danh sách faq
    @GetMapping("/get/all")
    public Page<FaqEntity> getAllFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<FaqEntity> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("question"), pattern)
                );
            });
        }
        return faqRepository.findAll(spec, paging);
    }

    //lấy faq theo id
    @GetMapping("/get/{id}")
    public FaqEntity getFaqById(@PathVariable(value = "id") Long faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(() -> new ResourceNotFoundException("Faq", "id", faqId));
    }
    //chỉnh sữa faq
    @PutMapping("/update/{id}")
    public FaqEntity updateFaq(@PathVariable(value = "id") Long faqId,
                               @RequestBody FaqEntity faqDetails) {
        FaqEntity faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new ResourceNotFoundException("Faq", "id", faqId));

        faq.setQuestion(faqDetails.getQuestion());
        faq.setAnswer(faqDetails.getAnswer());

        return faqRepository.save(faq);
    }
    //xóa faq
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable(value = "id") Long faqId) {

        FaqEntity faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new ResourceNotFoundException("Faq", "id", faqId));

        FeedbackEntity feedback = feedbackRepository.findByFaqId(faqId);
        HistoryEntity historyEntity = historyRepository.findByFaqId(faqId);
        if(feedback!= null || historyEntity != null){
            return new ResponseEntity(new MessageResponse("IS USE"), HttpStatus.BAD_REQUEST);
        }
        faqRepository.delete(faq);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/count")
    public Long countFaq() {
        return faqRepository.count();
    }
}
