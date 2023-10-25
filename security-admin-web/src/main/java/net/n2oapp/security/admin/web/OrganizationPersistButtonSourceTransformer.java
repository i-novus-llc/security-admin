package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.N2oNamespace;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.compile.SourceTransformer;
import net.n2oapp.framework.api.metadata.global.view.action.LabelType;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import org.jdom2.Namespace;

import static java.util.Objects.nonNull;

public class OrganizationPersistButtonSourceTransformer implements SourceTransformer<N2oButton>, SourceClassAware {

    private final N2oNamespace pointer = new N2oNamespace(Namespace.getNamespace("pointer", "http://n2oapp.net/security/schema/pointer"));

    @Override
    public N2oButton transform(N2oButton source, SourceProcessor p) {
        if (LabelType.ICON.equals(source.getType()))
            source.setVisible("{" + Boolean.FALSE + "}");
        else
            source.setVisible(Boolean.FALSE.toString());
        return source;
    }

    @Override
    public boolean matches(N2oButton source) {
        if (nonNull(source.getExtAttributes()) && source.getExtAttributes().containsKey(pointer))
            return nonNull(source.getExtAttributes().get(pointer).get("organization-persist-function"));
        return false;
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oButton.class;
    }
}
