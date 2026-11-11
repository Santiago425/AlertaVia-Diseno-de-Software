package com.alertavia.integration;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CloudStorageClient {

    private static final String FOLDER = "alertavia";

    private final RestClient restClient = RestClient.create();
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final Path localDir;
    private final String publicUrl;

    public CloudStorageClient(@Value("${app.cloudinary.cloud-name}") String cloudName,
                              @Value("${app.cloudinary.api-key}") String apiKey,
                              @Value("${app.cloudinary.api-secret}") String apiSecret,
                              @Value("${app.storage.local-dir}") String localDir,
                              @Value("${app.storage.public-url}") String publicUrl) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.localDir = Path.of(localDir).toAbsolutePath();
        this.publicUrl = publicUrl;
    }

    public String upload(MultipartFile file) {
        try {
            return isCloudinaryConfigured() ? uploadToCloudinary(file) : saveLocally(file);
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo guardar la foto", ex);
        }
    }

    private boolean isCloudinaryConfigured() {
        return StringUtils.hasText(cloudName) && StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret);
    }

    private String uploadToCloudinary(MultipartFile file) throws IOException {
        long timestamp = System.currentTimeMillis() / 1000;
        String signature = sha1("folder=" + FOLDER + "&timestamp=" + timestamp + apiSecret);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("api_key", apiKey);
        body.add("timestamp", String.valueOf(timestamp));
        body.add("folder", FOLDER);
        body.add("signature", signature);

        JsonNode response = restClient.post()
                .uri("https://api.cloudinary.com/v1_1/{cloud}/image/upload", cloudName)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        if (response == null || !response.hasNonNull("secure_url")) {
            throw new IllegalStateException("Cloudinary no devolvió la URL de la foto");
        }
        return response.get("secure_url").asText();
    }

    private String saveLocally(MultipartFile file) throws IOException {
        Files.createDirectories(localDir);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String name = UUID.randomUUID() + (extension == null ? "" : "." + extension.toLowerCase());
        file.transferTo(localDir.resolve(name));
        return publicUrl + "/uploads/" + name;
    }

    private static String sha1(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
