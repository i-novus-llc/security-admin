# Быстрый старт
Полная документация доступна [здесь](https://git.i-novus.ru/framework/security-admin/blob/master/doc/src/index.adoc).

## Шаг первый - настройка Keycloak

Keycloak это открытый сервер SSO аутентификации, разворачиваемый на базе Wildfly.

<a name="step1install"></a>
### Установка

* Скачайте Keycloak в варианте [Standalone server](https://www.keycloak.org/downloads) и запустите на порту `8888`

* Перейдите на `http://localhost:8888`. В случае отсутствия учетной записи администратора будет предложено её создать. 

* Войдите в консоль администратора и создайте новый `realm` (Не рекомендуется использовать master realm).

* Создайте клиент с именем auth-gateway. Назначьте ему следующие Grant Types: "Standard Flow" и 
"Service Accounts Enabled". Также назначьте ему клиентские роли: "realm-management", "manage-realm" и "manage-users". 
Для данного клиента необходимо добавить маппер ролей на базе шаблона "realm roles". 
Важно, чтобы роли отображались и в токене и в `/userinfo` запросе под ключом roles.
 
* Создайте нового пользователя. Создайте и назначьте ему пользовательскую роль "access.admin".  

* В настройках реалма можно настроить доступ к smtp серверу для отправки сообщений (Смена пароля и т.п.).


## Шаг второй - настройка Auth Gateway

Auth Gateway - это SSO сервер авторизации, построенный на базе Spring Cloud Security, интегрируемый с любыми другими серверами 
аутентификации по протоколу OAuth2 OpenId Connect, например, с Keycloak или ЕСИА.

### Требования
* jdk 11 +
* maven 3.5 +

### Установка

* Соберите Auth Gateway выполнив команду `mvn clean package`.

* Сконфигурировать auth-gateway можно с помощью следующих настроек:

    * Настройки подключения к БД
        ```
            spring.datasource.url=jdbc:postgresql://localhost:5432/security
            spring.datasource.username=postgres
            spring.datasource.password=postgres
        ```
    * Настройки подключения к Keycloak
        ```
            access.keycloak.server-url=http://localhost:8888/auth
            access.keycloak.realm=security-admin
            access.keycloak.client.client-id=auth-gateway
            access.keycloak.client.client-secret=3340bfa7-430c-448e-aed5-5278873d54cf
          
            # Данные клиента администрирования, как правило, 
            # это тот же клиент,который используется при аутентификации пользователя 
            access.keycloak.admin-client-id=${access.keycloak.client.client-id}
            access.keycloak.admin-client-secret=${access.keycloak.client.client-secret}
        ```
    * Настройки почтового сервиса
        ```
            sec.mail.host=smtp.gmail.com
            sec.mail.port=587
            sec.mail.username=inovus.sec
            sec.mail.password=
            sec.mail.smtp.auth=true
            sec.mail.smtp.starttls.enabled=true
            sec.password.mail.message.from=inovus.sec@gmail.com
        
        ```
* Auth gateway может быть развернут вместе с микросервисами аудита и НСИ. 
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
        
    
* Запустить сервер можно с помощью команды `java -jar auth-gateway.jar`

* Выполните скрипты `auth-gateway/src/main/resources/create_admin.sql` для загрузки в БД привилегий. 

* Зарегистрируйте клиента, который будет авторизовываться в Auth Gateway. 
Для этого вам нужно добавить запись в таблицу "sec.client", заполнив следующие столбцы:

   ```
       client_id - id клиента, который будет обращаться к серверу auth-gateway.
       client_secret - секретное слово клиента
       grant_types - типы авторизации через запятую (доступны client_credentials и authorization_code)
       redirect_uris - URI разрешенные для редиректа после аутентификации
       access_token_lifetime - время жизни токена доступа
       refresh_token_lifetime - время жизни refresh токена
       logout_url - URL для обнуления сессии пользователя на клиентском приложении
   ```
  
## Шаг третий - подключение клиентского приложения к Auth Gateway

В качестве примера клиентского приложения можно рассмотреть AdminFrontendApplication.

### Требования
* jdk 11 +
* maven 3.5 +

### Установка

* Соберите AdminFrontendApplication выполнив команду `mvn clean package`.

* Сконфигурируйте приложение для использования OpenID сервера с помощью настроек

   ```
        security.oauth2.auth-server-uri=http://localhost:9999
        #Настройки клиента хранятся в auth-gateway в таблице sec.client
        security.oauth2.client.client-id=admin-web
        security.oauth2.client.client-secret=33403217-430c-448e-aed5-5278873d5sda
        access.service.url=${security.oauth2.auth-server-uri}/api
   ```
 * Запустите приложение командой `java -jar access-web.jar`