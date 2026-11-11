package com.alertavia.controller;

import com.alertavia.dto.ReportRequest;
import com.alertavia.dto.ReportResponse;
import com.alertavia.model.Citizen;
import com.alertavia.service.AuthService;
import com.alertavia.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final AuthService authService;

    public ReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse create(@Valid @RequestPart("report") ReportRequest request,
                                 @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                                 Authentication authentication) {
        Citizen citizen = authService.findCitizen(authentication.getName());
        return reportService.createReport(citizen, request, photos);
    }

    @GetMapping("/mine")
    public List<ReportResponse> myReports(Authentication authentication) {
        return reportService.getMyReports(authService.findCitizen(authentication.getName()).getId());
    }

    @GetMapping("/{id}")
    public ReportResponse getReport(@PathVariable Long id, Authentication authentication) {
        return reportService.getReport(id, authService.findByEmail(authentication.getName()));
    }
}
