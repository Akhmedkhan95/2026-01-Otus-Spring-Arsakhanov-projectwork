package com.example.lms.controller;

import com.example.lms.dto.CourseRequest;
import com.example.lms.dto.CourseResponse;
import com.example.lms.dto.LessonRequest;
import com.example.lms.dto.LessonResponse;
import com.example.lms.service.CourseService;
import com.example.lms.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @PostMapping("/{courseId}/lessons")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LessonResponse> createLesson(@PathVariable Long courseId,
                                                       @Valid @RequestBody LessonRequest request) {
        // ПРОВЕРКА: только владелец курса может добавлять уроки
        courseService.checkCourseOwnership(courseId);
        return ResponseEntity.ok(lessonService.createLesson(courseId, request));
    }
}