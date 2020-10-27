package net.n2oapp.security.auth.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
public class SpringSecurityUserContextTest {
    private final SpringSecurityUserContext springSecurityUserContext  = new SpringSecurityUserContext();

    @Test
    public void get() {
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("testKey", "TestObject");
        Object baseParam = springSecurityUserContext.get("testKey", baseParams);
        assertEquals("TestObject", (String) baseParam);
        baseParam = springSecurityUserContext.get("notExists", baseParams);
        assertNull(baseParam);
    }

    @Test
    public void set() {
        Map<String, Object> baseParams = new HashMap<>();
        Map<String, Object> context = new HashMap<>();
        Throwable thrown = catchThrowable(() -> springSecurityUserContext.set(context));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
        thrown = catchThrowable(() -> springSecurityUserContext.set(context, baseParams));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}