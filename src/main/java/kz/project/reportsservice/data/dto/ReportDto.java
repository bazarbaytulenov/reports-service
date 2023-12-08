package kz.project.reportsservice.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReportDto", description = "Запрос")
public class ReportDto {
    @NotBlank
    @Schema(name ="fileName" ,description = "Флаг для генерации отчета асинхронна")
    private String fileName;
    @NotBlank
    @Schema(name ="acync" ,description = "Флаг для генерации отчета асинхронна")
    private Boolean acync;
    @NotBlank
    @Schema(name ="inline" ,description = "Флаг для генерации отчета асинхронна")
    private Boolean inline;
    @NotBlank
    @Schema(name = "templateId",description = "Код шаблона")
    private Long templateId;
    @Schema(name = "reportType",description = "Код шаблона")
    private String reportType;
    @NotBlank
    @Schema(name = "data", description = "Данные")
    private String data;


}