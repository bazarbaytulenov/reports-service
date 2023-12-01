package kz.project.reportsservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.project.reportsservice.data.dto.MessageDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
public class ReportsController {
    private final ReportService service;


    @PostMapping(value = "/get", consumes = {APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @Operation(description = "Метод для получения списков документов ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные получены успешно",
                    content = {
                            @Content(
                                    mediaType = "application/json",

                                    schema = @Schema(implementation = ResponseDto.class))
                    }),


            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервиса",
                    content = {
                            @Content(
                                    mediaType = "application/json",

                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })

    public ResponseEntity<ResponseDto> getReportAsync(@RequestBody MessageDto dto) {
        try {
            return ResponseEntity.ok(service.getReport(dto));
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

    @PostMapping(value = "/getAsync", consumes = {APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @Operation(description = "Метод для получения списков документов ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные получены успешно",
                    content = {
                            @Content(
                                    mediaType = "application/json",

                                    schema = @Schema(implementation = ResponseDto.class))
                    }),


            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервиса",
                    content = {
                            @Content(
                                    mediaType = "application/json",

                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })

    public ResponseEntity<ResponseDto> getReport(@RequestBody Integer requestId) {


        return ResponseEntity.ok(service.getAsyncReport(requestId));


    }
}
