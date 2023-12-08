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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Controller" ,description = "API Report Service")
public class ReportsController {
    private final ReportService service;

    @PostMapping(value = "/get")
    @Operation(description = "Метод для получения списков документов")
    public byte[] getReport(@RequestBody ReportDto dto) throws IOException, XDocReportException, JRException {
           return service.getReport(dto);

    }


    @GetMapping(value = "/getAsync/{requestId}")
    @Operation(description = "Метод для получения списков документов ")
    public ResponseEntity<Object> getReportAsync(@Parameter(name = "requestId", description = "ID запроса") @PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(service.getAsyncReport(requestId));


    }
}
