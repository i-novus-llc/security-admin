package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_type_role", schema = "sec")
public class AccountTypeRoleEntity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @Id
    @ManyToOne
    @JoinColumn(name = "account_type_id")
    private AccountTypeEntity accountType;

}
