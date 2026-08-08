package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.ResearchTopic;
import com.myportfolio.portfolio.service.ResearchTopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/research")
public class ResearchController {

    private final ResearchTopicService researchTopicService;

    public ResearchController(ResearchTopicService researchTopicService) {
        this.researchTopicService = researchTopicService;
    }

    @GetMapping
    public List<ResearchTopic> getResearchTopics() throws ExecutionException, InterruptedException {
        return researchTopicService.fetchActiveTopics();
    }
}