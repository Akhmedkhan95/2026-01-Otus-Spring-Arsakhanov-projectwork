package com.example.lms.service;

import com.example.lms.dto.CourseRequest;
import com.example.lms.dto.CourseResponse;
import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import com.example.lms.exception.ForbiddenException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourseResponse() {
        // Given
        Long courseId = 1L;

        User teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("Teacher");
        teacher.setEmail("teacher@example.com");

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setTeacher(teacher);
        course.setLessons(new ArrayList<>());

        when(courseRepository.findByIdWithLessons(courseId)).thenReturn(Optional.of(course));

        // When
        CourseResponse result = courseService.getCourseById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Test Course", result.getTitle());
        assertEquals("Teacher", result.getTeacher().getUsername());
        verify(courseRepository, times(1)).findByIdWithLessons(courseId);
    }

    @Test
    void getCourseById_WhenCourseNotFound_ShouldThrowException() {
        // Given
        Long courseId = 999L;
        when(courseRepository.findByIdWithLessons(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(courseId));
        verify(courseRepository, times(1)).findByIdWithLessons(courseId);
    }

    @Test
    void createCourse_WhenTeacherExists_ShouldCreateCourse() {
        // Given
        CourseRequest request = new CourseRequest();
        request.setTitle("New Course");
        request.setDescription("Description");

        User teacher = new User();
        teacher.setId(1L);
        teacher.setEmail("teacher@example.com");
        teacher.setUsername("Teacher");

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setTitle("New Course");
        savedCourse.setDescription("Description");
        savedCourse.setTeacher(teacher);
        savedCourse.setLessons(new ArrayList<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        CourseResponse result = courseService.createCourse(request);

        // Then
        assertNotNull(result);
        assertEquals("New Course", result.getTitle());
        assertEquals("Teacher", result.getTeacher().getUsername());
        verify(userRepository, times(1)).findByEmail("teacher@example.com");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void checkCourseOwnership_WhenOwnerMatches_ShouldNotThrowException() {
        // Given
        Long courseId = 1L;
        Long teacherId = 1L;

        User teacher = new User();
        teacher.setId(teacherId);
        teacher.setEmail("teacher@example.com");

        Course course = new Course();
        course.setId(courseId);
        course.setTeacher(teacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When & Then
        assertDoesNotThrow(() -> courseService.checkCourseOwnership(courseId));
    }

    @Test
    void checkCourseOwnership_WhenOwnerDoesNotMatch_ShouldThrowException() {
        // Given
        Long courseId = 1L;
        Long currentUserId = 2L;
        Long ownerId = 1L;

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setEmail("other@example.com");

        User owner = new User();
        owner.setId(ownerId);

        Course course = new Course();
        course.setId(courseId);
        course.setTeacher(owner);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other@example.com");
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(currentUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When & Then
        assertThrows(ForbiddenException.class, () -> courseService.checkCourseOwnership(courseId));
    }
}