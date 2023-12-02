package kz.project.reportsservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.project.reportsservice.data.dto.MessageDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.lang.runtime.ObjectMethods;
import java.time.LocalDateTime;
import java.util.Map;

import static kz.project.reportsservice.util.Util.generateReport;
import static kz.project.reportsservice.util.Util.maptToString;

@Service
@RequiredArgsConstructor
@Slf4j
public class Consumer {
    private final PrintedFormsFeignClient feignClient;
    private final ReportRepository repository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(MessageDto message) throws JsonProcessingException {
        Map<String,byte[]> template = feignClient.getTemplate(message.getTemplateCode());
        byte[] bytes = new byte[0];
        ReportEntity reportEntity = new ReportEntity();
        try {
            String s = maptToString(message);
            bytes = JasperExportManager.exportReportToPdf(generateReport(template, s));
        } catch (JRException e) {
            reportEntity.setErrorMessage(e.getMessage());
            log.error(e.getMessage(),e);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
            reportEntity.setErrorMessage(e.getMessage());
        }
        reportEntity.setReport(bytes);
        reportEntity.setCreateDate(LocalDateTime.now());
        reportEntity.setRequestId(message.getRequestId());
        repository.save(reportEntity);
    }

}
