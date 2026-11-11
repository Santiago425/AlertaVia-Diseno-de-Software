package com.alertavia.dto;

import com.alertavia.model.ReportType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres") String description,
        @NotNull(message = "El tipo de problema es obligatorio") ReportType type,
        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") Double latitude,
        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") Double longitude,
        String address) {
}
