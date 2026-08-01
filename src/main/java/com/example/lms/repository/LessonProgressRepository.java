package com.example.lms.repository;

import com.example.lms.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByStudentId(Long studentId);
    boolean existsByStudentIdAndLessonId(Long studentId, Long lessonId);
    int countByStudentIdAndLessonCourseId(Long studentId, Long courseId);
}