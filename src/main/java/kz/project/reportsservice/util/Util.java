package kz.project.reportsservice.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Paragraph;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.formatter.IDocumentFormatter;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import freemarker.cache.ByteArrayTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.odftoolkit.simple.TextDocument;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class Util {
    public static JasperPrint generateJasperReport(byte[] template, byte[] json) throws JRException, FileNotFoundException {
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.export.pdf.font.files.prefix", "/fonts/fonts.xml");
        jasperReportsContext.setProperty("net.sf.jasperreports.export.doc.font.files.prefix", "/fonts/fonts.xml");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding","Helvetica");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.doc.encoding","UTF-8");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name","DejaVu Sans");
        JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(template));
        jasperReport.setProperty("locale", "ru_Rus");
        JsonDataSource data1Source = new JsonDataSource(new ByteArrayInputStream(json));
        Locale locale = new Locale("ru", "RU");
        Map<String, Object> parameters = new HashMap<>();
        return JasperFillManager.fillReport(jasperReport, parameters, data1Source);

    }

    public static byte[] generateFreeemarkerReport(byte[] temp, String jsonData, String name) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(temp);
        XWPFDocument document = new XWPFDocument(is);

        // 2) Prepare Pdf options
        PdfOptions options = PdfOptions.create();

        // 3) Convert XWPFDocument to Pdf
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfConverter.getInstance().convert(document, os, options);

         return os.toByteArray();


       /* StringWriter stringWriter = getStringWriter(temp, jsonData, name);
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        fopFactory.newFop(MimeConstants.MIME_PDF,new ByteArrayOutputStream(stringWriter.toString().getBytes(StandardCharsets.UTF_8)))
        return createPdf(stringWriter);*/
    }


    public static StringWriter getStringWriter(byte[] temp, String jsonData, String name) throws IOException, TemplateException {
        String dynamicKey = "dynamicData";
        Map<String, Object> data = new ObjectMapper().readValue(jsonData, Map.class);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        ByteArrayTemplateLoader templateLoader = new ByteArrayTemplateLoader();
        templateLoader.putTemplate(name, temp);
        cfg.setTemplateLoader(templateLoader);
        Template template = new Template("dynamicTemplate", new String(temp), cfg);
        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);
        return stringWriter;
    }


    private static byte[] convertToPdf(String xmlContent) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            InputStream xsltInputStream = Util.class.getResourceAsStream("/templates/fop.xsl"); // XSLT stylesheet for PDF transformation

            StreamSource xsltStreamSource = new StreamSource(xsltInputStream);
            Transformer transformer = transformerFactory.newTransformer(xsltStreamSource);

            Source source = new StreamSource(new ByteArrayInputStream(xmlContent.getBytes()));
            fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);

            Result result = new StreamResult(outputStream);
            transformer.transform(source, new StreamResult(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }
            }));

            return outputStream.toByteArray();
        }
    }

    public static byte[] generateFromXDocReport(byte[] temp, String jsonData, String name) throws Exception {
        FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();
/*
        // Load the ODT template from byte array
        InputStream odtTemplateStream = new ByteArrayInputStream(odtTemplateBytes);
        IXDocReport xDocReport = templateEngine.lo(odtTemplateStream, TemplateEngineKind.Freemarker);

        // Create a context and put data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        xDocReport.createContext().put("name", "John Doe");

        // Generate the output as DOCX
        xDocReport.convert(out, ConverterTypeTo.DOCX);

        // Save DOCX content to file
        try (FileOutputStream fos = new FileOutputStream("output.docx")) {
            out.writeTo(fos);
        }*/




        try {
            byte[] templateBytes = temp;
            String jsonString = jsonData;

            // Динамический анализ JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dataMap = mapper.readValue(jsonString, Map.class);
            InputStream is = new ByteArrayInputStream(templateBytes);
            TextDocument document = TextDocument.loadDocument(is);
            for (Iterator<org.odftoolkit.simple.text.Paragraph> it = document.getParagraphIterator(); it.hasNext(); ) {
                org.odftoolkit.simple.text.Paragraph paragraph = it.next();
                String text = paragraph.getTextContent();
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String placeholder = "${" + entry.getKey() + "}";
                    if (text.contains(placeholder)) {
                        text = text.replace(placeholder, entry.getValue().toString());
                        paragraph.setTextContent(text);
                    }
                }
            }

            // Сохранение измененного документа
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            document.save(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



        /*IXDocReport report  = XDocReportRegistry.getRegistry().loadReport(new ByteArrayInputStream(temp), TemplateEngineKind.Freemarker);
        IContext context = report.createContext();
        Map<String, Object> data = new HashMap<>();

        // Динамический ключ (может быть изменен в зависимости от сценария)
        String dynamicKey = "dynamicData";
        data.put(dynamicKey, jsonData);
        context.putMap(data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Options via = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
        report.convert(context,via,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();*/
    }


    private static byte[] createPdf(StringWriter sw) throws IOException {
        // Создание документа PDF
        //Document document = new Document();

        // Использование ByteArrayOutputStream для сохранения PDF в byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // PdfWriter.getInstance(document, byteArrayOutputStream);
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Открытие документа для записи
        // document.open();
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDType0Font font = PDType0Font.load(document, Util.class.getResourceAsStream("/arialmt.ttf"));
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText(sw.toString());
            contentStream.endText();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        document.save(byteArrayOutputStream);
        document.close();

        //}
        // Добавление содержимого из StringWriter в PDF
        //document.add(new Paragraph(sw.toString()));

        // Закрытие документа
        //document.close();

        // Получение PDF в виде byte[]
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return pdfBytes;

    }

  /*  public static String maptToString(ReportDto message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(message.g.getJsonData());
        return s;
    }*/

    /*private static String convertToHTML(TextDocument textDocument) {
        StringBuilder htmlContent = new StringBuilder();

        // Iterate through paragraphs and lists
        for (Paragraph paragraph : textDocument.getParagraphList()) {
            htmlContent.append("<p>").append(paragraph.getString()).append("</p>");
        }

        for (List list : textDocument.getListList()) {
            htmlContent.append("<ul>");
            for (ListItem listItem : list.getListItems()) {
                htmlContent.append("<li>").append(listItem.getParagraph().getString()).append("</li>");
            }
            htmlContent.append("</ul>");
        }

        return htmlContent.toString();
    }*/

}
