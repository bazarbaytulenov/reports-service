package kz.project.reportsservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.opensagres.xdocreport.core.XDocReportException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Controller" ,description = "API Report Service")
public class ReportsController {
    private final ReportService service;

    @PutMapping(value = "/get", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Метод для получения списков документов")
    public ResponseEntity<ResponseDto> getReport(@RequestParam("jsonData") MultipartFile jsonData,
                                                 @RequestParam("dto") ReportDto dto) throws IOException, XDocReportException {
        try {
            return ResponseEntity.ok(service.getReport(dto, jsonData ));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new ResponseDto(null, e.getMessage(), null));
        } catch (JRException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new ResponseDto(null, e.getMessage(), null));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new ResponseDto(null, e.getMessage(), null));
        }

    }


    @GetMapping(value = "/getAsync/{requestId}")
    @Operation(description = "Метод для получения списков документов ")
    public ResponseEntity<ResponseDto> getReportAsync(@Parameter(name = "requestId", description = "ID запроса") @PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(service.getAsyncReport(requestId));


    }
}
