package kz.project.reportsservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.opensagres.xdocreport.document.json.JSONObject;
import kz.project.reportsservice.data.dto.AmqpDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.enums.ReportTypeEnum;
import kz.project.reportsservice.enums.TemplateTypeEnum;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import kz.project.reportsservice.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static kz.project.reportsservice.util.Util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class Consumer {
    private final PrintedFormsFeignClient feignClient;
    private final ReportRepository repository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(AmqpDto message) throws JsonProcessingException {
       /* String data = feignClient.getTemplate(message.getDto().getTemplateId()).getBody();
        JSONObject jsObject = new JSONObject(data);
        if(jsObject.getString("type").equals(TemplateTypeEnum.JASPER))
            return switch (ReportTypeEnum.valueOf(message.getDto().getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    byte data1 = (byte) jsObject.get("data");
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", (byte[]) jsObject.get("body"));
                    body.put("body", (byte[]) jsObject.get("header"));
                    yield JasperExportManager.exportReportToPdf(generateJasperReport(body,message.getDto().getData().getBytes(StandardCharsets.UTF_8)));
                }
                default -> null;
            };
        if(jsObject.getString("type").equals(TemplateTypeEnum.FREEMARKER.getValue()))
            return switch (ReportTypeEnum.valueOf(message.getDto().getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", (byte[]) jsObject.get("body"));
                    body.put("header", (byte[]) jsObject.get("header"));
                    yield  generateFreeemarkerReport(body,new String(message.getJsonData()),message.getDto().getFileName());
                }
                default -> null;
            };
        if(dto.getReportType().equals(TemplateTypeEnum.XDOCREPORT.getValue()))
            return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> null;
                case HTML -> null;
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", (byte[]) jsObject.get("body"));
                    body.put("header", (byte[]) jsObject.get("header"));
                    yield  generateFromXDocReport(body,new String(contentAsByteArray),dto.getFileName());
                }
                default -> null;
            };
*/
    }

}
