package net.n2oapp.security.admin.api.model;

import lombok.Data;
import net.n2oapp.security.admin.api.model.bank.Bank;

import java.util.UUID;

/**
 * Модель уполномоченного лица для показа на UI
 */
@Data
public class EmployeeBank {
    private UUID id;
    private String employeeName;
    private String position;
    private User user;
    private Bank bank;

}
