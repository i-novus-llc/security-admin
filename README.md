[![License: Apache License 2](https://img.shields.io/hexpm/l/plug.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

## Единая подсистема прав доступа

### Структура проекта
- `security-admin-api` - общие интерфейсы и модели
- `security-admin-rest-api` - общие интерфейсы и модели REST-сервиса
- `security-admin-rest-client` - java клиент для REST-сервиса
- `security-auth` - связь spring-security с n2o-framework
- `security-auth-common` - реализация ролевой модели 
- `security-auth-oauth2` - реализация oauth2 на базе `security-auth`
- `security-auth-oauth2-gateway` - адаптер для `spring-boot` приложения для взаимодействия с ЕППД

