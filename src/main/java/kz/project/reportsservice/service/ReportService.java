package kz.project.reportsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kz.project.reportsservice.data.dto.MessageDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;

public interface ReportService {
    ResponseDto getReport(MessageDto dto) throws FileNotFoundException, JRException, JsonProcessingException;

    ResponseDto getAsyncReport(Integer requestId);
}
