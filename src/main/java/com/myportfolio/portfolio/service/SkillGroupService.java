package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
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

    public SkillGroup createGroup(SkillGroup group) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection("skill_groups").add(group);
        String newId = future.get().getId();
        group.setId(newId);
        return group;
    }

    public void updateGroup(String id, SkillGroup group) throws ExecutionException, InterruptedException {
        firestore.collection("skill_groups").document(id).set(group).get();
    }

    public void deleteGroup(String id) throws ExecutionException, InterruptedException {
        firestore.collection("skill_groups").document(id).delete().get();
    }
}