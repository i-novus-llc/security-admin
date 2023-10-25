# Быстрый старт
Полная документация доступна [здесь](/doc/src/index.adoc).

## Шаг первый - настройка Keycloak

Keycloak это открытый сервер SSO аутентификации, разворачиваемый на базе Wildfly.

<a name="step1install"></a>
### Установка

* Keycloak может быть развернут из docker образа командой:
```
    docker run -p 8888:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=1234 jboss/keycloak
```

* Перейдите на `http://localhost:8888`, войдите в консоль администратора и создайте новый `realm` (Не рекомендуется использовать master realm).

* Создайте клиента для security-admin-backend. Назначьте ему следующие Grant Types: "Standard Flow" и 
"Service Accounts Enabled". Также назначьте ему клиентские роли: "realm-management", "manage-realm" и "manage-users". 
* Создайте клиента для приложения. Для данного клиента необходимо добавить маппер ролей на базе шаблона "realm roles". 
Важно, чтобы роли отображались в `/userinfo` запросе под ключом roles.
 
* Создайте нового пользователя. Создайте и назначьте ему пользовательскую роль "access.admin".  

* В настройках реалма можно настроить доступ к smtp серверу для отправки сообщений (Смена пароля и т.п.).

## Шаг второй - настройка security-admin-backend

Security-admin-backend - это сервер авторизации, построенный на базе Spring Security, интегрируемый с любыми другими серверами 
аутентификации по протоколу OAuth2 OpenId Connect, например, с Keycloak или ЕСИА.

### Требования
* jdk 14 +
* maven 3.5 +

### Установка

* Соберите security-admin-backend выполнив команду `mvn clean package`.

* Сконфигурировать security-admin-backend можно с помощью следующих настроек:

* Настройки подключения к БД
    ```
        spring.datasource.url=jdbc:postgresql://localhost:5432/security
        spring.datasource.username=postgres
        spring.datasource.password=postgres
    ```
* Настройки подключения к Keycloak
    ```
        access.keycloak.server-url=http://localhost:8888/auth
        # Данные клиента администрирования
        access.keycloak.realm=security-admin
        access.keycloak.admin-client-id=auth-gateway
        access.keycloak.admin-client-secret=3340bfa7-430c-448e-aed5-5278873d54cf
    ```
* Настройки почтового сервиса
    ```
        sec.mail.host=host
        sec.mail.port=587
        sec.mail.username=username
        sec.mail.password=password
        sec.mail.smtp.auth=true
        sec.mail.smtp.starttls.enabled=true
        sec.password.mail.message.from=example@mail.com
        
    ```
* security-admin-backend может быть развернут вместе с микросервисами аудита и НСИ. 
Для конфигурации доступа к этим микросервисам доступны следующие настройки:
    ```
        audit.service.url=http://audit-service:8080/api
        audit.client.sourceApplication=access
  
        #Импорт из НСИ
        rdm.client.sync.url=http://rdm-service:8080/rdm/api
        #Экспорт в НСИ
        rdm.client.export.url=http://rdm-service:8080/rdm/api
        
        #Интервалы событий импорта/экспорта
        rdm.cron.export=0 */15 * ? * *
        rdm.cron.import.region=0 */15 * ? * *
        rdm.cron.import.organization=0 */15 * ? * *
        rdm.cron.import.department=0 */15 * ? * *
    ```
  В случае использования асинхронных клиентов, необходимо задать адрес брокера сообщений
    ```
        spring.activemq.broker-url=tcp://localhost:61616
    ```
        
    
* Запустить сервер можно с помощью команды `java -jar access-service.jar`

* Выполните скрипты `security-admin-backend/src/main/resources/create_admin.sql` для загрузки в БД привилегий.
  
## Шаг третий - подключение клиентского приложения к Auth Gateway
Стартер для приложения клиента [здесь](security-starters/security-client-starter/README.adoc).
