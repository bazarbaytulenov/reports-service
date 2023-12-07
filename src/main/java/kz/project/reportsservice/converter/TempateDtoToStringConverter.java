package kz.project.reportsservice.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.project.reportsservice.data.dto.ReportDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TempateDtoToStringConverter implements Converter<String, ReportDto> {

        @Autowired
        private ObjectMapper objectMapper;

        @Override
        @SneakyThrows
        public ReportDto convert(String source) {
            return objectMapper.readValue(source, ReportDto.class);
        }

}
