package net.n2oapp.security.admin.api.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BankTypeEnum {

    PARENT("1", "Головная  организация"),
    DEPARTMENT("2","Подразделение банка");

    private String code;
    private String name;
    BankTypeEnum (String code, String name){
        this.code = code;
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
