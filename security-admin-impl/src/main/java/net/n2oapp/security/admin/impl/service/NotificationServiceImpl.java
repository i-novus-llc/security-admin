package net.n2oapp.security.admin.impl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.n2oapp.security.admin.api.model.NotificationClientModel;
import net.n2oapp.security.admin.api.model.NotificationClientResponseModel;
import net.n2oapp.security.admin.api.service.NotificationService;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final ObjectMapper mapper;

    @Value("${notification.url:#{null}}")
    private String notificationUrl;

    @Value("${notification.authToken:#{null}}")
    private String authToken;

    @Override
    public Integer createClient(NotificationClientModel clientModel) {
        if (isNull(notificationUrl))
            return null;
        Integer result = null;
        try {
            String body = mapper.writeValueAsString(clientModel);

            Response response = createNotificationPostRequest(notificationUrl, body);

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                result = mapper.readValue(
                        response.readEntity(String.class), NotificationClientResponseModel.class).getId();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    Response createNotificationPostRequest(String url, String body) {
        return WebClient.create(url)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE)
                .post(body);
    }
}
