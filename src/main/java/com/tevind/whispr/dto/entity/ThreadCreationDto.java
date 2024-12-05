package com.tevind.whispr.dto.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadCreationDto {

    @NotBlank(message = "Thread name cannot be blank.")
    @Size(min = 5, max = 20, message = "Thread name must be between 5 and 20 characters.")
    private String threadName;
}
