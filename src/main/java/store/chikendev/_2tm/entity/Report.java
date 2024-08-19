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
    private String completeAt;
    private Double totalSale;

    public Report(String completeAt, Double totalSale) {
        this.completeAt = completeAt;
        this.totalSale = totalSale;
    }

    public Report(Double totalSale) {
        this.totalSale = totalSale;
    }

    public Report() {
    }

}
