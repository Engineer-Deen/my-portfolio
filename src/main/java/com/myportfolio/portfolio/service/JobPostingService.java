package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.myportfolio.portfolio.model.JobPosting;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class JobPostingService {

    private final Firestore firestore;

    public JobPostingService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<JobPosting> fetchActivePostings() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("job_postings")
                .orderBy("order")
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(JobPosting.class))
                .filter(JobPosting::isActive)
                .toList();
    }

    public List<JobPosting> fetchAllPostings() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("job_postings")
                .orderBy("order")
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(JobPosting.class))
                .toList();
    }

    public JobPosting createPosting(JobPosting posting) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection("job_postings").add(posting);
        String newId = future.get().getId();
        posting.setId(newId);
        return posting;
    }

    public void updatePosting(String id, JobPosting posting) throws ExecutionException, InterruptedException {
        firestore.collection("job_postings").document(id).set(posting).get();
    }

    public void deletePosting(String id) throws ExecutionException, InterruptedException {
        firestore.collection("job_postings").document(id).delete().get();
    }
}