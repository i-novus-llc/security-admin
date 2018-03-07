package net.n2oapp.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Преобразоваине типа для Jackson
 */
public class RestPageImpl<T> extends PageImpl<T>{

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPageImpl(@JsonProperty("content") List<T> content,
                        @JsonProperty("number") int page,
                        @JsonProperty("size") int size,
                        @JsonProperty("totalElements") long total) {
        super(content, new PageRequest(page, size), total);
    }

    public RestPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestPageImpl(List<T> content) {
        super(content);
    }

    public RestPageImpl() {
        super(new ArrayList());
    }
}