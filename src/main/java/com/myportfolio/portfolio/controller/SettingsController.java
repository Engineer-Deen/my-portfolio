package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.service.SettingsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> getPhoto() throws ExecutionException, InterruptedException {
        byte[] photoBytes = settingsService.getPhotoBytes();

        if (photoBytes == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = settingsService.getPhotoContentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(photoBytes);
    }

    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            settingsService.uploadPhoto(file);
            return ResponseEntity.ok(Map.of("success", true, "message", "Photo updated."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IOException | ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Upload failed. Please try again."));
        }
    }
}