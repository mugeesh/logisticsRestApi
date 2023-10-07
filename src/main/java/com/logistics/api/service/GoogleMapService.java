package com.logistics.api.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import com.logistics.api.dto.OrderRequestDto;
import com.logistics.api.exception.MapException;
import com.logistics.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleMapService {
    private final GeoApiContext geoApiContext;

    public GoogleMapService(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    public boolean isValidCoordinates(String latitude, String longitude) {
        try {
            LatLng coordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, coordinates).await();
            return results.length > 0;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("error checking lat & lon valida", e);
            log.error("error while getting distance", e);
            throw new MapException("Internal error on Google Maps, please contact the company");
        }
    }

    public int getDistanceFromMap(OrderRequestDto orderRequestDto) {
        int distance = 0;
        try {
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(orderRequestDto.getOrigin().get(0) + "," + orderRequestDto.getOrigin().get(1))
                    .destinations(orderRequestDto.getDestination().get(0) + "," + orderRequestDto.getDestination().get(1))
                    .mode(TravelMode.DRIVING)
                    .await();

            DistanceMatrixElement element = distanceMatrix.rows[0].elements[0];
            if (element.status == DistanceMatrixElementStatus.OK) {
                distance = (int) element.distance.inMeters;
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("error while getting distance", e);
            throw new NotFoundException("Internal error on Google Maps, please contact the company");
        }
        return distance;
    }
}
