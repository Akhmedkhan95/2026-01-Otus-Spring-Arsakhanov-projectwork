package com.example.lms.service;

import com.example.lms.dto.LessonRequest;
import com.example.lms.dto.LessonResponse;
import com.example.lms.entity.Course;
import com.example.lms.entity.Lesson;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse createLesson(Long courseId, LessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(request.getOrderIndex())
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        return toResponse(savedLesson);
    }

    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}