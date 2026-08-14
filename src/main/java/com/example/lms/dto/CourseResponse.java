package com.example.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private TeacherInfo teacher;
    private List<LessonResponse> lessons;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {
        private Long id;
        private String username;
        private String email;
    }
}