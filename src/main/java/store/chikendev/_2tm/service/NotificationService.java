package store.chikendev._2tm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import store.chikendev._2tm.dto.request.NotificationPayload;

@Service
public class NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    public String callCreateNotification(NotificationPayload payload) {
        // String url = "http://localhost:3001/api/notification";
        String url = "http://nextjs-api.2tm.store/api/notification";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("object_id", payload.getObjectId());
        requestBody.put("account_id", payload.getAccountId());
        requestBody.put("message", payload.getMessage());
        requestBody.put("type", payload.getType());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
    }

    public void callCreateManual(List<NotificationPayload> payload) {
        for (NotificationPayload item : payload) {
            try {
                callCreateNotification(item);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
