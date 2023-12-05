package kz.project.reportsservice.service.impl;

import fr.opensagres.xdocreport.core.XDocReportException;
import kz.project.reportsservice.data.dto.AmqpDto;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import kz.project.reportsservice.producer.Producer;
import kz.project.reportsservice.service.ReportService;
import kz.project.reportsservice.util.Util;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static kz.project.reportsservice.util.Util.generateReport;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PrintedFormsFeignClient feignClient;
    private final Producer producer;
    private final ReportRepository repository;

    @Override
    public ResponseDto getReport(ReportDto dto, MultipartFile jsonData) throws IOException, JRException, XDocReportException {
        if (dto == null) return new ResponseDto(null, "dto is empty", null);
        byte[] contentAsByteArray = jsonData.getResource().getContentAsByteArray();
        if (dto.getIsAcync()) {
            producer.sendMessage(new AmqpDto(dto,contentAsByteArray));
            return new ResponseDto("send to service", null, null);
        }

        Map<String, byte[]> template = feignClient.getTemplate(dto.getTemplateCode());
        if(dto.getType().equals("jasper")) {
                        return new ResponseDto("report is create", null, JasperExportManager.exportReportToPdf(generateReport(template, contentAsByteArray)));
        }
        if(dto.getType().equals("freemarker"))
            return  new ResponseDto("report is create", null, Util.getPdf(template,new String(contentAsByteArray),dto.getName()));
        if(dto.getType().equals("xDocReport"))
            return  new ResponseDto("report is create", null, Util.createPdfFromXDocReport(template,new String(contentAsByteArray),dto.getName()));
        else return new ResponseDto(null, "Тип не поддерживается", null);
    }

    @Override
    public ResponseDto getAsyncReport(Integer requestId) {
        ReportEntity reportEntity = repository.findByRequestId(requestId).orElse(null);
        if (reportEntity != null) {
            if(reportEntity.getErrorMessage()!=null)
                return new ResponseDto(null,reportEntity.getErrorMessage(),null);
            return new ResponseDto("report is create", null, reportEntity.getReport());
        }
        return new ResponseDto("the report has not yet been generated", null, null);
    }



}
