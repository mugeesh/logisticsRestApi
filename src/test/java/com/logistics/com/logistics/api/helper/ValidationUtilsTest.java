package com.logistics.com.logistics.api.helper;

import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.helper.ValidationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ValidationUtilsTest {
    private String startLatitude = "22.300574";
    private String startLongitude = "114.174732";
    private String endLatitude = "22.3129446";
    private String endLongitude = "22.300574";

    @Test
    void testValidationUtils(){
        List<Object> origin = Arrays.asList(startLatitude, startLongitude);
        List<Object> destination = Arrays.asList(endLatitude, endLongitude);
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        Assertions.assertTrue(ValidationUtils.validateCoordinateLength(orderRequestDto));
    }
    @Test
    void testValidationUtilsFailed(){
        List<Object> origin = Arrays.asList(123.0, startLongitude);
        List<Object> destination = Arrays.asList(endLatitude, endLongitude);
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        Assertions.assertFalse(ValidationUtils.validateCoordinateLength(orderRequestDto));

        destination = Arrays.asList(endLatitude, null);
        orderRequestDto = OrderRequestDto.builder()
                .origin(origin)
                .destination(destination)
                .build();

        Assertions.assertFalse(ValidationUtils.validateCoordinateLength(orderRequestDto));
    }
}
