/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.admin.api.criteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.ws.rs.DefaultValue;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Базовая модель фильтрации данных в таблице
 */
public class BaseCriteria implements Pageable {

    private final int DEFAULT_SIZE_VALUE = 10;

    @DefaultValue("0")
    private int page;

    @DefaultValue("10")
    private int size;

    private List<Sort.Order> orders;

    public BaseCriteria() {
        this.page = 0;
        this.size = DEFAULT_SIZE_VALUE;
    }

    public BaseCriteria(int page, int size, Sort sort) {
        this(page, size);
        this.orders = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(sort.iterator(),
                        Spliterator.ORDERED), false).collect(Collectors.<Sort.Order>toList());
    }

    public BaseCriteria(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        } else if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        } else {
            this.page = page;
            this.size = size;
        }
    }

    @Override
    public int getPageSize() {
        return this.size;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public long getOffset() {
        return this.page * this.size;
    }

    @Override
    public Sort getSort() {
        if (orders != null && !orders.isEmpty()) {
            return Sort.by(orders);
        } else
            return Sort.unsorted();
    }

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPageNumber(int page) {
        this.page = page;
    }

    public void setOffset(long offset) {
        throw new UnsupportedOperationException();
    }

    public void setSort(Sort sort) {
        throw new UnsupportedOperationException();
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        if (size == 0)
            this.size = DEFAULT_SIZE_VALUE;
        else
            this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<Sort.Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Sort.Order> orders) {
        this.orders = orders;
    }

    @Override
    public Pageable next() {
        return new BaseCriteria(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    public Pageable previous() {
        return this.getPageNumber() == 0 ? this : new BaseCriteria(this.getPageNumber(), this.getPageSize(), this.getSort());
    }

    @JsonIgnore
    public boolean isPaged() {
        return true;
    }

    @JsonIgnore
    public boolean isUnpaged() {
        return !isPaged();
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Optional.empty();
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }

    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Override
    public Pageable first() {
        return new BaseCriteria(0, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new BaseCriteria(pageNumber, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCriteria)) return false;
        BaseCriteria that = (BaseCriteria) o;
        return page == that.page &&
                size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }
}
