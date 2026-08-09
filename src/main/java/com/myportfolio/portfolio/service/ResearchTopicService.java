package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.myportfolio.portfolio.model.ResearchTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ResearchTopicService {

    private final Firestore firestore;

    public ResearchTopicService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<ResearchTopic> fetchActiveTopics() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("research_topics")
                .orderBy("order")
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(ResearchTopic.class))
                .filter(ResearchTopic::isActive)
                .toList();
    }

    public ResearchTopic createTopic(ResearchTopic topic) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection("research_topics").add(topic);
        String newId = future.get().getId();
        topic.setId(newId);
        return topic;
    }

    public void updateTopic(String id, ResearchTopic topic) throws ExecutionException, InterruptedException {
        firestore.collection("research_topics").document(id).set(topic).get();
    }

    public void deleteTopic(String id) throws ExecutionException, InterruptedException {
        firestore.collection("research_topics").document(id).delete().get();
    }
}