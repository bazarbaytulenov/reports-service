package kz.project.reportsservice.service;

import fr.opensagres.xdocreport.core.XDocReportException;
import kz.project.reportsservice.data.dto.ReportDto;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    byte[] getReport(ReportDto dto) throws IOException, JRException, XDocReportException;

    byte[] getAsyncReport(Integer requestId);
}
