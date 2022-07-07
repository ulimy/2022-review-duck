package com.reviewduck.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reviewduck.domain.Review;
import com.reviewduck.domain.ReviewForm;
import com.reviewduck.dto.request.AnswerRequest;
import com.reviewduck.dto.request.ReviewCreateRequest;
import com.reviewduck.dto.request.ReviewFormCreateRequest;
import com.reviewduck.exception.NotFoundException;

@SpringBootTest
@Transactional
public class ReviewServiceTest {

    @Autowired
    private ReviewFormService reviewFormService;

    @Autowired
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰를 저장한다.")
    void saveReview() {
        // given
        String reviewTitle = "title";
        List<String> questionValues = List.of("question1", "question2");
        ReviewFormCreateRequest createRequest = new ReviewFormCreateRequest(reviewTitle, questionValues);

        ReviewForm savedReviewForm = reviewFormService.save(createRequest);

        Long questionId1 = savedReviewForm.getQuestions().get(0).getId();
        Long questionId2 = savedReviewForm.getQuestions().get(1).getId();

        // when
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("제이슨",
            List.of(new AnswerRequest(questionId1, "answer1"), new AnswerRequest(questionId2, "answer2")));
        Review savedReview = reviewService.save(savedReviewForm.getCode(), reviewCreateRequest);

        // then
        assertAll(
            () -> assertThat(savedReview.getId()).isNotNull(),
            () -> assertThat(savedReview.getNickname()).isEqualTo("제이슨"),
            () -> assertThat(savedReview.getAnswersByQuestions()
                .get(savedReviewForm.getQuestions().get(0)).getValue()).isEqualTo("answer1")
        );
    }

    @Test
    @DisplayName("유효하지 않은 입장 코드로 리뷰를 저장할 수 없다.")
    void saveReviewWithInvalidCode() {
        // given
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));

        // when, then
        assertThatThrownBy(() -> reviewService.save("aaaaaaaa", reviewCreateRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("존재하지 않는 입장코드입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 질문 번호로 회고를 작성할 수 없다.")
    void saveReviewWithInvalidQuestionId() {
        //given
        String reviewTitle = "title";
        List<String> questionValues = List.of("question1");
        ReviewFormCreateRequest createRequest = new ReviewFormCreateRequest(reviewTitle, questionValues);

        ReviewForm savedReviewForm = reviewFormService.save(createRequest);

        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("제이슨",
            List.of(new AnswerRequest(123445L, "answer1")));

        // when, then
        assertThatThrownBy(() -> reviewService.save(savedReviewForm.getCode(), reviewCreateRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("존재하지 않는 질문입니다.");
    }

    @Test
    @DisplayName("특정 회고 폼을 기반으로 작성된 회고를 모두 조회한다.")
    void findReviewsBySpecificReviewForm() {
        // given
        String reviewTitle = "title";
        List<String> questionValues = List.of("question1", "question2");
        ReviewFormCreateRequest createRequest = new ReviewFormCreateRequest(reviewTitle, questionValues);

        ReviewForm savedReviewForm = reviewFormService.save(createRequest);

        Long questionId1 = savedReviewForm.getQuestions().get(0).getId();
        Long questionId2 = savedReviewForm.getQuestions().get(1).getId();

        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("제이슨",
            List.of(new AnswerRequest(questionId1, "answer1"), new AnswerRequest(questionId2, "answer2")));
        Review savedReview = reviewService.save(savedReviewForm.getCode(), reviewCreateRequest);

        // when
        List<Review> reviews = reviewService.findAllByCode(savedReviewForm.getCode());

        // then
        assertAll(
            () -> assertThat(reviews).hasSize(1),
            () -> assertThat(reviews.get(0).getNickname()).isEqualTo(savedReview.getNickname())
        );
    }
}