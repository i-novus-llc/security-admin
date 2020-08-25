/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    Client findByClientId(String id);

    /**
     * Получить  всех клиентов
     *
     * @param criteria
     * @return Страница всех клиентов
     */
    Page<Client> findAll(ClientCriteria criteria);

    /**
     * Создание, обновление или удаление клиента
     *
     * @param clientForm Модель клиента
     * @return Созданного или обновленного клиента или null при удалении
     */
    Client persist(Client clientForm);


    /**
     * Получить клиента из базы, при отсутсвии вёрнет клиента с default значениями полей
     * и clientId = id
     *
     * @param id Идентификатор
     * @return
     */
    Client getDefaultValue(String id);
}
