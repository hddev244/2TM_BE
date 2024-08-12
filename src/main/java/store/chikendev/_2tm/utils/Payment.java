package store.chikendev._2tm.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.stereotype.Component;
import store.chikendev._2tm.config.VNPTConfig;

@Component
public class Payment {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String createVNPT(Long sumTotalPrice, String paymentRecordId)
        throws UnsupportedEncodingException {
        String orderType = "other";
        long amount = sumTotalPrice * 100;
        String vnp_TxnRef = VNPTConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPTConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPTConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPTConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        // vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "thanh toan hoa don:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");

        // String vnp_ReturnUrl = "http://localhost:8080/api/payment/";
        String vnp_ReturnUrl = "http://api.2tm.store/api/payment/";

        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl + paymentRecordId);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(
                    URLEncoder.encode(
                        fieldValue,
                        StandardCharsets.US_ASCII.toString()
                    )
                );
                // Build query
                query.append(
                    URLEncoder.encode(
                        fieldName,
                        StandardCharsets.US_ASCII.toString()
                    )
                );
                query.append('=');
                query.append(
                    URLEncoder.encode(
                        fieldValue,
                        StandardCharsets.US_ASCII.toString()
                    )
                );
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPTConfig.hmacSHA512(
            VNPTConfig.secretKey,
            hashData.toString()
        );
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPTConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }
}
