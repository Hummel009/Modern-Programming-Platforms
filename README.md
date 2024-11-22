[![Code Smells][code_smells_badge]][code_smells_link]
[![Maintainability Rating][maintainability_rating_badge]][maintainability_rating_link]
[![Security Rating][security_rating_badge]][security_rating_link]
[![Bugs][bugs_badge]][bugs_link]
[![Vulnerabilities][vulnerabilities_badge]][vulnerabilities_link]
[![Duplicated Lines (%)][duplicated_lines_density_badge]][duplicated_lines_density_link]
[![Reliability Rating][reliability_rating_badge]][reliability_rating_link]
[![Quality Gate Status][quality_gate_status_badge]][quality_gate_status_link]
[![Technical Debt][technical_debt_badge]][technical_debt_link]
[![Lines of Code][lines_of_code_badge]][lines_of_code_link]

Мои лабораторные работы для BSUIR/БГУИР (белорусский государственный университет информатики и радиоэлектроники).

Предмет - SPP/СПП (современные платформы программирования).

## Условия

### Лабораторная работа 1

Разработать простое приложения с рендерингом на сервере. Например, список задач со
статусом их выполнения, фильтрацией по статусу и выставлением ожидаемой даты завершения,
а также возможностью прикреплять файлы к каждой задаче. Сервер должен отдавать клиенту
готовую разметку, отправка данных серверу должна осуществляться через отправку форм.
Обязательно использование NodeJS , конкретные библиотеки могут отличаться. Например,
подойдут Express + EJS.

### Лабораторная работа 2

Простое приложение, как в лабораторной работе №1, но с другой архитектурой. На сервере
должен быть реализован REST API , на клиенте - Single Page Application . Обмен данных
должен осуществляться путем отправки/принятия HTTP-запросов с данными в формате JSON
или файлов в формате multipart/form-data . Обновление данных на клиенте не должно
приводить к перегрузке страницы. Серверный REST API должен поддерживать ожидаемую
семантику: правильно использовать HTTP-методы ( GET для чтения данных, POST/PUT для
изменения, DELETE для удаления и т.п.) и возвращать правильные коды ответов (200 в случае
успешного чтения/изменения данных, 404 если ресурс не найдет и т.п.). Обязательно
использование NodeJS на сервере. На клиенте можно использовать что угодно,
React/Angular/Vue или вообще без библиотеки.

### Лабораторная работа 3

Добавить к приложению из лабораторной №2 аутентификацию на базе JWT-токенов . Токен
должен передаваться через httponly cookie на клиент и так же отправляться на сервер. При
попытке прочитать/изменить данные на сервере без валидного токена, клиенту должен
возвращаться 401 код. При получении кода 401 клиент должен потребовать от пользователя
ввода логина/пароля. Для формирования JWT-токена можно использовать только пакеты
jsonwebtoken и bcrypt . Логику аутентификации нужно описать в виде отдельного middleware
той библиотеки, на которой написан сервер (например, Express )

### Лабораторная работа 4

Как лабораторная работа №3, но заменить REST API на обмен данных через WebSockets.
Можно использовать библиотеку Socket.IO.

### Лабораторная работа 5

Как лабораторная работа №3, но на сервере сделать API на GraphQL.

### Лабораторная работа 6

Как лабораторная работа №3, но сервера теперь два, и один с другим общается посредством GRPC.

<!----------------------------------------------------------------------------->

[code_smells_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=code_smells

[code_smells_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[maintainability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=sqale_rating

[maintainability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[security_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=security_rating

[security_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[bugs_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=bugs

[bugs_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[vulnerabilities_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=vulnerabilities

[vulnerabilities_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[duplicated_lines_density_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=duplicated_lines_density

[duplicated_lines_density_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[reliability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=reliability_rating

[reliability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[quality_gate_status_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=alert_status

[quality_gate_status_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[technical_debt_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=sqale_index

[technical_debt_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms

[lines_of_code_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Modern-Programming-Platforms&metric=ncloc

[lines_of_code_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Modern-Programming-Platforms
