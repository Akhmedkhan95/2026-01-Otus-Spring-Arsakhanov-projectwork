package com.example.lms.controller;

import com.example.lms.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> enroll(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressService.enrollToCourse(courseId));
    }

    @PostMapping("/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> completeLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(progressService.completeLesson(lessonId));
    }

    @GetMapping("/courses/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> getProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressService.getCourseProgress(courseId));
    }
}