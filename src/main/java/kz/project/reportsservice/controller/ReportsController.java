package kz.project.reportsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import kz.project.reportsservice.enums.ReportTypeEnum;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        ResponseDto report = service.getReport(dto);
        return switch (report.getType()){
            case DOC-> getResponse(report.getData(),MediaType.APPLICATION_OCTET_STREAM,"attachment;filename=download.doc");
            case PDF -> getResponse(report.getData(),MediaType.APPLICATION_PDF,"attachment;filename=download.pdf");
            case HTML -> getResponse(report.getData(),MediaType.TEXT_HTML,"attachment;filename=download.html");
            case XML -> getResponse(report.getData(),MediaType.APPLICATION_XML,"attachment;filename=download.xml");
            default -> null;
        };
    }


    @GetMapping(value = "/getAsync/{requestId}")
    @Operation(description = "Метод для получения списков документов ")
    public ResponseEntity<Object> getReportAsync(@Parameter(name = "requestId", description = "ID запроса") @PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(service.getAsyncReport(requestId));


    }
    private ResponseEntity<Resource> getPdf(byte[] pdfContent) throws Exception {
        ByteArrayResource resource = new ByteArrayResource(pdfContent);
        MediaType mediaType = MediaType.APPLICATION_PDF;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=download.pdf")
                .contentType(mediaType)
                .contentLength(pdfContent.length)
                .body(resource);
    }
    private ResponseEntity<Resource> getHtml(byte[]htmlContent){
        ByteArrayResource resource = new ByteArrayResource(htmlContent);
        MediaType mediaType = MediaType.TEXT_HTML;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=download.html")
                .contentType(mediaType)
                .contentLength(htmlContent.length)
                .body(resource);
    }

    private ResponseEntity<Resource> getXml(byte[] xmlContent){
        ByteArrayResource resource = new ByteArrayResource(xmlContent);
        MediaType mediaType = MediaType.APPLICATION_XML;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=download.xml")
                .contentType(mediaType)
                .contentLength(xmlContent.length)
                .body(resource);
    }

    private ResponseEntity<Resource> getDoc(byte []docContent){
        ByteArrayResource resource = new ByteArrayResource(docContent);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=download.doc")
                .contentType(mediaType)
                .contentLength(docContent.length)
                .body(resource);
    }

    private ResponseEntity<Resource> getResponse(byte []docContent, MediaType mediaType, String headerValue){
        ByteArrayResource resource = new ByteArrayResource(docContent);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(mediaType)
                .contentLength(docContent.length)
                .body(resource);
    }
}
