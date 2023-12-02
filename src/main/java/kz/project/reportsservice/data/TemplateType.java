package kz.project.reportsservice.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;
@AllArgsConstructor
public enum TemplateType {
    JASPER("jasper"),
    FREEMARKER("freemarker");


    @Getter
    private final String value;

    public static TemplateType of(String value) {
        if (value == null) return null;

        return Stream.of(values()).filter(item -> item.value.equalsIgnoreCase(value))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
