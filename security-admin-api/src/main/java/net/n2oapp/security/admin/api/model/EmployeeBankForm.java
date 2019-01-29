package net.n2oapp.security.admin.api.model;

import lombok.Data;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;

/**
 * Модель уполномоченного лица банка для actions
 */
@Data
public class EmployeeBankForm {
    private Integer id;
    private String position;
    private UserForm user;
    private BankUpdateForm bank;
}
