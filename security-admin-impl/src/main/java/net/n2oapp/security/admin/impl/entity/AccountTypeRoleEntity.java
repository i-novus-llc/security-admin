package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_type_role", schema = "sec")
public class AccountTypeRoleEntity implements Serializable {

    @EmbeddedId
    private AccountTypeRoleEntityId id;

    @Column(name = "org_default_role")
    private Boolean orgDefaultRole = false;

}
