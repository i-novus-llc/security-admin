package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.aware.CompiledClassAware;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.compile.CompileTransformer;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.PerformButton;

public class OrganizationPersistButtonTransformer implements CompileTransformer<PerformButton, CompileContext<?, ?>>, CompiledClassAware {

    private final String VALIDATED_WIDGET_ID = "organizations_organization_update_main";

    @Override
    public PerformButton transform(PerformButton compiled, CompileContext context, CompileProcessor p) {
        compiled.setVisible(Boolean.FALSE);
        return compiled;
    }

    @Override
    public boolean matches(PerformButton compiled, CompileContext<?, ?> context) {
        return VALIDATED_WIDGET_ID.equals(compiled.getValidatedWidgetId());
    }

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return PerformButton.class;
    }
}
