package com.tevind.whispr.dto.responses;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto extends BaseResponseDto {

    private String path;
    private int statusCode;
}
