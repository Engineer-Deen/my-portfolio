package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.Project;
import com.myportfolio.portfolio.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getProjects(@RequestParam(required = false) String category)
            throws ExecutionException, InterruptedException {

        List<Project> projects = projectService.fetchActiveProjects();

        if (category != null && !category.isBlank()) {
            return projects.stream()
                    .filter(p -> category.equals(p.getCategory()))
                    .toList();
        }
        return projects;
    }
}