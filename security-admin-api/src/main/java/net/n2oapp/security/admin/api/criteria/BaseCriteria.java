package net.n2oapp.security.admin.api.criteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Базовая модель фильтрации данных в таблице
 */
public class BaseCriteria implements Pageable {

    private int page;
    private int size;
    private List<Sort.Order> orders;

    public BaseCriteria() {
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
    @JsonProperty("size")
    public int getPageSize() {
        return this.size;
    }

    @Override
    @JsonProperty("page")
    public int getPageNumber() {
        return this.page;
    }

    @Override
    @JsonIgnore
    public int getOffset() {
        return (this.page - 1) * this.size;
    }

    @Override
    @JsonIgnore
    public Sort getSort() {
        if (orders != null && !orders.isEmpty()) {
            return new Sort(orders);
        } else
            return null;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Sort.Order> getOrders() {
        return orders;
    }

    @JsonIgnore
    public void setOrders(List<Sort.Order> orders) {
        this.orders = orders;
    }

    @Override
    @JsonIgnore
    public Pageable next() {
        return new BaseCriteria(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @JsonIgnore
    public Pageable previous() {
        return this.getPageNumber() == 0 ? this : new BaseCriteria(this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    @JsonIgnore
    public boolean hasPrevious() {
        return this.page > 0;
    }

    @JsonIgnore
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Override
    @JsonIgnore
    public Pageable first() {
        return new BaseCriteria(0, this.getPageSize(), this.getSort());
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
