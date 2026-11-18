package net.n2oapp.framework.security.autoconfigure.userinfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.JsonToPrincipalMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonToPrincipalFilterTest {

    private static final String HEADER_NAME = "n2o-user-info";
    private static final String USER_JSON = "{\"username\":\"testUsername\",\"systems\":[],\"roles\":[],\"permissions\":[]}";

    private final JsonToPrincipalFilter filter = new JsonToPrincipalFilter(new JsonToPrincipalMapper(), HEADER_NAME);

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void contextIsNotLeakedToNextRequestWithoutHeader() throws Exception {
        AtomicReference<Authentication> insideChain = new AtomicReference<>();
        filter.doFilter(requestWithHeader(), new MockHttpServletResponse(),
                (req, res) -> insideChain.set(SecurityContextHolder.getContext().getAuthentication()));
        assertThat(((UserInfoModel) insideChain.get().getPrincipal()).username, is("testUsername"));

        AtomicReference<Authentication> secondRequest = new AtomicReference<>();
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(),
                (req, res) -> secondRequest.set(SecurityContextHolder.getContext().getAuthentication()));
        assertThat(secondRequest.get(), is(nullValue()));
    }

    @Test
    public void contextIsClearedAfterRequest() throws Exception {
        filter.doFilter(requestWithHeader(), new MockHttpServletResponse(), (req, res) -> {
        });

        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
    }

    @Test
    public void contextIsClearedWhenChainThrows() {
        FilterChain failingChain = (req, res) -> {
            throw new ServletException("test");
        };

        assertThrows(ServletException.class,
                () -> filter.doFilter(requestWithHeader(), new MockHttpServletResponse(), failingChain));
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
    }

    private MockHttpServletRequest requestWithHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_NAME, USER_JSON);
        return request;
    }
}
