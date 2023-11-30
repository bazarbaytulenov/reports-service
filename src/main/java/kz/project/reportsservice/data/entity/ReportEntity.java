package kz.project.reportsservice.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="report")
@Data
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer requestId;
    private byte [] report;
    private LocalDateTime createDate;
    private String errorMessage;

    public ReportEntity(Integer requestId, byte[] report, LocalDateTime createDate) {
        this.requestId = requestId;
        this.report = report;
        this.createDate = createDate;
    }
}
