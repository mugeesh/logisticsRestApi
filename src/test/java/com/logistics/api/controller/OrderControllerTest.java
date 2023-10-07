package com.logistics.api.controller;

import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.dto.OrderResponseDto;
import com.logistics.api.entity.Order;
import com.logistics.api.repository.OrderRepository;
import com.logistics.api.service.GoogleMapService;
import com.logistics.api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

import com.logistics.DatabaseBaseTest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends DatabaseBaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @MockBean
    private GoogleMapService googleMapService;

    @Autowired
    private MockMvc mockMvc;


    private Long orderId;

    private final String startLatitude = "22.300574";
    private final String startLongitude = "114.174732";
    private final String endLatitude = "22.3129446";
    private final String endLongitude = "22.300574";


    @BeforeEach
    void beforeEach() {
        cleanDatabase();

        when(googleMapService.isValidCoordinates(any(), any())).thenReturn(true);
        when(googleMapService.getDistanceFromMap(any())).thenReturn(10040);

        List<Object> origin = Arrays.asList(startLatitude, startLongitude);
        List<Object> destination = Arrays.asList(endLatitude, endLongitude);
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        OrderResponseDto dto = orderService.placeOrder(orderRequestDto);
        orderId = dto.getOrderId();
    }

    private void cleanDatabase() {
        orderRepository.deleteAll();
    }


    @Test
    void testPlaceOrder() throws Exception {
        String orderRequestBody = "{\n" +
                "    \"origin\": [\"" + startLatitude + "\", \"" + startLongitude + "\"],\n" +
                "    \"destination\": [\"" + endLatitude + "\", \"" + endLongitude + "\"]\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.distance").value(10040))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UNASSIGNED"));
    }


    @Test
    void testGetOrderList() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders?page=1&limit=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].distance").value(10040))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("UNASSIGNED"));

        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
    }

    @Test
    void testTakeOrder() throws Exception {
        String responseBody = """
                {
                    "status": "TAKEN"
                }""";
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/" + orderId)
                        .content(responseBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
    }
}