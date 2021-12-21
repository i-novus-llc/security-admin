package net.n2oapp.security.auth.common;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@ConfigurationProperties(prefix = "access.user.attribute-keys")
public class UserAttributeKeys {
    public List<String> principal;
    public List<String> surname;
    public List<String> name;
    public List<String> patronymic;
    public List<String> email;
    public List<String> guid;
    public List<String> authorities;
    public List<String> department;
    public List<String> organization;
    public List<String> region;
    public List<String> userLevel;
}
