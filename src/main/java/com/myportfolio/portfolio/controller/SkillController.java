package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.SkillGroup;
import com.myportfolio.portfolio.service.SkillGroupService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public SkillGroup createGroup(@RequestBody SkillGroup group)
            throws ExecutionException, InterruptedException {
        return skillGroupService.createGroup(group);
    }

    @PutMapping("/{id}")
    public void updateGroup(@PathVariable String id, @RequestBody SkillGroup group)
            throws ExecutionException, InterruptedException {
        skillGroupService.updateGroup(id, group);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        skillGroupService.deleteGroup(id);
    }
}