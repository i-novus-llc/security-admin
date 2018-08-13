package net.n2oapp.security.auth.access;

import net.n2oapp.criteria.filters.FilterType;
import net.n2oapp.engine.factory.integration.spring.OverrideBean;
import net.n2oapp.framework.access.functions.StreamUtil;
import net.n2oapp.framework.access.metadata.accesspoint.AccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectAccessPoint;
import net.n2oapp.framework.access.metadata.schema.simple.N2oSimpleAccessSchema;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.validation.TypedMetadataValidator;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import net.n2oapp.framework.config.metadata.validation.ValidationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Проверка схемы прав доступа с учетом динамических объектов
 */
public class SimpleAccessSchemaValidatorOverrided extends TypedMetadataValidator<N2oSimpleAccessSchema> implements OverrideBean {
    @Override
    public String getOverrideBeanName() {
        return "accessSchemaValidator";
    }

    @Override
    public Class<N2oSimpleAccessSchema> getMetadataClass() {
        return N2oSimpleAccessSchema.class;
    }

    @Override
    public void check(N2oSimpleAccessSchema metadata) {
        StreamUtil.safeStreamOf(metadata.getN2oPermissions()).flatMap(p -> StreamUtil.safeStreamOf(p.getAccessPoints())).forEach(this::validate);
        StreamUtil.safeStreamOf(metadata.getN2oRoles()).flatMap(p -> StreamUtil.safeStreamOf(p.getAccessPoints())).forEach(this::validate);
        StreamUtil.safeStreamOf(metadata.getN2OUserAccesses()).flatMap(p -> StreamUtil.safeStreamOf(p.getAccessPoints())).forEach(this::validate);
        StreamUtil.safeStreamOf(metadata.getGuestPoints()).forEach(this::validate);
        StreamUtil.safeStreamOf(metadata.getAuthenticatedPoints()).forEach(this::validate);
    }

    private void validate(AccessPoint accessPoint) {
        if (accessPoint instanceof N2oObjectAccessPoint) {
            checkObjectAccess(((N2oObjectAccessPoint) accessPoint));
        }
    }

    private void checkObjectAccess(N2oObjectAccessPoint accessPoint) {
        if (accessPoint.getObjectId() == null)
            throw new N2oMetadataValidationException("n2o.idNotSpecified");
        if (accessPoint.getObjectId().contains("*")) {
            return;
        }
        N2oObject n2oObject = ValidationUtil.getOrNull(accessPoint.getObjectId(), N2oObject.class);
        if (n2oObject == null) {
            throw new N2oMetadataValidationException("n2o.objectNotExists").addData(accessPoint.getObjectId());
        }
        if (accessPoint.getAction() == null || accessPoint.getAction().isEmpty()) {
            return;
        }
        String[] actions = accessPoint.getAction().split(",");
        if (n2oObject.getActions() != null) {
            List<String> objectActionsIds = getActionsIds(Arrays.asList(n2oObject.getActions()));
            for (String action : actions) {
                if (!action.trim().equals(N2oObjectAccessPoint.SPEC_CHARACTER_FOR_ALL_ACTION) && !action.trim().equals("read")) {
                    if (!objectActionsIds.contains(action.trim())) {
                        throw new N2oMetadataValidationException("n2o.actionNotSpecified").addData(n2oObject.getName(), n2oObject.getId(), action);
                    }
                }
            }
        }
        if (accessPoint.getAccessFilters() != null)
            accessPoint.getAccessFilters().forEach(f -> {
                if (f.getFieldId() == null)
                    throw new N2oMetadataValidationException("n2o.fieldIdNotSpecified").addData(accessPoint.getObjectId());
                if ((f.getType() == null || !f.getType().arity.equals(FilterType.Arity.nullary)) && f.getValue() == null && (f.getValues() == null || f.getValues().isEmpty()))
                    throw new N2oMetadataValidationException("n2o.filterValueNotSpecified").addData(accessPoint.getObjectId());
            });
    }

    private List<String> getActionsIds(List<N2oObject.Action> actions) {
        List<String> ids = new ArrayList<>();
        if (actions != null) {
            for (N2oObject.Action action : actions) {
                ids.add(action.getId());
            }
        }
        return ids;
    }
}
