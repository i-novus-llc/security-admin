package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import org.springframework.data.domain.Page;

public interface ClientService {

    /**
     * Создать клиента
     *
     * @param client Модель клиента
     * @return Созданный клиент
     */
    Client create(Client client);

    /**
     * Изменить клиента
     *
     * @param client Модель клиента
     * @return Измененный клиент
     */
    Client update(Client client);

    /**
     * Удалить клиента
     *
     * @param id Идентификатор клиента
     */
    void delete(String id);

    /**
     * Получить клиента по идентификатору
     *
     * @param id Идентификатор
     * @return Модель клиента
     */
    Client findById(String id);

    /**
     * Получить  всех клиентов
     *
     * @return Страница всех клиентов
     */
    Page<Client> findAll(ClientCriteria criteria);

}
