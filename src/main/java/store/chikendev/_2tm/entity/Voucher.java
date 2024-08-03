package store.chikendev._2tm.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class Voucher {

    @Id
    private String id;
    private Double DiscountPercentage;
    private String note;
    private int UsageLimit;
    private Boolean status;
    private Date startDate;
    private Date endDate;
    private Double MinimumPurchaseAmount;
    private Double MaximumDiscountAmount;

    @OneToMany(mappedBy = "voucher")
    private List<Order> orders;
}
