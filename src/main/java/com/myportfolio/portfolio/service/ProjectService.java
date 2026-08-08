package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.myportfolio.portfolio.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectService {

    private final Firestore firestore;

    public ProjectService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<Project> fetchActiveProjects() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("projects")
                .orderBy("order")
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(Project.class))
                .filter(Project::isActive)
                .toList();
    }
}