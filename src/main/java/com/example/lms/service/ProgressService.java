package com.example.lms.service;

import com.example.lms.entity.Course;
import com.example.lms.entity.CourseEnrollment;
import com.example.lms.entity.Lesson;
import com.example.lms.entity.LessonProgress;
import com.example.lms.entity.User;
import com.example.lms.exception.AlreadyExistsException;
import com.example.lms.exception.ForbiddenException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository progressRepository;

    @Transactional
    public String enrollToCourse(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new AlreadyExistsException("Вы уже записаны на этот курс");
        }

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .build();

        enrollmentRepository.save(enrollment);
        return "Вы успешно записались на курс: " + course.getTitle();
    }

    @Transactional
    public String completeLesson(Long lessonId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));

        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), lesson.getCourse().getId())) {
            throw new ForbiddenException("Сначала запишитесь на курс, чтобы проходить уроки");
        }

        if (progressRepository.existsByStudentIdAndLessonId(student.getId(), lessonId)) {
            throw new AlreadyExistsException("Этот урок уже пройден");
        }

        LessonProgress progress = LessonProgress.builder()
                .student(student)
                .lesson(lesson)
                .build();

        progressRepository.save(progress);
        return "Урок '" + lesson.getTitle() + "' отмечен как пройденный!";
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCourseProgress(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        int totalLessons = lessonRepository.countByCourseId(courseId);
        int completedLessons = progressRepository.countByStudentIdAndLessonCourseId(student.getId(), courseId);

        int progressPercent = totalLessons == 0 ? 0 : (completedLessons * 100) / totalLessons;

        return Map.of(
                "courseId", courseId,
                "courseTitle", course.getTitle(),
                "totalLessons", totalLessons,
                "completedLessons", completedLessons,
                "progressPercent", progressPercent + "%"
        );
    }
}