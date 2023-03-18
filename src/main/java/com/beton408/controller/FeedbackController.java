package com.beton408.controller;

import com.beton408.entity.FaqEntity;
import com.beton408.entity.FeedbackEntity;
import com.beton408.entity.Notification;
import com.beton408.entity.UserEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.FeedbackRequest;
import com.beton408.model.MessageResponse;
import com.beton408.model.FeedbackDto;
import com.beton408.repository.FaqRepository;
import com.beton408.repository.FeedbackRepository;
import com.beton408.repository.NotificationRepository;
import com.beton408.repository.UserRepository;
import com.beton408.security.jwt.JwtUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;

import java.util.List;


@RestController
@RequestMapping("/feedback")
@CrossOrigin(value = "*")
public class FeedbackController {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FaqRepository faqRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    //thêm một phản hồi
    @PostMapping("/create")
    public ResponseEntity<FeedbackEntity> createFeedback(@RequestBody FeedbackRequest feedbackRequest, HttpServletRequest request) {
        //kiểm tra token
        String token = JwtUtils.resolveToken(request);
        if (token == null || !JwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //lấy ra curent user
        Claims claims = JwtUtils.getClaimsFromToken(token);
        Long currentUserId = Long.parseLong(String.valueOf(claims.get("id", Integer.class)));
        UserEntity currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("User not found"));
        // tạo feedback mới
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setContent(feedbackRequest.getContent());
        feedbackEntity.setUser(currentUser);
        if (feedbackRepository.findByContent(feedbackEntity.getContent()) != null) {
            return new ResponseEntity(new MessageResponse("WARNING: CONTENT IS ALREADY EXISTS"), HttpStatus.NO_CONTENT);
        }
        FeedbackEntity createdFeedback = feedbackRepository.save(feedbackEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }

    //tạo Dto để lấy dữ liệu
    //lấy danh sách phản hồi
    @GetMapping("/get/all")
    public Page<FeedbackDto> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long userId
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<FeedbackEntity> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                Join<FeedbackEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);
                Join<FeedbackEntity, FaqEntity> faqJoin = root.join("faq", JoinType.LEFT);
                Predicate userPredicate = criteriaBuilder.like(userJoin.get("name"), pattern);
                Predicate faqPredicate = criteriaBuilder.like(faqJoin.get("question"), pattern);
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), pattern);
                return criteriaBuilder.or(
                        criteriaBuilder.and(userPredicate, faqPredicate, contentPredicate),
                        userPredicate,
                        faqPredicate,
                        contentPredicate
                );
            });

        }

        if (userId != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("user").get("id"), userId);
            });
        }

        Page<FeedbackEntity> feedbacks = feedbackRepository.findAll(spec, paging);

        return feedbacks.map(FeedbackDto::fromEntity);
    }
    //xóa phản hồi
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable(value = "id") Long fId) {
        FeedbackEntity f = feedbackRepository.findById(fId)
                .orElseThrow(() -> new ResourceNotFoundException("Faq", "id", fId));

        feedbackRepository.delete(f);
        //tạo thông báo
        Notification notification =new Notification();
        UserEntity user = userRepository.findByUsername(f.getUser().getUsername());
        notification.setTitle("Xóa phản hồi");
        notification.setContent("Phản hồi " + f.getContent()+
                " của bạn đã bị xóa và không được phản hồi do có nội dung hỏi không hợp lý.");
        notification.setUser(user);
        notification.setStatus("Chưa đọc");
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }
    //xóa các pahnr hồi đã được trả lời
    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAllFeedback() {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAllByFaqNotNull();
        if(!feedbackEntities.isEmpty()){
            feedbackRepository.deleteByFaqNotNull();
            return ResponseEntity.ok("Deleted");
        }else{
            return new ResponseEntity(new MessageResponse("NOT FOUND"), HttpStatus.BAD_REQUEST);
        }

    }
    //trả lời một phản hồi
    @PostMapping("/reply/{id}")
    public ResponseEntity<?> repFaq(@RequestBody FaqEntity faq,
                                    @PathVariable(value = "id") Long feedId) {
        if(faqRepository.findByQuestion(faq.getQuestion()) != null){
            return new ResponseEntity(new MessageResponse("Question already exists"), HttpStatus.CONFLICT);
        }else{
            faqRepository.save(faq);
            FaqEntity faqNew = faqRepository.findTopByOrderByIdDesc();
            FeedbackEntity feedbackEntity = feedbackRepository.findById(feedId)
                    .orElseThrow(() -> new ResourceNotFoundException("Faq", "id", feedId));
            feedbackEntity.setFaq(faqNew);
            feedbackRepository.save(feedbackEntity);
            //tạo thông báo
            Notification notification =new Notification();
            UserEntity user = userRepository.findByUsername(feedbackEntity.getUser().getUsername());
            notification.setContent("Câu hỏi " + feedbackEntity.getContent()+
                    " của bạn đã được phản hồi vui lòng hỏi chatbot với từ khóa "
                    + faqNew.getQuestion() +".");
            notification.setTitle("Trả lời phản hồi");
            notification.setUser(user);
            notification.setStatus("Chưa đọc");
            notificationRepository.save(notification);
            return ResponseEntity.ok(faq);
        }
    }
}

