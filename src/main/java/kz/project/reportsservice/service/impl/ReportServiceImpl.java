package kz.project.reportsservice.service.impl;

import fr.opensagres.xdocreport.core.XDocReportException;
import freemarker.template.TemplateException;
import kz.project.reportsservice.data.dto.AmqpDto;
import kz.project.reportsservice.data.dto.ReportDto;
import kz.project.reportsservice.data.dto.ResponseDto;
import kz.project.reportsservice.data.entity.ReportEntity;
import kz.project.reportsservice.data.repository.ReportRepository;
import kz.project.reportsservice.enums.ReportTypeEnum;
import kz.project.reportsservice.enums.TemplateTypeEnum;
import kz.project.reportsservice.feign.PrintedFormsFeignClient;
import kz.project.reportsservice.producer.Producer;
import kz.project.reportsservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
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
    public ResponseDto getReport(ReportDto dto) throws Exception {
        java.util.List<FontFamily> extensions = ExtensionsEnvironment.getExtensionsRegistry().getExtensions(FontFamily.class);
        System.out.println("Available fonts: " + extensions);
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
        if(templateType.equals(TemplateTypeEnum.JASPER.getValue())){
            Map<String, byte[]> body = new HashMap<>();
            body.put("body", templBody);
            body.put("body", templhead);
            JasperPrint print = generateJasperReport(templBody, dto.getData().getBytes(StandardCharsets.UTF_8));
            return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> {
                    JRDocxExporter exporter = new JRDocxExporter();
                    SimpleDocxExporterConfiguration configuration = new SimpleDocxExporterConfiguration();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    exporter.setConfiguration(configuration);
                    // Set the input (JasperPrint)

                    exporter.setExporterInput(new SimpleExporterInput(print));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
                    exporter.exportReport();
                   /* HtmlExporter exporter = new HtmlExporter();
                    exporter.setExporterInput(new SimpleExporterInput(print));
                    exporter.setExporterOutput(new SimpleHtmlExporterOutput(byteArrayOutputStream));
                    exporter.exportReport();
                    Document doc = Jsoup.parse(String.valueOf(byteArrayOutputStream), "UTF-8");
                    XWPFDocument document = new XWPFDocument();
                    Elements elements = doc.body().children();
                    for (Element element : elements) {
                        XWPFParagraph paragraph = document.createParagraph();
                        paragraph.createRun().setText(element.text());
                    }*/
                   /* ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        document.write(bos);*/
                    yield new ResponseDto(bos.toByteArray(),ReportTypeEnum.DOC);
                }
                case HTML -> {
                    HtmlExporter exporter = new HtmlExporter();
                    SimpleHtmlExporterConfiguration configuration = new SimpleHtmlExporterConfiguration();
                    configuration.setBetweenPagesHtml("<div style=\"page-break-before:always\"></div>");

                    exporter.setConfiguration(configuration);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    // Set the input (JasperPrint)
                    exporter.setExporterInput(new SimpleExporterInput(print));

                    // Set the output (HTML)
                    exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));

                    // Export the report
                    exporter.exportReport();

                   // JasperExportManager.exportReportToHtmlFile(print);
                    yield new ResponseDto(bos.toByteArray(), ReportTypeEnum.HTML);
                }
                case XML -> {
                    String string = JasperExportManager.exportReportToXml(print);
                    yield new ResponseDto(string.getBytes(StandardCharsets.UTF_8), ReportTypeEnum.XML);
                }
                case PDF -> {

                    byte[] bytes = JasperExportManager.exportReportToPdf(print);
                    yield new ResponseDto(bytes,ReportTypeEnum.PDF);
                }
                case RTF -> {
                    JRDocxExporter exporter = new JRDocxExporter();

                    // Set the input source
                    exporter.setExporterInput(new SimpleExporterInput(print));

                    // Set the output stream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

                    // Configure RTF exporter
                    SimpleDocxExporterConfiguration configuration = new SimpleDocxExporterConfiguration();
                    exporter.setConfiguration(configuration);

                    // Export the RTF
                    exporter.exportReport();
                    yield new ResponseDto(outputStream.toByteArray(),ReportTypeEnum.RTF);
                }
                case CSV -> {
                    JRCsvExporter exporter = new JRCsvExporter();

                    // Set the input source
                    exporter.setExporterInput(new SimpleExporterInput(print));

                    // Set the output writer
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));

                    // Export the CSV
                    exporter.exportReport();
                    yield new ResponseDto(outputStream.toByteArray(),ReportTypeEnum.CSV);

                }
                default -> null;
            };
        }
        if(templateType.equals(TemplateTypeEnum.FREEMARKER.getValue()))
            return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> null;
                case HTML -> new ResponseDto(getStringWriter(templBody,new String(contentAsByteArray), dto.getFileName())
                        .toString()
                        .getBytes(StandardCharsets.UTF_8),ReportTypeEnum.HTML);
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", templBody);
                    body.put("body", templhead);
                    yield  new ResponseDto(generateFreeemarkerReport(templBody,new String(contentAsByteArray),dto.getFileName()),ReportTypeEnum.PDF);
                }
                default -> null;
            };
        if(templateType.equals(TemplateTypeEnum.XDOCREPORT.getValue()))
             return switch (ReportTypeEnum.valueOf(dto.getReportType())) {
                case DOC -> {
                    byte[] bytes = generateFromXDocReport(templBody, new String(contentAsByteArray), dto.getFileName());
                    yield new ResponseDto(bytes, ReportTypeEnum.DOC);
                }
                case HTML -> {
                  //  TextDocument textDocument = TextDocument.loadDocument(new ByteArrayInputStream());
                    yield null;
                }
                case XML -> null;
                case PDF -> {
                    Map<String, byte[]> body = new HashMap<>();
                    body.put("body", templBody);
                    body.put("body", templhead);
                    yield null; //generateFromXDocReport(templBody,new String(contentAsByteArray),dto.getFileName());
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

    private static void generateWordDocument(String htmlContent, String outputPath) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath)) {

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(htmlContent);

            document.write(out);
            System.out.println("Word document generated successfully.");
        }
    }

}
