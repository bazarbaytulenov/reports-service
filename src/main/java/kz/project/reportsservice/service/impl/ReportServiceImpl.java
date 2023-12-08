package kz.project.reportsservice.service.impl;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.json.JSONObject;
import kz.project.reportsservice.data.dto.AmqpDto;

import kz.project.reportsservice.error.ResponseDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.enums.ReportTypeEnum;
import kz.project.reportsservice.enums.TemplateTypeEnum;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import kz.project.reportsservice.producer.Producer;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.stereotype.Service;
import kz.project.reportsservice.data.dto.ReportDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static kz.project.reportsservice.util.Util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PrintedFormsFeignClient feignClient;
    private final Producer producer;
    private final ReportRepository repository;

    @Override
    public byte[] getReport(ReportDto dto) throws IOException, JRException, XDocReportException {
        if (dto == null) throw  new RuntimeException( "dto is empty");
        byte[] contentAsByteArray = dto.getData().getBytes(StandardCharsets.UTF_8);
        if (dto.getAcync()) {
            producer.sendMessage(new AmqpDto(dto,contentAsByteArray));
            return null;
        }

        Map<String,  byte[]>  data = feignClient.getTemplate(dto.getTemplateId());
        String templateType = new String(data.get("type"));
        byte[] templBody =  data.get("body");
        byte[] templhead =  data.get("header");
        if(templateType.equals(TemplateTypeEnum.JASPER.getValue()))
            return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", templBody);
                    body.put("body", templhead);
                    byte[] bytes = JasperExportManager.exportReportToPdf(generateJasperReport(templBody, dto.getData()));
                    yield bytes;
                }
                default -> null;
            };
        if(templateType.equals(TemplateTypeEnum.FREEMARKER.getValue()))
            return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", templBody);
                    body.put("body", templhead);
                    yield  generateFreeemarkerReport(templBody,new String(contentAsByteArray),dto.getFileName());
                }
                default -> null;
            };
        if(templateType.equals(TemplateTypeEnum.XDOCREPORT.getValue()))
             return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", templBody);
                    body.put("body", templhead);
                    yield  generateFromXDocReport(templBody,new String(contentAsByteArray),dto.getFileName());
                }
                default -> null;
            };
        return null;

    }

    @Override
    public byte[] getAsyncReport(Integer requestId) {
        ReportEntity reportEntity = repository.findByRequestId(requestId).orElse(null);
        if (reportEntity != null) {
            if(reportEntity.getErrorMessage()!=null)
                throw new RuntimeException(reportEntity.getErrorMessage());
            return reportEntity.getReport();
        }
        return null;
    }



}
