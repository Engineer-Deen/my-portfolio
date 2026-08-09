package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
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

    public Project createProject(Project project) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection("projects").add(project);
        String newId = future.get().getId();
        project.setId(newId);
        return project;
    }

    public void updateProject(String id, Project project) throws ExecutionException, InterruptedException {
        firestore.collection("projects").document(id).set(project).get();
    }

    public void deleteProject(String id) throws ExecutionException, InterruptedException {
        firestore.collection("projects").document(id).delete().get();
    }
}