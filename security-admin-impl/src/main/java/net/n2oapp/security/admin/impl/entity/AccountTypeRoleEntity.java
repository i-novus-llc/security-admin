package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.AccountTypeRoleEnum;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_type_role", schema = "sec")
public class AccountTypeRoleEntity implements Serializable {

    @EmbeddedId
    private AccountTypeRoleEntityId id;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    private AccountTypeRoleEnum roleType;

}
