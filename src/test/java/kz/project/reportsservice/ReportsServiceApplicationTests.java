package kz.project.reportsservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class ReportsServiceApplicationTests {

    public static void main(String[] args) {
        try {
            // Путь к первому JRXML файлу
            String firstJrxmlFilePath = "путь_к_первому_отчету.jrxml";

            // Путь ко второму JRXML файлу
            String secondJrxmlFilePath = "путь_к_второму_отчету.jrxml";

            // Путь к новому JRXML файлу
            String outputJrxmlFilePath = "путь_к_новому_отчету.jrxml";

            // Загружаем первый JRXML файл в DOM-дерево
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document firstDoc = dBuilder.parse(new File(firstJrxmlFilePath));

            // Загружаем второй JRXML файл в DOM-дерево
            Document secondDoc = dBuilder.parse(new File(secondJrxmlFilePath));

            // Получаем все элементы textField в первом документе
            NodeList firstTextFieldElements = firstDoc.getElementsByTagName("textField");

            // Проходим по всем элементам textField в первом документе
            for (int i = 0; i < firstTextFieldElements.getLength(); i++) {
                Node firstTextFieldNode = firstTextFieldElements.item(i);
                if (firstTextFieldNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstTextFieldElement = (Element) firstTextFieldNode;

                    // Получаем имя поля из первого документа (здесь предполагается, что имя поля находится внутри текстового элемента)
                    String fieldName = firstTextFieldElement.getTextContent();

                    // Находим соответствующий элемент textField второго документа
                    NodeList secondTextFieldElements = secondDoc.getElementsByTagName("textField");
                    for (int j = 0; j < secondTextFieldElements.getLength(); j++) {
                        Node secondTextFieldNode = secondTextFieldElements.item(j);
                        if (secondTextFieldNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element secondTextFieldElement = (Element) secondTextFieldNode;

                            // Если имя поля совпадает, заменяем его значением из первого документа
                            if (secondTextFieldElement.getTextContent().equals(fieldName)) {
                                // Заменяем значение во втором документе значением из первого документа
                                secondTextFieldElement.setTextContent("<![CDATA[" + getNewValueFromFirstDocument() + "]]>");
                            }
                        }
                    }
                }
            }

            // Сохраняем изменения в новый файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(secondDoc);
            StreamResult result = new StreamResult(new File(outputJrxmlFilePath));
            transformer.transform(source, result);

            System.out.println("Значения успешно заменены в JRXML файле.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Дополнительный метод для получения нового значения из первого документа
    private static String getNewValueFromFirstDocument() {
        // Ваш код для получения нового значения из первого документа
        return "Новое значение";
    }
}
