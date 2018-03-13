
### Требования:
1. N2O версии 6.1.13 и выше
1. Java 1.8
1. Версии используемых библиотек spring.boot.version - 1.5.2.RELEASE
        spring.security.version - 4.2.3.RELEASE
        spring.version - 4.3.10.RELEASE

### Админка прав доступа состоит из 3х частей:
2. security-admin-impl - модуль содержащий скрипты для наката бд (схема sec)
2. security-admin-web - frontend модуль администрирования пользователей и ролей
2. security-auth - аутентификация пользователей spring-security (информация о пользователях хранится в бд)

### Сценарии использования:
3. Необходима только аутентификация пользователей. Пользователи регистрируются самостоятельно через форму регистрации.
    Подключаем модуль наката скриптов и модуль аутентификации. Для того, чтобы на форме логина появилась ссылка на форму регистрации
    в настройках n2o добавить n2o.auth.registration.enabled=true.

3. Необходима только админка пользователей и ролей, аутентификация пользователей производится внешними системами(есиа, есиаиа, поиб и тд)
    - Подключаем модуль наката скриптов и модуль администрирования пользователей.
    - Реализуем интеграцию пользователей, ролей и привелегий из сторонней системы.
    - При реализации аутентификации необходимо скопировать классы AuthenticationSuccessListener и SessionDestroyedHandler из модуля security-auth
    и реализовать
        - net.n2oapp.framework.access.simple.PermissionApi,
        - net.n2oapp.framework.context.smart.impl.api.ContextProvider для пареметров
        - net.n2oapp.framework.api.user.UserContext.USERNAME
        - net.n2oapp.framework.api.user.UserContext.CONTEXT
        - net.n2oapp.framework.api.user.UserContext.SESSION

3. Необходима аутентификация пользователей и админка прав.
    - Подключаем модуль наката скриптов, модуль администрирования пользователей и модуль аутентификации.

### Базовые шаги по подключению.
4. Подключение liquibase-скриптов
    * добавить зависимость от security-admin-impl
    * подключить следующие скрипты (порядок подключения важен):
	    <include file="classpath*:/security/admin/db/normal/properties.xml"/>
        <include file="classpath*:/security/admin/db/normal/securityAdminBaseChangelog.xml"/>
    После наката скриптов появится схема sec с необходимыми таблицами.

4. Подключение модуля администраирования прав пользователей
    * добавить зависимость от security-admin-web
    * в header подключить страницы для администрирования пользователей (users) и ролей(roles)

4. Подключение модуля аутентификации пользователей
    * добавить зависимость от security-auth
    * добавить конфигурацию spring-security, какие страницы и как должны быть доступны пользователям. Можно на java, тогда в ней для конфигурации по умолчанию можно использовать SpringConfigUtil. Пример https://git.i-novus.ru/framework/n2o-tutorial/blob/master/security-admin-simple/src/main/java/net/n2oapp/framework/tutorial/security/admin/SecurityConfig.java


Пример использования админки можно посмотреть в https://git.i-novus.ru/framework/n2o-tutorial/tree/master/security-admin-simple


