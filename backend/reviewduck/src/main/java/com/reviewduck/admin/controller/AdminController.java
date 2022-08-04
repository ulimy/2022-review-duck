package com.reviewduck.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reviewduck.admin.dto.response.AdminMembersResponse;
import com.reviewduck.admin.dto.response.AdminReviewFormsResponse;
import com.reviewduck.admin.dto.response.AdminReviewsResponse;
import com.reviewduck.admin.service.AdminService;
import com.reviewduck.member.domain.Member;
import com.reviewduck.review.domain.Review;
import com.reviewduck.review.domain.ReviewForm;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "가입한 사용자를 전원 조회한다")
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public AdminMembersResponse findAllMembers() {

        log.info("uri={}, method = {}",
            "api/admin/members", "GET");

        List<Member> members = adminService.findAllMembers();

        return AdminMembersResponse.from(members);
    }

    @Operation(summary = "생성된 회고 폼을 모두 조회한다")
    @GetMapping("/review-forms")
    @ResponseStatus(HttpStatus.OK)
    public AdminReviewFormsResponse findAllReviewForms() {

        log.info("uri={}, method = {}",
            "api/admin/review-forms", "GET");

        List<ReviewForm> reviewForms = adminService.findAllReviewForms();

        return AdminReviewFormsResponse.from(reviewForms);
    }

    @Operation(summary = "작성된 회고를 모두 조회한다")
    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    public AdminReviewsResponse findAllReviews() {

        log.info("uri={}, method = {}",
            "api/admin/reviews", "GET");

        List<Review> reviews = adminService.findAllReviews();

        return AdminReviewsResponse.from(reviews);
    }
}
