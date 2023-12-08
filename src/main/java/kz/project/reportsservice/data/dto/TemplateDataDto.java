package kz.project.reportsservice.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateDataDto {
    @NotBlank
    @Schema(name = "documentNuber", description = "Номер")
    private String documentNuber;
    @NotBlank
    @Schema(name = "registerDate", description = "Дата")
    private LocalDateTime registerDate;
    @NotBlank
    @Schema(name = "fullName", description = "Наименование ")
    private String fullName;
    private byte[] notificationText;

}
