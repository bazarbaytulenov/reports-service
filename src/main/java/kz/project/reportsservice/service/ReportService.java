package kz.project.reportsservice.service;

import kz.project.reportsservice.data.dto.MessageDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;

public interface ReportService {
    ResponseDto getReport(MessageDto dto) throws FileNotFoundException, JRException;

    ResponseDto getAsyncReport(Integer requestId);
}
