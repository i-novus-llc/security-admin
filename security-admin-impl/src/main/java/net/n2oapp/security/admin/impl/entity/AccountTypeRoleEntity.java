package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.security.admin.api.model.AccountTypeRoleEnum;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "account_type_role", schema = "sec")
public class AccountTypeRoleEntity implements Serializable {

    @EmbeddedId
    private AccountTypeRoleEntityId id;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    private AccountTypeRoleEnum roleType;

}
