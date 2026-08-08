package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.SkillGroup;
import com.myportfolio.portfolio.service.SkillGroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillGroupService skillGroupService;

    public SkillController(SkillGroupService skillGroupService) {
        this.skillGroupService = skillGroupService;
    }

    @GetMapping
    public List<SkillGroup> getSkillGroups() throws ExecutionException, InterruptedException {
        return skillGroupService.fetchAllGroups();
    }
}