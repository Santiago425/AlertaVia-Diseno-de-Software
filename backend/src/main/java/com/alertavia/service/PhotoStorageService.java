package com.alertavia.service;

import com.alertavia.integration.CloudStorageClient;
import com.alertavia.model.Photo;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoStorageService {

    private static final int MAX_PHOTOS = 3;

    private final CloudStorageClient cloudStorageClient;

    public PhotoStorageService(CloudStorageClient cloudStorageClient) {
        this.cloudStorageClient = cloudStorageClient;
    }

    public List<Photo> store(List<MultipartFile> files) {
        List<MultipartFile> photos = files == null ? List.of() : files.stream().filter(f -> !f.isEmpty()).toList();
        if (photos.isEmpty()) {
            throw new IllegalArgumentException("Debes adjuntar al menos una foto del problema");
        }
        if (photos.size() > MAX_PHOTOS) {
            throw new IllegalArgumentException("Solo se permiten hasta " + MAX_PHOTOS + " fotos por reporte");
        }
        photos.forEach(this::validate);
        return photos.stream().map(file -> new Photo(cloudStorageClient.upload(file))).toList();
    }

    private void validate(MultipartFile file) {
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }
    }
}
