package kz.project.reportsservice.service;

import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ReportService {
    ResponseDto getReport(ReportDto dto, MultipartFile jsonData) throws IOException, JRException;

    ResponseDto getAsyncReport(Integer requestId);
}
