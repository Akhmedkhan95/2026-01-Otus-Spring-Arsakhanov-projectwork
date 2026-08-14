package com.example.lms.service;

import com.example.lms.dto.CourseRequest;
import com.example.lms.dto.CourseResponse;
import com.example.lms.dto.LessonResponse;
import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import com.example.lms.exception.ForbiddenException;  // ← ИЗМЕНИЛИ
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "courses")
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "course", key = "#id")
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findByIdWithLessons(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
        return toResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .build();

        Course savedCourse = courseRepository.save(course);
        return toResponse(savedCourse);
    }

    // Проверка владельца курса
    public void checkCourseOwnership(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Вы не являетесь владельцем этого курса");  // ← ИЗМЕНИЛИ
        }
    }

    // Конвертация Entity в DTO
    private CourseResponse toResponse(Course course) {
        List<LessonResponse> lessonResponses = course.getLessons() != null
                ? course.getLessons().stream()
                .map(lesson -> LessonResponse.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .content(lesson.getContent())
                        .orderIndex(lesson.getOrderIndex())
                        .createdAt(lesson.getCreatedAt())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacher(CourseResponse.TeacherInfo.builder()
                        .id(course.getTeacher().getId())
                        .username(course.getTeacher().getUsername())
                        .email(course.getTeacher().getEmail())
                        .build())
                .lessons(lessonResponses)
                .createdAt(course.getCreatedAt())
                .build();
    }
}