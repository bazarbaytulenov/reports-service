package kz.project.reportsservice.service;

import fr.opensagres.xdocreport.core.XDocReportException;
import freemarker.template.TemplateException;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    ResponseDto getReport(ReportDto dto) throws IOException, JRException, XDocReportException, TemplateException;

    byte[] getAsyncReport(Integer requestId);
}
