package com.logistics.api.dto;

import lombok.*;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private int distance;
    private String status;
}