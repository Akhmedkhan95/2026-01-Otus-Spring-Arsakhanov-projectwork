package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Название обязательно")
    private String title;

    private String description;
}