package kz.project.reportsservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmqpDto{
    private ReportDto dto;
    private byte[] jsonData;
}