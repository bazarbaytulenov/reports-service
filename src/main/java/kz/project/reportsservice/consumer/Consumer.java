package kz.project.reportsservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import kz.project.reportsservice.data.dto.AmqpDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import kz.project.reportsservice.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;

import static kz.project.reportsservice.util.Util.generateReport;

@Service
@RequiredArgsConstructor
@Slf4j
public class Consumer {
    private final PrintedFormsFeignClient feignClient;
    private final ReportRepository repository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(AmqpDto message) throws JsonProcessingException {
        Map<String, byte[]> template = feignClient.getTemplate(message.getDto().getTemplateCode());
        byte[] bytes = new byte[0];
        ReportEntity reportEntity = new ReportEntity();
        if (message.getDto().getType().equals("jasper")) {
            try {
                bytes = JasperExportManager.exportReportToPdf(generateReport(template, message.getJsonData()));
            } catch (JRException e) {
                reportEntity.setErrorMessage(e.getMessage());
                log.error(e.getMessage(), e);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
                reportEntity.setErrorMessage(e.getMessage());
            }
        } else if (message.getDto().getType().equals("freemarker")) {
            Util.getPdf(template,new String(message.getJsonData()),message.getDto().getName());

        }
        reportEntity.setReport(bytes);
        reportEntity.setCreateDate(LocalDateTime.now());
        reportEntity.setRequestId(message.getDto().getRequestId());
        repository.save(reportEntity);
    }

}
