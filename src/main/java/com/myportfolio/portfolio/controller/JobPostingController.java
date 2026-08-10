package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.JobPosting;
import com.myportfolio.portfolio.service.JobPostingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/postings")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping
    public List<JobPosting> getActivePostings() throws ExecutionException, InterruptedException {
        return jobPostingService.fetchActivePostings();
    }

    @GetMapping("/all")
    public List<JobPosting> getAllPostings() throws ExecutionException, InterruptedException {
        return jobPostingService.fetchAllPostings();
    }

    @PostMapping
    public JobPosting createPosting(@RequestBody JobPosting posting)
            throws ExecutionException, InterruptedException {
        return jobPostingService.createPosting(posting);
    }

    @PutMapping("/{id}")
    public void updatePosting(@PathVariable String id, @RequestBody JobPosting posting)
            throws ExecutionException, InterruptedException {
        jobPostingService.updatePosting(id, posting);
    }

    @DeleteMapping("/{id}")
    public void deletePosting(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        jobPostingService.deletePosting(id);
    }
}