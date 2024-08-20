package store.chikendev._2tm.entity;
import java.util.Date;


import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Report implements java.io.Serializable {
    @Id
    private Long id;
    private String date;
    private Double totalSale;
    private String role;
    private Long totalMember;
    private Long totalOrder;

    public Report(String role, Long totalMember) {
        this.role = role;
        this.totalMember = totalMember;
    }

    public Report(String completeAt, Double totalSale) {
        this.date = completeAt;
        this.totalSale = totalSale;
    }

    public Report(String completeAt, Double totalSale, Long totalOrder) {
        this.date = completeAt;
        this.totalSale = totalSale;
        this.totalOrder = totalOrder;
    }

    public Report(Double totalSale) {
        this.totalSale = totalSale;
    }

    public Report() {
    }

}
