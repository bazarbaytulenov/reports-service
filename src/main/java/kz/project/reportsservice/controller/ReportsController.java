package kz.project.reportsservice.controller;

import fr.opensagres.xdocreport.core.XDocReportException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Controller" ,description = "API Report Service")
public class ReportsController {
    private final ReportService service;

    @PostMapping(value = "/get")
    @Operation(description = "Метод для получения списков документов")
    public  ResponseEntity<Resource> getReport(@RequestBody ReportDto dto) throws Exception {
        ResponseEntity<Resource> pdf = getPdf(service.getReport(dto));
        return pdf;


    }


    @GetMapping(value = "/getAsync/{requestId}")
    @Operation(description = "Метод для получения списков документов ")
    public ResponseEntity<Object> getReportAsync(@Parameter(name = "requestId", description = "ID запроса") @PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(service.getAsyncReport(requestId));


    }

    private ResponseEntity<Resource> getPdf(byte[] baos) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("methods.pdf", StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);
        return ResponseEntity.ok().headers(headers)
                .body(new ByteArrayResource(baos));
    }
}
