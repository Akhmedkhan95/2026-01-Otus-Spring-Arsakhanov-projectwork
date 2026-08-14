package com.example.lms.repository;

import com.example.lms.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}