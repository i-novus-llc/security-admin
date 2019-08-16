package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Служба
 */
@Data
@NoArgsConstructor
public class AppService {
    @JsonProperty
    private String code;
    @JsonProperty
    private String name;
    @JsonProperty
    private String systemCode;

}