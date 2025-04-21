# Campaign API

Микросервис для управления рекламными кампаниями в Telegram.

## Документяция
[What is immediate campaign](https://github.com/orgs/AdsMriia/discussions/31)

## Технологии

- Java 21
- Spring Boot 3.2.3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- Swagger/OpenAPI
- MapStruct
- Lombok

## Запуск

1. Убедитесь, что у вас установлены:
   - Java 21
   - Maven
   - PostgreSQL

2. Создайте базу данных:
```sql
CREATE DATABASE campaign_db;
```

3. Настройте подключение к базе данных в `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/campaign_db
    username: your_username
    password: your_password
```

4. Запустите приложение:
```bash
mvn spring-boot:run
```

## API Документация

После запуска приложения, Swagger UI доступен по адресу:
```
http://localhost:8080/swagger-ui.html
```

## Основные эндпоинты

### Кампании
- `POST /api/v1/campaigns/submit` - создание и запуск кампании
- `GET /api/v1/campaigns` - получение списка кампаний
- `GET /api/v1/campaigns/{id}` - получение информации о кампании
- `POST /api/v1/campaigns/{id}/stop` - остановка кампании
- `PATCH /api/v1/campaigns/{id}/archive` - архивирование кампании

### Креативы
- `POST /api/v1/constructor` - создание креатива
- `GET /api/v1/constructor/{id}` - получение креатива
- `PUT /api/v1/constructor/{id}` - обновление креатива
- `DELETE /api/v1/constructor/{id}` - удаление креатива

### Пользователи
- `GET /api/v1/users/me` - получение информации о текущем пользователе 
