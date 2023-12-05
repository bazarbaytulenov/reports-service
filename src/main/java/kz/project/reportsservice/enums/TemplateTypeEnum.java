package kz.project.reportsservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public enum TemplateTypeEnum {
    FREEMARKER("freemarker"),
    JASPER("jasper"),
    XDOCREPORT("xDocReport");

    @Getter
    private  String value;

}
