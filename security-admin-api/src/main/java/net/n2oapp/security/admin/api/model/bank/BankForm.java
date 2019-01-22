package net.n2oapp.security.admin.api.model.bank;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BankForm {
    private UUID parentId;
    private String fullName;
    private String shortName;
    private String regNum;
    private LocalDate regDt;
    private String inn;
    private String ogrn;
    private String bik;
    private String kpp;
    private String legalAddress;
    private String actualAddress;
    private Boolean isCoincidesAddress;

}
