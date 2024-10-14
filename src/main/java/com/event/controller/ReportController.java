package com.event.controller;

import com.event.service.ReportService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/event-excel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void downloadExcel(HttpServletResponse response) throws Exception {
        byte[] excelBytes = reportService.generateExcel(response);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"event_report.xls\"");
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/event-users-pdf/{eventId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void generatePdfFile(HttpServletResponse response, @PathVariable Long eventId) throws DocumentException, IOException
    {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=event_user" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        reportService.generate(eventId, response);
    }
}
