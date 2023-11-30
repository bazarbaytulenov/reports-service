package kz.project.reportsservice.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResponseDto", description = "Ответ")
public class ResponseDto {
    @NotBlank
    @Schema(name = "succesMessage", description = "Сообшение о успешной формировании отчета")
    private String succesMessage;
    @NotBlank
    @Schema(name = " errorMessage", description = "Сообщение об ошибке")
    private String errorMessage;
    @NotBlank
    @Schema(name ="pdf", description = "Отчет")
    private byte[] pdf;

}
