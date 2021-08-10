package net.n2oapp.security.admin.frontend;

import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.api.metadata.meta.toolbar.ToolbarCell;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.config.metadata.compile.BaseMetadataBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrganizationPagesBinder implements BaseMetadataBinder<Page> {

    @Value("${access.organization-persist-mode}")
    private String organizationPersistMode;

    private static final String ORGANIZATION_LIST_PAGE = "organizations";
    private static final String ORGANIZATION_VIEW_PAGE = "organizations_organization_update";

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }

    @Override
    public Page bind(Page page, BindProcessor p) {
        if ("rest".equals(organizationPersistMode))
            return page;

        if (page.getId().equals(ORGANIZATION_LIST_PAGE)) {
            StandardPage standardPage = (StandardPage) page;
            ((Table) standardPage.getRegions().get("single").get(0).getContent().get(0)).getToolbar().getButton("create").setVisible(Boolean.FALSE);
            ((Table) standardPage.getRegions().get("single").get(0).getContent().get(0)).getComponent().getCells().removeIf(ToolbarCell.class::isInstance);
        }

        if (page.getId().equals(ORGANIZATION_VIEW_PAGE))
            page.getToolbar().getButton("submit").setVisible(Boolean.FALSE);

        return page;
    }
}
