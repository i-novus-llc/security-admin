package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Система
 */
@Data
@NoArgsConstructor
public class AppSystem {
    @JsonProperty
    private String code;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private List<AppService> appServices;
}