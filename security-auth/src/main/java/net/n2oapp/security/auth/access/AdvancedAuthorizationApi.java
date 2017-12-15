package net.n2oapp.security.auth.access;

import net.n2oapp.framework.access.AdminService;
import net.n2oapp.framework.access.api.model.ObjectPermission;
import net.n2oapp.framework.access.api.model.filter.N2oAccessFilter;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectAccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.util.FilterMerger;
import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.access.simple.SimpleAuthorizationApi;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.user.UserContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация AuthorizationApi с учетом динамических объектов
 */
public class AdvancedAuthorizationApi extends SimpleAuthorizationApi {

    private PermissionApi permissionApi;

    private final static String defaultObjectAccess = "n2o.access.N2oObjectAccessPoint.default";


    @Override
    public ObjectPermission getPermissionForObject(UserContext user, String objectId, String action) {
        if (objectId == null) {
            return ObjectPermission.allowed("objectId is null");
        }

        ObjectPermission permission = getPermission(N2oObjectAccessPoint.class,
                AdvancedPermissionAndRoleCollector.OBJECT_ACCESS.apply(objectId, action), user, ac -> new ObjectPermission(ac, objectId),
                defaultObjectAccess);
        collectFilters(user, permission, objectId, action);
        return permission;
    }

    @Override
    public void setPermissionApi(PermissionApi permissionApi) {
        super.setPermissionApi(permissionApi);
        this.permissionApi = permissionApi;
    }

    /**
     * Сборка фильтров по объекту и действию
     * @param user пользователь
     * @param permission право на объект
     * @param objectId объект
     * @param actionId действие
     */
    private void collectFilters(UserContext user, ObjectPermission permission, String objectId, String actionId) {
        if (AdminService.getInstance().isAdmin(user.getUsername())) {
            return;
        }
        List<N2oAccessFilter> filters = AdvancedPermissionAndRoleCollector.collectFilters(r -> permissionApi.hasRole(user, r.getId()),
                p -> permissionApi.hasPermission(user, p.getId()), u -> permissionApi.hasUsername(user, u.getName()), objectId, actionId);
        permission.setAccessFilters(FilterMerger.merge(resolveContext(user, filters)));
    }


    /**
     * Разрешает контекст пользователя и удаляет фильтры, если контекст вернул null
     * @param user конектст
     * @param filters фильтры
     * @return фильтры с разрешенным контекстом
     */
    private List<N2oAccessFilter> resolveContext(UserContext user, List<N2oAccessFilter> filters) {
        return filters.stream().map(filter -> {
            switch (filter.getType().arity) {
                case nullary:
                    filter.setValue(String.valueOf(true));
                    break;
                case unary: {
                    Object resolve = ContextProcessor.resolve(filter.getValue(), user);
                    filter.setValue(resolve != null ? resolve.toString() : null);
                }
                break;
                case n_ary: {
                    if (filter.getValue() != null) {
                        Object resolve = ContextProcessor.resolve(filter.getValue(), user);
                        if (resolve != null) {
                            if (!(resolve instanceof Collection))
                                throw new IllegalStateException("Context value [" + filter.getValue() + "] must be Collection");
                            Collection<Object> list = (Collection) resolve;
                            filter.setValues(list.stream().map(Object::toString).collect(Collectors.toList()));
                            filter.setValue(null);
                        } else {
                            filter.setValues(null);
                            filter.setValue(null);
                        }
                    } else {
                        List<String> resolves = new ArrayList<>();
                        for (String value : filter.getValues()) {
                            Object resolve = ContextProcessor.resolve(value, user);
                            if (resolve != null)
                                resolves.add(resolve.toString());
                        }
                        filter.setValues(resolves);
                    }
                }
                break;
            }
            return filter;
        }).filter(f -> f.getValue() != null || (f.getValues() != null)).collect(Collectors.toList());
    }


}
