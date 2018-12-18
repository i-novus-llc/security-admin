package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Сервис управления банками
 */
public interface BankService {

    /**
     * Поиск банков по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных банков
     */
    Page<Bank> search(BankCriteria criteria);

    /**
     * Получить банк по идентификатору
     *
     * @param id Идентификатор
     * @return Модель банка
     */
    Bank getById(UUID id);

    /**
     * Создать сведения о банке
     * @param bank Модель банка
     * @return Созданный банк
     */
    Bank create(BankCreateForm bank);

    /**
     * Изменение сведений о банке
     * @param bank Модель банка
     * @return Измененный банк
     */
    Bank update(BankUpdateForm bank);



}
