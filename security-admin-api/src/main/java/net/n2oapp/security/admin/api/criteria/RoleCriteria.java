package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Критерий фильтрации ролей
 */
@Data
public class RoleCriteria extends PageRequest {
    private String name;
    private String description;
    private List<Integer> permissionIds;

    public RoleCriteria(int page, int size) {
        super(page, size);
    }

    public RoleCriteria(int page, int size, Sort.Direction direction, String property) {
        super(page, size, direction, property);
    }

}
