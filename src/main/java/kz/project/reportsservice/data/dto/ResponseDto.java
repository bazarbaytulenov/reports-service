package kz.project.reportsservice.data.dto;

import kz.project.reportsservice.enums.ReportTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    private byte[] data;
    private ReportTypeEnum type;
}
