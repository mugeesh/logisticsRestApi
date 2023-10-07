package com.logistics.api.service;

import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.dto.OrderResponseDto;
import com.logistics.api.dto.TakeOrderRequestDto;
import com.logistics.api.dto.TakeOrderResponseDto;
import com.logistics.api.entity.Order;
import com.logistics.api.enums.OrderStatus;
import com.logistics.api.exception.BadRequestException;
import com.logistics.api.exception.NotFoundException;
import com.logistics.api.exception.OrderConflictException;
import com.logistics.api.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.logistics.api.helper.ValidationUtils.validateCoordinateLength;

@Service
@Slf4j
public class OrderService {
    private final GoogleMapService googleMapService;
    private final OrderRepository orderRepository;

    private static final String ORDER_TAKEN_ERROR_MSG = "Order is already taken.";

    public OrderService(GoogleMapService googleMapService, OrderRepository orderRepository) {
        this.googleMapService = googleMapService;
        this.orderRepository = orderRepository;
    }


    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto orderRequest) {
        if (!validateCoordinateLength(orderRequest)) {
            throw new BadRequestException("Coordinates should contain exactly two strings.");
        }
        String startLatitude = (String) orderRequest.getOrigin().get(0);
        String startLongitude = (String) orderRequest.getOrigin().get(1);
        String endLatitude = (String) orderRequest.getDestination().get(0);
        String endLongitude = (String) orderRequest.getDestination().get(1);

        if (!googleMapService.isValidCoordinates(startLatitude, startLongitude)
                || !googleMapService.isValidCoordinates(endLatitude, endLongitude)) {
            throw new BadRequestException("latitude and longitude are not valid");
        }
        int distance = googleMapService.getDistanceFromMap(orderRequest);
        Order order = new Order();
        order.setStartLat(startLatitude);
        order.setStartLon(startLongitude);
        order.setEndLat(endLatitude);
        order.setEndLon(endLongitude);
        order.setDistance(distance);
        order.setStatus(OrderStatus.UNASSIGNED.name());
        order = orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .distance(order.getDistance())
                .build();
    }

    @Transactional
    public TakeOrderResponseDto takeOrder(Long orderId, TakeOrderRequestDto takeOrderRequest) {
        String response = "SUCCESS";
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new NotFoundException("Order not found.");
        }
        try {
            Order order = optionalOrder.get();
            if (!order.getStatus().equals(OrderStatus.UNASSIGNED.name())) {
                throw new OrderConflictException(ORDER_TAKEN_ERROR_MSG);
            }
            OrderStatus status = OrderStatus.valueOf(takeOrderRequest.getStatus());
            // Update order status and apply optimistic locking
            order.setStatus(status.name());
            Order savedOrder = orderRepository.saveAndFlush(order);

            // Check if the saved order has the updated status
            if (!savedOrder.getStatus().equals(status.name())) {
                throw new OrderConflictException(ORDER_TAKEN_ERROR_MSG);
            }
        } catch (OrderConflictException e) {
            throw new OrderConflictException(ORDER_TAKEN_ERROR_MSG);
        } catch (Exception e) {
            log.error("Error while taking order", e);
            response = "Some problem occurred while taking the order";
        }
        return TakeOrderResponseDto.builder().status(response).build();
    }

    public List<OrderResponseDto> getOrderList(int page, int limit) {
        // Validate page and limit parameters
        if (page < 1 || limit < 1) {
            throw new BadRequestException("Invalid page or limit value.");
        }
        // Retrieve paginated orders from the database
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        // Return the response
        List<Order> orders = orderPage.getContent();
        return orders.stream()
                .map(order -> OrderResponseDto.builder()
                        .orderId(order.getId())
                        .distance(order.getDistance())
                        .status(order.getStatus())
                        .build())
                .toList();
    }
}
