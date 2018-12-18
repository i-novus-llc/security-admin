package net.n2oapp.security.admin.api.model.bank;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BankUpdateForm extends BankForm {
    private UUID id;
    private LocalDateTime lastActionDate;
}
