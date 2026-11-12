package com.alertavia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alertavia.integration.CloudStorageClient;
import com.alertavia.model.Photo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PhotoStorageServiceTest {

    @Mock
    private CloudStorageClient cloudStorageClient;

    @InjectMocks
    private PhotoStorageService photoStorageService;

    private static MultipartFile image(String name) {
        return new MockMultipartFile("photos", name, "image/jpeg", new byte[]{1, 2, 3});
    }

    @Test
    void uploadsEveryPhoto() {
        when(cloudStorageClient.upload(any())).thenReturn("https://res.cloudinary.com/alertavia/foto.jpg");

        List<Photo> photos = photoStorageService.store(List.of(image("a.jpg"), image("b.jpg")));

        assertThat(photos).hasSize(2)
                .allMatch(photo -> photo.getUrl().equals("https://res.cloudinary.com/alertavia/foto.jpg"));
    }

    @Test
    void requiresAtLeastOnePhoto() {
        assertThatThrownBy(() -> photoStorageService.store(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Debes adjuntar al menos una foto del problema");
        assertThatThrownBy(() -> photoStorageService.store(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsAtMostThreePhotos() {
        List<MultipartFile> files = List.of(image("1.jpg"), image("2.jpg"), image("3.jpg"), image("4.jpg"));

        assertThatThrownBy(() -> photoStorageService.store(files))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("hasta 3 fotos");
        verify(cloudStorageClient, never()).upload(any());
    }

    @Test
    void rejectsFilesThatAreNotImages() {
        MultipartFile pdf = new MockMultipartFile("photos", "doc.pdf", "application/pdf", new byte[]{1});

        assertThatThrownBy(() -> photoStorageService.store(List.of(pdf)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Solo se permiten archivos de imagen");
    }
}
