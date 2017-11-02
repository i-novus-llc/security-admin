package net.n2oapp.security.admin.api.criteria;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by otihonova on 01.11.2017.
 */
public class Criteria extends PageRequest {
    public Criteria(int page, int size, Sort sort) {
        super(page, size, sort);
    } ///TODO:разберись



}
