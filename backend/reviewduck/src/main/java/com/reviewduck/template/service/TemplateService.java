package com.reviewduck.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reviewduck.auth.exception.AuthorizationException;
import com.reviewduck.common.exception.NotFoundException;
import com.reviewduck.member.domain.Member;
import com.reviewduck.member.service.MemberService;
import com.reviewduck.template.domain.Template;
import com.reviewduck.template.dto.request.TemplateCreateRequest;
import com.reviewduck.template.dto.request.TemplateQuestionUpdateRequest;
import com.reviewduck.template.dto.request.TemplateUpdateRequest;
import com.reviewduck.template.repository.TemplateRepository;
import com.reviewduck.template.vo.TemplateSortType;

import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    private final TemplateQuestionService templateQuestionService;

    private final MemberService memberService;

    @Transactional
    public Template save(Member member, TemplateCreateRequest createRequest) {
        Template template = new Template(member,
            createRequest.getTemplateTitle(),
            createRequest.getTemplateDescription(),
            createRequest.toServiceDto());

        return templateRepository.save(template);
    }

    public Template findById(Long id) {
        return templateRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 템플릿입니다."));
    }

    public Page<Template> findAll(int page, int size, String sort) {
        String sortType = TemplateSortType.getSortBy(sort);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortType));

        return templateRepository.findAll(pageRequest);
    }

    public Page<Template> findAllBySocialId(String id, int page, int size) {
        Member member = memberService.getBySocialId(id);

        Sort sort = Sort.by(Sort.Direction.DESC, TemplateSortType.LATEST.getSortBy());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return templateRepository.findByMember(pageRequest, member);
    }

    @Transactional
    public Template update(Member member, Long id, TemplateUpdateRequest templateUpdateRequest) {
        Template template = findById(id);

        validateTemplateIsMine(template, member, "본인이 생성한 템플릿이 아니면 수정할 수 없습니다.");

        for (TemplateQuestionUpdateRequest question : templateUpdateRequest.getQuestions()) {
            templateQuestionService.saveOrUpdateQuestion(question.getId(), question.getValue(),
                question.getDescription(), template);
        }

        template.update(templateUpdateRequest.getTemplateTitle(),
            templateUpdateRequest.getTemplateDescription());
        return template;
    }

    @Transactional
    public void increaseUsedCount(Long templateId) {
        templateRepository.increaseUsedCount(templateId);
    }

    @Transactional
    public void deleteById(Member member, Long id) {
        Template template = findById(id);
        validateTemplateIsMine(template, member, "본인이 생성한 템플릿이 아니면 삭제할 수 없습니다.");

        templateRepository.delete(template);
    }

    private void validateTemplateIsMine(Template template, Member member, String message) {
        if (!template.isMine(member)) {
            throw new AuthorizationException(message);
        }
    }
}
