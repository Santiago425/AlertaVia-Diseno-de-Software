package com.alertavia.service;

import com.alertavia.integration.MapsClient;
import com.alertavia.model.Location;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocationService {

    private final MapsClient mapsClient;

    public LocationService(MapsClient mapsClient) {
        this.mapsClient = mapsClient;
    }

    public Location buildLocation(double latitude, double longitude, String address) {
        if (StringUtils.hasText(address)) {
            return new Location(latitude, longitude, address.trim());
        }
        String resolved = mapsClient.reverseGeocode(latitude, longitude)
                .orElse(String.format(Locale.US, "%.6f, %.6f", latitude, longitude));
        return new Location(latitude, longitude, resolved);
    }
}
