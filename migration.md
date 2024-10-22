# Миграция с security-admin на 8.1.0
* Из проекта была удалена зависимость от web-flux.
В связи с этим была удалена поддержка WebClient в стартере для передачи информации о пользователе. Вместо WebClient была добавлена поддержка RestClient.


# Миграция с security-admin 7+ на 8.0.0
* Переход на n2o-boot-platform 6.1.3 и Spring Boot 3.2.

# Миграция с security-admin 6+ на 7.0.0

Модуль security-admin-auth-server, auth-gateway-adapter и auth-gateway был удален. Функции бэкенда выполняет модуль
security-admin-backend.
Функционал по выпуску обогащённых токенов был упразднён, дополнительную информацию о пользователе нужно получать через
userinfo запрос при выборе аккаунта.

Изменения настроек клиентского приложения. С переходом с библиотеки spring-security-oauth2 на реализацию,
предоставляемую [spring security 5+](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html),
используются новые настройки для указания данных клиента:

    spring.security.oauth2.client.registration.admin-web.provider=keycloak
    spring.security.oauth2.client.registration.admin-web.authorization-grant-type=authorization_code
    spring.security.oauth2.client.registration.admin-web.scope=openid
    spring.security.oauth2.client.registration.admin-web.client-id=
    spring.security.oauth2.client.registration.admin-web.client-secret=
    spring.security.oauth2.client.provider.keycloak.issuer-uri={server-url}/realms/{realm}

Классы ContextFilter и ContextUserInfoTokenServices были перенесены из модуля security-auth-common в security-auth.

Конфигурация OpenIdSecurityConfigurerAdapter была переименована в OpenIdSecurityCustomizer.
Конфигурация N2oSecurityConfigurerAdapter была переименована в N2oSecurityCustomizer.
Для конфигурирования HttpSecurity нужно переопределить метод N2oSecurityCustomizer.configureHttpSecurity.

Функционал классов AuthoritiesPrincipalExtractor/GatewayPrincipalExtractor
был перенесён в net.n2oapp.security.auth.common.KeycloakUserService.

Таблицы бд sec.client и sec.client_role более не используется и могут быть удалены.

# Миграция с security-admin 4+ на 5.0.0

Модули security-admin-commons, security-auth-oauth2-gateway, security-auth-oauth2 были удалёны.
Функциональность была перенесена в модули security-admin-impl, security-auth-common и security-auth соответственно.

Для перевода клиентского N2O приложения с версий 4+ на 5.0.0 необходимо добавить в pom.xml зависимость:

```
<dependency>
    <groupId>net.n2oapp.framework.security</groupId>
    <artifactId>security-auth</artifactId>
</dependency>
``` 

При этом удалив следующие зависимости:
```
<dependency>
    <groupId>net.n2oapp.framework.security</groupId>
    <artifactId>security-auth-oauth2</artifactId>
</dependency>

<dependency>
    <groupId>net.n2oapp.framework.security</groupId>
    <artifactId>security-auth-oauth2-gateway</artifactId>
</dependency>
``` 
