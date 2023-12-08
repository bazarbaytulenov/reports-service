package kz.project.reportsservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReportTypeEnum {
    DOC("doc"),
    PDF("pdf"),
    HTML("html"),
    XML("xml");

    @Getter
    private String value;
}
