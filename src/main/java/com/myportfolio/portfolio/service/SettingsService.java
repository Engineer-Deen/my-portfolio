package com.myportfolio.portfolio.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class SettingsService {

    private static final long MAX_PHOTO_BYTES = 700_000; // keeps the Firestore document safely under its 1MB limit

    private final Firestore firestore;

    public SettingsService(Firestore firestore) {
        this.firestore = firestore;
    }

    public void uploadPhoto(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        if (file.getSize() > MAX_PHOTO_BYTES) {
            throw new IllegalArgumentException("Photo is too large. Please use an image under 700KB.");
        }

        String base64 = Base64.getEncoder().encodeToString(file.getBytes());

        Map<String, Object> data = new HashMap<>();
        data.put("photo_base64", base64);
        data.put("photo_content_type", file.getContentType());

        firestore.collection("settings").document("profile").set(data).get();
    }

    public byte[] getPhotoBytes() throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection("settings").document("profile").get().get();

        if (!doc.exists() || doc.getString("photo_base64") == null) {
            return null;
        }

        return Base64.getDecoder().decode(doc.getString("photo_base64"));
    }

    public String getPhotoContentType() throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection("settings").document("profile").get().get();

        if (!doc.exists() || doc.getString("photo_content_type") == null) {
            return "image/jpeg";
        }

        return doc.getString("photo_content_type");
    }
}