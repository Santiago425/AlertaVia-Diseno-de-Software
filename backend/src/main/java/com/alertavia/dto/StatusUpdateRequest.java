package com.alertavia.dto;

import com.alertavia.model.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "El nuevo estado es obligatorio") ReportStatus status,
        String comment) {
}
