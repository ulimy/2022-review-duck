package com.reviewduck.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.reviewduck.dto.request.AnswerRequest;
import com.reviewduck.dto.request.QuestionRequest;
import com.reviewduck.dto.request.ReviewFormCreateRequest;
import com.reviewduck.dto.request.ReviewRequest;
import com.reviewduck.dto.response.ReviewFormCreateResponse;
import com.reviewduck.dto.response.ReviewFormResponse;
import com.reviewduck.dto.response.ReviewResponse;
import com.reviewduck.dto.response.ReviewsFindResponse;

public class ReviewAcceptanceTest extends AcceptanceTest {

    private final String invalidCode = "aaaaaaaa";
    private final Long invalidReviewId = 99L;

    @Test
    @DisplayName("회고를 생성한다.")
    void createReview() {
        // given
        String reviewTitle = "title";
        List<QuestionRequest> questions = List.of(new QuestionRequest("question1"),
            new QuestionRequest("question2"));
        String code = createReviewFormAndGetCode(reviewTitle, questions);

        // when, then
        // 질문조회
        assertReviewTitleFromFoundReviewForm(code, reviewTitle);

        // 리뷰생성
        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        post("/api/review-forms/" + code, createRequest)
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("존재하지 않는 질문에 대해 답변을 작성하면 회고 작성에 실패한다.")
    void failToCreateReview() {
        // given
        String reviewTitle = "title";
        List<QuestionRequest> questions = List.of(new QuestionRequest("question1"));
        String code = createReviewFormAndGetCode(reviewTitle, questions);

        // when, then
        // 질문조회
        assertReviewTitleFromFoundReviewForm(code, reviewTitle);

        // 리뷰생성
        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        post("/api/review-forms/" + code, createRequest)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("특정 회고 폼에 속한 회고 전체를 조회한다.")
    void findReviews() {
        // given
        String reviewTitle = "title";
        List<QuestionRequest> questions = List.of(new QuestionRequest("question1"),
            new QuestionRequest("question2"));
        String code = createReviewFormAndGetCode(reviewTitle, questions);

        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        post("/api/review-forms/" + code, createRequest);

        // when
        ReviewsFindResponse response = get("/api/review-forms/" + code + "/reviews")
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ReviewsFindResponse.class);

        ReviewResponse reviewResponse = response.getReviews().get(0);

        // then
        assertAll(
            () -> assertThat(response.getReviewFormTitle()).isEqualTo(reviewTitle),
            () -> assertThat(reviewResponse.getNickname()).isEqualTo("제이슨"),
            () -> assertThat(reviewResponse.getAnswers()).hasSize(2)
        );
    }

    @Test
    @DisplayName("존재하지 회고 폼 코드에 대해 회고를 조회할 수 없다.")
    void findReviewsWithInvalidCode() {
        // when, then
        get("/api/review-forms/" + invalidCode + "/reviews")
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("회고를 수정한다.")
    void editReview() {
        Long reviewId = saveReviewAndGetId();

        //when, then
        ReviewRequest editRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "editedAnswer1"), new AnswerRequest(2L, "editedAnswer2")));

        put("/api/reviews/" + reviewId, editRequest)
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 회고를 수정할 수 없다.")
    void failToEditReview() {
        // when, then
        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        put("/api/reviews/" + invalidReviewId, createRequest)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("회고를 삭제한다.")
    void deleteReview() {
        // given
        String reviewTitle = "title";
        List<QuestionRequest> questions = List.of(new QuestionRequest("question1"),
            new QuestionRequest("question2"));
        String code = createReviewFormAndGetCode(reviewTitle, questions);
        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        post("/api/review-forms/" + code, createRequest);

        ReviewsFindResponse response = get("/api/review-forms/" + code + "/reviews")
            .extract()
            .as(ReviewsFindResponse.class);

        Long reviewId = saveReviewAndGetId();

        //when, then
        delete("/api/reviews/" + reviewId)
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 회고를 삭제할 수 없다.")
    void failToDeleteReview() {
        // when, then
        delete("/api/reviews/" + invalidReviewId)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private String createReviewFormAndGetCode(String reviewTitle, List<QuestionRequest> questions) {
        // given
        ReviewFormCreateRequest request = new ReviewFormCreateRequest(reviewTitle, questions);

        // when, then
        return post("/api/review-forms", request)
            .extract()
            .as(ReviewFormCreateResponse.class)
            .getReviewFormCode();
    }

    private void assertReviewTitleFromFoundReviewForm(String code, String reviewTitle) {
        ReviewFormResponse reviewFormResponse = get("/api/review-forms/" + code)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ReviewFormResponse.class);
        assertThat(reviewFormResponse.getReviewTitle()).isEqualTo(reviewTitle);
    }

    private Long saveReviewAndGetId() {
        String reviewTitle = "title";
        List<QuestionRequest> questions = List.of(new QuestionRequest("question1"),
            new QuestionRequest("question2"));
        String code = createReviewFormAndGetCode(reviewTitle, questions);
        ReviewRequest createRequest = new ReviewRequest("제이슨",
            List.of(new AnswerRequest(1L, "answer1"), new AnswerRequest(2L, "answer2")));
        post("/api/review-forms/" + code, createRequest);

        return get("/api/review-forms/" + code + "/reviews")
            .extract()
            .as(ReviewsFindResponse.class).getReviews()
            .get(0)
            .getReviewId();
    }
}
