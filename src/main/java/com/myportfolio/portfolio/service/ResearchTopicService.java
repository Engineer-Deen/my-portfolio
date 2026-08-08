package com.myportfolio.portfolio.service;

import com.google.api.core.ApiFuture;
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
}