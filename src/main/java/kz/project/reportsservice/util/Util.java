package kz.project.reportsservice.util;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import freemarker.cache.ByteArrayTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Util {
    public static JasperPrint generateReport(Map<String, byte[]> template, String json) throws JRException, FileNotFoundException {
        JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(template.get("body")));
        JsonDataSource data1Source = new JsonDataSource(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        Map<String, Object> parameters = new HashMap<>();
        return JasperFillManager.fillReport(jasperReport, parameters, data1Source);

    }

    public static byte[] getPdf(Map<String, byte[]> temp, String jsonData, String name) {
        Map<String, Object> data = new HashMap<>();

        // Динамический ключ (может быть изменен в зависимости от сценария)
        String dynamicKey = "dynamicData";
        data.put(dynamicKey, jsonData);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        // Создание TemplateLoader с использованием массива байт
        ByteArrayTemplateLoader templateLoader = new ByteArrayTemplateLoader();
        templateLoader.putTemplate("dynamicTemplate", temp.get("body"));
        // Установка TemplateLoader в конфигурацию FreeMarker
        cfg.setTemplateLoader(templateLoader);

        try {
            // Загрузка шаблона из файла (или ресурса)
            Template template = cfg.getTemplate(name);

            // Обработка шаблона
            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);

            // Вывод результата
            System.out.println(stringWriter.toString());
            return createPdf(stringWriter);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] createPdf(StringWriter sw) {
        try {
            // Создание документа PDF
            Document document = new Document();

            // Использование ByteArrayOutputStream для сохранения PDF в byte[]
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);

            // Открытие документа для записи
            document.open();

            // Добавление содержимого из StringWriter в PDF
            document.add(new Paragraph(sw.toString()));

            // Закрытие документа
            document.close();

            // Получение PDF в виде byte[]
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            return pdfBytes;

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}