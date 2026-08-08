package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SkillGroup {

    @DocumentId
    private String id;
    private String name;
    private int order;
    private List<Skill> skills;

    @Data
    @NoArgsConstructor
    public static class Skill {
        private String name;

        @PropertyName("is_accent")
        private boolean isAccent;

        private int order;
    }
}