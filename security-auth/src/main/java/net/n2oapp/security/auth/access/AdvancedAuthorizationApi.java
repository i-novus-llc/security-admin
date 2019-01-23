package net.n2oapp.security.auth.access;

import net.n2oapp.framework.access.AdminService;
import net.n2oapp.framework.access.api.model.ObjectPermission;
import net.n2oapp.framework.access.api.model.filter.N2oAccessFilter;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectAccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.util.FilterMerger;
import net.n2oapp.framework.access.simple.PermissionAndRoleCollector;
import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.access.simple.SimpleAuthorizationApi;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.user.UserContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Реализация AuthorizationApi с учетом динамических объектов
 */
public class AdvancedAuthorizationApi extends SimpleAuthorizationApi {

    private PermissionApi permissionApi;
    private AdminService adminService;
    private Boolean defaultObjectAccess;

    public AdvancedAuthorizationApi(PermissionApi permissionApi, AdminService adminService, ReadCompileBindTerminalPipeline pipeline,
                                    String accessSchemaId, Boolean defaultObjectAccess, Boolean defaultReferenceAccess,
                                    Boolean defaultPageAccess, Boolean defaultUrlAccess, Boolean defaultColumnAccess,
                                    Boolean defaultFilterAccess) {
        super(permissionApi, adminService, pipeline, accessSchemaId, defaultObjectAccess, defaultReferenceAccess,
                defaultPageAccess, defaultUrlAccess, defaultColumnAccess, defaultFilterAccess);
        this.permissionApi = permissionApi;
        this.adminService = adminService;
        this.defaultObjectAccess = defaultObjectAccess;
    }

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

    /**
     * Сборка фильтров по объекту и действию
     * @param user пользователь
     * @param permission право на объект
     * @param objectId объект
     * @param actionId действие
     */
    private void collectFilters(UserContext user, ObjectPermission permission, String objectId, String actionId) {
        if (adminService.isAdmin(user.getUsername())) {
            return;
        }
        List<N2oAccessFilter> filters = AdvancedPermissionAndRoleCollector.collectFilters(
                r -> permissionApi.hasRole(user, r.getId()),
                p -> permissionApi.hasPermission(user, p.getId()),
                u -> permissionApi.hasUsername(user, user.getUsername()), objectId, actionId, getSchema()
        );
        permission.setAccessFilters(FilterMerger.merge(resolveContext(user, filters)));
    }


    /**
     * Разрешает контекст пользователя и удаляет фильтры, если контекст вернул null
     * @param user конектст
     * @param filters фильтры
     * @return фильтры с разрешенным контекстом
     */
    private List<N2oAccessFilter> resolveContext(UserContext user, List<N2oAccessFilter> filters) {
        ContextProcessor contextProcessor = new ContextProcessor(user);
        return filters.stream().map(filter -> {
            switch (filter.getType().arity) {
                case nullary:
                    filter.setValue(String.valueOf(true));
                    break;
                case unary: {
                    Object resolve = contextProcessor.resolve(filter.getValue());
                    filter.setValue(resolve != null ? resolve.toString() : null);
                }
                break;
                case n_ary: {
                    if (filter.getValue() != null) {
                        Object resolve = contextProcessor.resolve(filter.getValue());
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
                            Object resolve = contextProcessor.resolve(value);
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
