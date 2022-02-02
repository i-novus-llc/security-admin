package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.Account;
import org.springframework.data.domain.Page;

public interface AccountService {

    /**
     * Найти все аккаунты по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных аккаунтов
     */
    Page<Account> findAll(AccountCriteria criteria);

    /**
     * Найти  аккаунт по идентификатору
     *
     * @param id Идентификатор
     * @return Аккаунт
     */
    Account findById(Integer id);

    /**
     * Создать аккаунт
     *
     * @param account Модель аккаунта для создания
     * @return Созданный аккаунт
     */
    Account create(Account account);

    /**
     * Изменить аккаунт
     *
     * @param account Модель аккаунт для изменения
     * @return Измененный аккаунт
     */
    Account update(Account account);

    /**
     * Удалить аккаунт
     *
     * @param id Идентификатор аккаунта
     */
    void delete(Integer id);
}
