package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.myportfolio.portfolio.model.SkillGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SkillGroupService {

    private final Firestore firestore;

    public SkillGroupService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<SkillGroup> fetchAllGroups() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("skill_groups")
                .orderBy("order")
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(SkillGroup.class))
                .toList();
    }
}