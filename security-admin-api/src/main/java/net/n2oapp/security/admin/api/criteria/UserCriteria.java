package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Критерий фильтрации пользователей
 */
@Data
public class UserCriteria extends PageRequest {
    private String username;
    private String fio;
    private Boolean isActive;
    private List<Integer> roleIds;



    public UserCriteria(int page, int size) {
        super(page, size);
    }

    public UserCriteria(int page, int size, Sort.Direction direction, String property) {
        super(page, size, direction, property);
    }

}
