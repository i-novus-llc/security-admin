package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationClientEmailModel {
    private String email;
    private boolean confirmed;

    @JsonCreator
    public NotificationClientEmailModel(
            @JsonProperty("email") String email,
            @JsonProperty("confirmed") boolean confirmed
    ) {
        this.email = email;
        this.confirmed = confirmed;
    }
}
