package com.logistics.api.service;


import com.logistics.DatabaseBaseTest;
import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.dto.OrderResponseDto;
import com.logistics.api.dto.TakeOrderRequestDto;
import com.logistics.api.entity.Order;
import com.logistics.api.exception.BadRequestException;
import com.logistics.api.exception.OrderConflictException;
import com.logistics.api.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest extends DatabaseBaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private GoogleMapService googleMapService;

    @Autowired
    private OrderService orderService;


    final private String startLongitude = "114.174732";
    final private String endLatitude = "22.3129446";
    final private String endLongitude = "22.300574";

    OrderResponseDto orderResponseDto;

    @BeforeEach
    void beforeEach() {
        cleanDatabase();

        when(googleMapService.isValidCoordinates(any(), any())).thenReturn(true);

        List<Object> origin = Arrays.asList(startLongitude, startLongitude);
        List<Object> destination = Arrays.asList(endLatitude, endLongitude);
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        orderResponseDto = orderService.placeOrder(orderRequestDto);
    }

    private void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void testPlaceOrder() {
        Long orderId = orderResponseDto.getOrderId();
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Assertions.assertTrue(orderOptional.isPresent());
        Order order = orderOptional.get();
        order.setStatus("TAKEN");
        orderRepository.save(order);

        TakeOrderRequestDto takeOrderRequest = new TakeOrderRequestDto();
        takeOrderRequest.setStatus("TAKEN");

        OrderConflictException thrown = assertThrows(
                OrderConflictException.class,
                () -> orderService.takeOrder(orderId, takeOrderRequest),
                "Order is already taken."
        );
        Assertions.assertTrue(thrown.getMessage().contains("Order is already taken."));
    }

    @Test
    void testPlaceOrderWithWrongRequest() {

        List<Object> origin = Arrays.asList(123.0, startLongitude);
        List<Object> destination = Arrays.asList(endLatitude, endLongitude);
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> orderService.placeOrder(orderRequestDto),
                "Coordinates should contain exactly two strings."
        );
        Assertions.assertTrue(thrown.getMessage().contains("Coordinates should contain exactly two strings."));
    }
}