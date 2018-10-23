package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Роль
 */
@Data
@NoArgsConstructor
public class Role {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String code;
    @JsonProperty
    private String description;
    @JsonProperty
    private List<Permission> permissions;
}