package com.logistics.api.helper;

import com.logistics.api.dto.OrderRequestDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ValidationUtils {

    public static boolean validateCoordinateLength(OrderRequestDto orderRequest) {
        return (orderRequest.getOrigin() != null
                && isSizeTwo(orderRequest.getOrigin())
                && isParamStringValue(orderRequest.getOrigin()))
                && (orderRequest.getDestination() != null
                && isSizeTwo(orderRequest.getDestination())
                && isParamStringValue(orderRequest.getDestination()));
    }


    private boolean isSizeTwo(List<Object> reqList) {
        return reqList.size() == 2;
    }

    private boolean isParamStringValue(List<Object> reqList) {
        return reqList.stream().allMatch(String.class::isInstance);
    }
}
