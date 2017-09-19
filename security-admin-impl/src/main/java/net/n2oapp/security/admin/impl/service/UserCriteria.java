package net.n2oapp.security.admin.impl.service;


import net.n2oapp.criteria.api.Criteria;

import java.util.List;

public class UserCriteria  extends Criteria {
    private Integer id;
    private String username;
    private String surname;
    private String name;
    private String patronymic;
    private List<Boolean> isActive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public List<Boolean> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<Boolean> isActive) {
        this.isActive = isActive;
    }

    public Boolean isActive() {
        if (isActive != null && isActive.size() == 1) {
            return isActive.get(0);
        }
        return null;
    }
}

