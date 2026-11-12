package com.alertavia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.alertavia.integration.MapsClient;
import com.alertavia.model.Location;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private MapsClient mapsClient;

    @InjectMocks
    private LocationService locationService;

    @Test
    void keepsTheAddressWrittenByTheCitizen() {
        Location location = locationService.buildLocation(1.2136, -77.2811, "  Calle 18 con Carrera 25 ");

        assertThat(location.getAddress()).isEqualTo("Calle 18 con Carrera 25");
        verifyNoInteractions(mapsClient);
    }

    @Test
    void asksGoogleMapsWhenThereIsNoAddress() {
        when(mapsClient.reverseGeocode(1.2136, -77.2811)).thenReturn(Optional.of("Cl. 18 #25-10, Pasto, Nariño"));

        Location location = locationService.buildLocation(1.2136, -77.2811, null);

        assertThat(location.getAddress()).isEqualTo("Cl. 18 #25-10, Pasto, Nariño");
    }

    @Test
    void usesTheCoordinatesWhenGoogleMapsIsNotAvailable() {
        when(mapsClient.reverseGeocode(1.2136, -77.2811)).thenReturn(Optional.empty());

        Location location = locationService.buildLocation(1.2136, -77.2811, "");

        assertThat(location.getAddress()).isEqualTo("1.213600, -77.281100");
        assertThat(location.getMapUrl()).endsWith("1.2136,-77.2811");
    }
}
