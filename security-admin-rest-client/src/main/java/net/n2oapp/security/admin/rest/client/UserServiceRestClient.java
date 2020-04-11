package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления пользователями, для вызова rest
 */
public class UserServiceRestClient implements UserService {

    private UserRestService client;

    public UserServiceRestClient(UserRestService client) {
        this.client = client;
    }

    @Override
    public User create(UserForm user) {
        return client.create(user);
    }

    @Override
    public User update(UserForm user) {
        return client.update(user);
    }

    @Override
    public void delete(Integer id) {
        client.delete(id);
    }

    @Override
    public User getById(Integer id) {
        return client.getById(id);
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        RestUserCriteria userCriteria = new RestUserCriteria();
        userCriteria.setPage(criteria.getPageNumber());
        userCriteria.setSize(criteria.getPageSize());
        userCriteria.setFio(criteria.getFio());
        userCriteria.setIsActive(criteria.getIsActive());
        userCriteria.setUsername(criteria.getUsername());
        userCriteria.setOrders(criteria.getOrders());
        userCriteria.setRoleIds(criteria.getRoleIds());
        userCriteria.setSystems(criteria.getSystems());
        userCriteria.setUserLevel(criteria.getUserLevel());
        userCriteria.setDepartmentId(criteria.getDepartmentId());
        userCriteria.setOrganizations(criteria.getOrganizations());
        userCriteria.setRegionId(criteria.getRegionId());
        return client.findAll(userCriteria);
    }

    @Override
    public User changeActive(Integer id) {
        return client.changeActive(id);
    }

    @Override
    public Boolean checkUniqueUsername(String username) {
        RestUserCriteria criteria = new RestUserCriteria();
        criteria.setUsername(username);
        criteria.setSize(10);
        criteria.setPage(0);
        return client.findAll(criteria).getContent().size() == 0;
    }

    @Override
    public User loadSimpleDetails(Integer id) {
        return client.loadSimpleDetails(id);
    }

    @Override
    public void resetPassword(UserForm user) {
        client.resetPassword(user);
    }
}
