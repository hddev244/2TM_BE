package store.chikendev._2tm.utils;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;

import java.io.IOException;

@Component
public class SendOtp {

    @Value("${infobip.apiKey}")
    private String apiKey;

    @Value("${infobip.baseUrl}")
    private String baseUrl;

    @Value("${infobip.senderPhoneNumber}")
    private String senderPhoneNumber;

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    @SuppressWarnings("deprecation")
    public String sendOtp(String phoneNumber, String otp) {
        String messageContent = "Mã OTP của bạn là: " + otp;

        String json = String.format(
                "{\"messages\":[{\"destinations\":[{\"to\":\"%s\"}],\"from\":\"%s\",\"text\":\"%s\"}]}",
                phoneNumber, senderPhoneNumber, messageContent);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(baseUrl + "/sms/2/text/advanced")
                .post(body)
                .addHeader("Authorization", "App " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "SMS đã được gửi vui lòng đợi trong giây lát";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AppException(ErrorCode.OTP_ERROR);
    }
}
