package com.example.lms.repository;

import com.example.lms.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

// ИЗМЕНИЛИ: второй параметр — это тип составного ключа
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, CourseEnrollment.CourseEnrollmentId> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}