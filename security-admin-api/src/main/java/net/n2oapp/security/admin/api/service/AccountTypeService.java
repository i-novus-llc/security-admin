package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import org.springframework.data.domain.Page;

/**
 * Сервис типами аккаунтов
 */
public interface AccountTypeService {
    /**
     * Найти все типы аккаунтов по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных типов аккаунтов
     */
    Page<AccountType> findAll(AccountTypeCriteria criteria);

    /**
     * Найти тип аккаунта по идентификатору
     *
     * @param id Идентификатор
     * @return Тип аккаунта
     */
    AccountType findById(Integer id);

    /**
     * Создать тип аккаунта
     *
     * @param accountType Модель типа аккаунта для создания
     * @return Созданный тип аккаунта
     */
    AccountType create(AccountType accountType);

    /**
     * Изменить тип аккаунта
     *
     * @param accountType Модель типа аккаунта для изменения
     * @return Измененный тип аккаунта
     */
    AccountType update(AccountType accountType);

    /**
     * Удалить тип аккаунта
     *
     * @param id Идентификатор типа аккаунта
     */
    void delete(Integer id);
}
