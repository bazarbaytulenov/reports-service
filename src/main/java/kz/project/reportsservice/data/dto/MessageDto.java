package kz.project.reportsservice.data.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MessageDto", description = "Запрос")
public class MessageDto {
    @NotBlank
    @Schema(name ="isAcync" ,description = "Флаг для генерации отчета асинхронна")
    private Boolean isAcync;
    @NotBlank
    @Schema(name = "templateCode",description = "Код шаблона")
    private String templateCode;
    @NotBlank
    @Schema(name = "jsonData",description = "Данные для формирование отчета")
    private JSONObject jsonData;
    @NotBlank
    @Schema(name = "requestId", description = "Id запроса")
    private Integer requestId;
    @NotBlank
    @Schema(name = "type", description = "Формат шаблона")
    private String type;
    @NotBlank
    @Schema(name = "url", description = "Название шаблона")
    private String name;
    @NotBlank
    @Schema(name = "url", description = "Аддрес куда нужно отправить сформированный отчет")
    private String url;
}
