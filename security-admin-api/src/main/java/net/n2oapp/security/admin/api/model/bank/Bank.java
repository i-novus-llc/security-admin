package net.n2oapp.security.admin.api.model.bank;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.AbstractDto;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class Bank extends AbstractDto {
    private String fullName;
    private String shortName;
    private String regNum;
    private LocalDateTime regDt;
    private String inn;
    private String ogrn;
    private String bik;
    private String kpp;
    private String legalAddress;
    private String actualAddress;
}
