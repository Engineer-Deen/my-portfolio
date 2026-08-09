package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.ResearchTopic;
import com.myportfolio.portfolio.service.ResearchTopicService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResearchTopic createTopic(@RequestBody ResearchTopic topic)
            throws ExecutionException, InterruptedException {
        return researchTopicService.createTopic(topic);
    }

    @PutMapping("/{id}")
    public void updateTopic(@PathVariable String id, @RequestBody ResearchTopic topic)
            throws ExecutionException, InterruptedException {
        researchTopicService.updateTopic(id, topic);
    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        researchTopicService.deleteTopic(id);
    }
}