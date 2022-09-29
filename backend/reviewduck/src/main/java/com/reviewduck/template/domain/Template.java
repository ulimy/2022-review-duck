package com.reviewduck.template.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.reviewduck.common.domain.BaseDate;
import com.reviewduck.member.domain.Member;
import com.reviewduck.template.exception.TemplateException;
import com.reviewduck.template.service.TemplateQuestionCreateDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Template extends BaseDate {

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TemplateQuestion> questions = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @Column(nullable = false)
    private String templateTitle;
    @Column(nullable = false)
    private String templateDescription;
    @Column
    private int usedCount;

    public Template(Member member, String templateTitle, String templateDescription,
        List<TemplateQuestionCreateDto> questions) {
        validateWhenCreate(member, templateTitle, templateDescription);

        this.templateTitle = templateTitle;
        this.member = member;
        this.templateDescription = templateDescription;
        this.usedCount = 0;
        this.questions.addAll(createQuestionsFrom(questions));
        sortQuestions();
    }

    private List<TemplateQuestion> createQuestionsFrom(List<TemplateQuestionCreateDto> questions) {
        return questions.stream()
            .map(question -> new TemplateQuestion(question.getValue(), question.getDescription(), this))
            .collect(Collectors.toUnmodifiableList());
    }

    public void update(String templateTitle, String templateDescription) {
        validateWhenUpdate(templateTitle, templateDescription);

        this.templateTitle = templateTitle;
        this.templateDescription = templateDescription;
        sortQuestions();
    }

    public boolean isMine(Member member) {
        return member.equals(this.member);
    }

    private void sortQuestions() {
        int index = 0;
        for (TemplateQuestion question : questions) {
            question.setPosition(index++);
        }
    }

    private void validateWhenUpdate(String templateTitle, String templateDescription) {
        validateTitleLength(templateTitle);
        validateBlankTitle(templateTitle);
        validateNullDescription(templateDescription);
    }

    private void validateWhenCreate(Member member, String templateTitle, String templateDescription) {
        validateBlankTitle(templateTitle);
        validateTitleLength(templateTitle);
        validateNullMember(member);
        validateNullDescription(templateDescription);
    }

    private void validateNullMember(Member member) {
        if (Objects.isNull(member)) {
            throw new TemplateException("템플릿의 작성자가 존재해야 합니다.");
        }
    }

    private void validateTitleLength(String reviewTitle) {
        if (reviewTitle.length() > 100) {
            throw new TemplateException("템플릿의 제목은 100자를 넘을 수 없습니다.");
        }
    }

    private void validateBlankTitle(String templateTitle) {
        if (Objects.isNull(templateTitle) || templateTitle.isBlank()) {
            throw new TemplateException("템플릿의 제목은 비어있을 수 없습니다.");
        }
    }

    private void validateNullDescription(String templateDescription) {
        if (Objects.isNull(templateDescription)) {
            throw new TemplateException("템플릿의 설명 작성 중 오류가 발생했습니다.");
        }
    }
}
