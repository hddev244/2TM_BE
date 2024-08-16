package store.chikendev._2tm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationPayload {
    public static final String TYPE_NOTIFICATION = "notification";
    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_ORDER = "order";
    public static final String TYPE_CONSIGNMENT_ORDER = "consignment_order";
    public static final String TYPE_BILL_OF_LADING = "bill_of_lading";

    private String objectId;
    private String accountId;
    private String message;
    private String type;
}
