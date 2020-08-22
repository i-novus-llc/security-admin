package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationClientModel {
    private String lastName;
    private String firstName;
    private String patronymic;
    private List<NotificationClientEmailModel> emails;
}