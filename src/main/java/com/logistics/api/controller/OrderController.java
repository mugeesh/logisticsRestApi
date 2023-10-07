package com.logistics.api.controller;

import com.logistics.api.dto.OrderResponseDto;
import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.dto.TakeOrderRequestDto;
import com.logistics.api.dto.TakeOrderResponseDto;
import com.logistics.api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto orderResponse = orderService.placeOrder(orderRequestDto);
        return ResponseEntity.ok()
                .body(orderResponse);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TakeOrderResponseDto> takeOrder(@PathVariable Long id,
                                                          @RequestBody TakeOrderRequestDto takeOrderRequestDto) {
        TakeOrderResponseDto takeOrderResponseDto = orderService.takeOrder(id, takeOrderRequestDto);
        return ResponseEntity.ok().body(takeOrderResponseDto);
    }

    @GetMapping("")
    public ResponseEntity<List<OrderResponseDto>> getOrderList(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int limit) {

        List<OrderResponseDto> orderResponses = orderService.getOrderList(page, limit);
        return ResponseEntity.ok().body(orderResponses);
    }

}