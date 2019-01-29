package net.n2oapp.security.auth.access;

import net.n2oapp.framework.access.api.model.filter.N2oAccessFilter;
import net.n2oapp.framework.access.metadata.accesspoint.AccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectAccessPoint;
import net.n2oapp.framework.access.metadata.schema.permission.N2oPermission;
import net.n2oapp.framework.access.metadata.schema.role.N2oRole;
import net.n2oapp.framework.access.metadata.schema.simple.SimpleCompiledAccessSchema;
import net.n2oapp.framework.access.metadata.schema.user.N2oUserAccess;
import net.n2oapp.framework.access.simple.PermissionAndRoleCollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Собирает привелегии и роли с учетом возможности динамических объектов
 */
public class AdvancedPermissionAndRoleCollector {

    public final static BiFunction<String, String, Predicate<N2oObjectAccessPoint>> OBJECT_ACCESS = (objectId, actionId) ->
            ac -> (ac.getObjectId().equals(objectId) || objectId.matches(ac.getObjectId())) && Objects.equals(actionId, ac.getAction());

    /**
     * Возвращает все фильтры доступа по объекту и действию
     * @param rolePredicate функция проверки обрабатываемых единиц(role) на соответствие заданным условиям
     * @param permissionPredicate функция проверки обрабатываемых единиц(permission) на соответствие заданным условиям
     * @param userPredicate функция проверки обрабатываемых единиц(user) на соответствие заданным условиям
     * @param objectId id проверяемого объекта
     * @param actionId id проверяемого действия
     * @return фильтры доступа
     */
    public static List<N2oAccessFilter> collectFilters(Predicate<N2oRole> rolePredicate, Predicate<N2oPermission> permissionPredicate, Predicate<N2oUserAccess> userPredicate,
                                                       String objectId, String actionId, SimpleCompiledAccessSchema schema) {
        List<N2oRole> roles = PermissionAndRoleCollector.collectRoles(N2oObjectAccessPoint.class, OBJECT_ACCESS.apply(objectId, actionId), schema)
                .stream().filter(rolePredicate).collect(Collectors.toList());
        List<N2oPermission> permissions = PermissionAndRoleCollector.collectPermission(N2oObjectAccessPoint.class,
                OBJECT_ACCESS.apply(objectId, actionId), schema)
                .stream().filter(permissionPredicate).collect(Collectors.toList());
        List<N2oUserAccess> users = PermissionAndRoleCollector.collectUsers(N2oObjectAccessPoint.class, OBJECT_ACCESS.apply(objectId, actionId), schema)
                .stream().filter(userPredicate).collect(Collectors.toList());
        List<N2oAccessFilter> filters = new ArrayList<>();
        filters.addAll(collectFilters(roles, N2oRole::getAccessPoints, objectId, actionId));
        filters.addAll(collectFilters(permissions, N2oPermission::getAccessPoints, objectId, actionId));
        filters.addAll(collectFilters(users, N2oUserAccess::getAccessPoints, objectId, actionId));
        return filters;
    }

    private static <T> List<N2oAccessFilter> collectFilters(List<T> list, Function<T, AccessPoint[]> getter, String objectId, String actionId) {
        return list.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(N2oObjectAccessPoint.class::isInstance)
                .map(N2oObjectAccessPoint.class::cast)
                .filter(o -> (o.getObjectId().equals(objectId) || objectId.matches(o.getObjectId())) && o.getAction().equals(actionId))
                .filter(o -> o.getAccessFilters() != null)
                .flatMap(o -> o.getAccessFilters().stream())
                .map(ac -> {
                    if (ac.getValues() != null && !ac.getValues().isEmpty()) {
                        return new N2oAccessFilter(ac.getFieldId(), ac.getValues(), ac.getType());
                    } else {
                        return new N2oAccessFilter(ac.getFieldId(), ac.getValue(), ac.getType());
                    }
                })
                .collect(Collectors.toList());
    }
}
