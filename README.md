# Итоговый проект | ORM-фреймворки для Java

В рамках данного проекта проекта вам предстоит разработать учебную платформу для онлайн-курса по ORM и Hibernate. Проект имитирует реальный заказ: образовательная компания нуждается в системе управления учебными курсами, которая позволит вести расписание занятий, раздавать задания студентам, собирать их решения и проводить тестирование знаний. Этот проект поможет вам закрепить навыки работы с JPA/Hibernate, Spring Boot и реляционными БД, а также понять типичные проблемы, с которыми можно столкнуться, работая с ORM (например, с проблемой ленивой загрузки). Выполнив работу, вы получите практический опыт создания сложной серверной системы и добавите в портфолио полноценный проект.

---
## 1. Стек технологий
- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- Hibernate (JPA)
- PostgreSQL (боевой профиль)
- H2 (интеграционные тесты)
- Maven
- Docker + docker-compose
- JUnit 5 + Spring Boot Test
- Lombok
---
## 2. Структура проекта
Основные пакеты:
- `com.example.eduplatform.entity` — JPA-сущности:
  - `User`, `Profile`, `Course`, `Category`, `Tag`
  - `Enrollment`, `CourseModule`, `Lesson`
  - `Assignment`, `Submission`
  - `Quiz`, `Question`, `AnswerOption`, `QuizSubmission`
  - `CourseReview`
  - enum’ы: `UserRole`, `EnrollmentStatus`, `QuestionType`
- `com.example.eduplatform.repository` — `JpaRepository` для всех сущностей.
- `com.example.eduplatform.service` — сервисный слой (бизнес-логика):
  - `CourseService` — создание курсов, модулей, уроков.
  - `EnrollmentService` — запись студентов на курсы.
  - `AssignmentService` — задания, отправка решений, оценивание.
  - `QuizService` — прохождение теста и подсчёт баллов.
- `com.example.eduplatform.controller` — REST API контроллеры:
  - `CourseController`, `EnrollmentController`,
    `AssignmentController`, `QuizController`.
- `com.example.eduplatform.config`:
  - `GlobalExceptionHandler` — обработка ошибок/валидации.
  - `DemoDataInitializer` (профиль `dev`) — демо-данные.
Конфигурация:
- `src/main/resources/application.yml` — подключение к PostgreSQL через переменные окружения, `spring.jpa.open-in-view=false`.
- `src/test/resources/application-test.yml` — профиль `test` (H2).
DevOps:
- `Dockerfile` — сборка и запуск приложения.
- `docker-compose.yml` — сервисы `app` и `db` (PostgreSQL).
- `.github/workflows/ci.yml` — GitHub Actions: `mvn verify` при пуше.
---
## 3. Запуск без Docker (локально), `*nix`
### 3.1. Requirements
- JDK 17+
- Maven 3.8+
- Локальная PostgreSQL 14/15/16
### 3.2. Подготовка PostgreSQL
### 3.2.1. Установка PostgreSQL (Linux, Debian/Ubuntu)

1. Устанавливаем PostgreSQL и настраиваем:

   ```bash
   # Обновляем пакеты, ставим постгрес
   sudo apt update
   sudo apt install postgresql postgresql-contrib
   # Включаем автозапуск, запускаем, выводим статус
   sudo systemctl enable postgresql
   sudo systemctl start postgresql
   sudo systemctl status postgresql
   ```

2. Создать БД и пользователя (пример):
- Заходим под пользователем `postgres`
```bash
sudo -u postgres psql
```
- Создаем БД:
```sql
CREATE DATABASE edu_platform;
CREATE USER edu WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE edu_platform TO edu;
\c edu_platform
GRANT ALL ON SCHEMA public TO edu;
ALTER SCHEMA public OWNER TO edu;
```
- Тестим работоспособность, пароль `secret`:
```
psql -h localhost -U edu -d edu_platform
```
- Вывод:
```
user@vm-kvm-d13-dev ~> psql -h localhost -U edu -d edu_platform

Password for user edu:
psql (17.6 (Debian 17.6-0+deb13u1))
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, compression: off, ALPN: postgresql)
Type "help" for help.

edu_platform=>
````
### 3.3. Настройка переменных окружения
Приложение читает параметры подключения из переменных окружения:
- `DB_HOST` (по умолчанию `localhost`)- 
- `DB_PORT` (по умолчанию `5432`)- 
- `DB_NAME` (по умолчанию `edu_platform`)- 
- `DB_USERNAME` (по умолчанию `edu`)- 
- `DB_PASSWORD` (по умолчанию `secret`)- 
- `SPRING_PROFILES_ACTIVE=dev` (по умолчанию уже `dev`)

Пример:
````bash
export DB_HOST=localhost
export DB_NAME=edu_platform
export DB_USERNAME=edu
export DB_PASSWORD=secret
export SPRING_PROFILES_ACTIVE=dev
````
или для рыбаков:
```fish
set -x DB_HOST localhost
set -x DB_NAME edu_platform
set -x DB_USERNAME edu
set -x DB_PASSWORD secret
set -x SPRING_PROFILES_ACTIVE dev
```
### 3.4. Запуск приложения
````bash
mvn spring-boot:run
````
После успешного старта приложение доступно по адресу:
- `http://localhost:8080`
При активном профиле `dev` автоматически создаются демо-данные: преподаватель, студент, курс “ORM & Hibernate Basics”, модуль, урок, задание и простой квиз.
---
## 4. Сборка проекта (JAR)
Сборка “толстого” Spring Boot-JAR:
````bash
mvn clean package
````
Результат:
- `target/edu-platform-0.0.1-SNAPSHOT.jar`
Запуск собранного JAR (PostgreSQL должен быть доступен, переменные окружения — заданы):
````bash
java -jar target/edu-platform-0.0.1-SNAPSHOT.jar
````
---
## 5. Запуск через Docker / docker-compose
### 5.1. Требования
- Docker
- docker-compose (или `docker compose`)
### 5.2. Один командный запуск
В корне проекта:
````bash
docker compose up --build
````
Что произойдет:
- Поднимется контейнер `db` с `postgres:16-alpine`:
- БД: `edu_platform`  
- Пользователь: `edu`  
- Пароль: `secret`
- Соберётся Docker-образ приложения (`Dockerfile`) и запустится сервис `app`. 
- Переменные окружения передаются в контейнер `app` (`DB_HOST=db` и т.д.). 
- Приложение стартнет на `http://localhost:8080`.
Остановить:
````bash
docker compose down (либо Ctrl+C)
````
База дропнется автоматически при команде down (не для production :))
````bash
docker compose down -v
````
---

## 6. REST API и ручные проверки через curl

Ниже — минимальный сценарий ручной проверки основных фич через `curl`.

Предполагается:

- приложение запущено на `http://localhost:8080`;
- активен профиль `dev` (по умолчанию), и `DemoDataInitializer` уже создал:
    - 1 преподавателя,
    - 1 студента,
    - 1 категорию,
    - 1 курс «ORM & Hibernate Basics».

Для удобства можно завести переменную:

```bash
BASE_URL=http://localhost:8080
```

### 6.1. Проверка, что приложение поднялось

```bash
curl -s $BASE_URL/api/courses/1
```

При «чистой» базе и первом запуске курс с ID `1` должен существовать и вернуться в JSON-формате.

Если установлен `jq`, можно красиво форматировать:

```bash
curl -s $BASE_URL/api/courses/1 | jq
```

---

### 6.2. Курсы, модули, уроки

#### 6.2.1. Создать дополнительный модуль для существующего курса

```bash
curl -s -X POST "$BASE_URL/api/courses/1/modules"   -H "Content-Type: application/json"   -d '{
    "title": "Module created via curl",
    "orderIndex": 2
  }'
```

В ответе придёт JSON с полем `id` — это `MODULE_ID`, запомните его (или скопируйте).

#### 6.2.2. Создать урок в модуле

Подставьте `MODULE_ID` из предыдущего ответа:

```bash
MODULE_ID=1

curl -s -X POST "$BASE_URL/api/courses/modules/$MODULE_ID/lessons"   -H "Content-Type: application/json"   -d '{
    "title": "Lesson created via curl",
    "content": "Some lesson content",
    "videoUrl": null
  }'
```

В ответе придёт `id` — это `LESSON_ID`, он понадобится для создания задания.

---

### 6.3. Запись студента на курс

В демо-данных создаётся студент и курс. При чистой базе их ID, как правило:

- студент: `2`
- курс: `1`

(при необходимости можно проверить в БД:  
`psql -h localhost -U edu -d edu_platform -c "select id,email,role from users order by id;"`)

#### 6.3.1. Записать студента на курс

```bash
curl -s -X POST "$BASE_URL/api/enrollments"   -H "Content-Type: application/json"   -d '{
    "courseId": 1,
    "studentId": 2
  }'
```

Ответ содержит `id` — это `ENROLLMENT_ID`.

#### 6.3.2. Посмотреть, кто записан на курс

```bash
curl -s "$BASE_URL/api/enrollments/by-course/1"
```

#### 6.3.3. Посмотреть, на какие курсы записан студент

```bash
curl -s "$BASE_URL/api/enrollments/by-student/2"
```

---

### 6.4. Задания и решения

#### 6.4.1. Создать задание для урока

Используем `LESSON_ID`, полученный при создании урока:

```bash
LESSON_ID=1

curl -s -X POST "$BASE_URL/api/assignments/lesson/$LESSON_ID"   -H "Content-Type: application/json"   -d '{
    "title": "Homework via curl",
    "description": "Implement something with Hibernate",
    "dueDate": "2025-01-01T20:00:00",
    "maxScore": 100
  }'
```

Из ответа возьмите `id` — это `ASSIGNMENT_ID`.

#### 6.4.2. Студент отправляет решение

Предположим, студент имеет ID `2`:

```bash
ASSIGNMENT_ID=1

curl -s -X POST "$BASE_URL/api/assignments/$ASSIGNMENT_ID/submit/2"   -H "Content-Type: application/json"   -d '{
    "content": "My solution text from curl"
  }'
```

Ответ содержит `id` — это `SUBMISSION_ID`. Повторная отправка тем же студентом на то же задание вернёт ошибку (проверка уникальности).

#### 6.4.3. Преподаватель оценивает решение

```bash
SUBMISSION_ID=1

curl -s -X POST "$BASE_URL/api/assignments/submissions/$SUBMISSION_ID/grade"   -H "Content-Type: application/json"   -d '{
    "score": 95,
    "feedback": "Looks good, minor issues."
  }'
```

В ответе вернётся обновлённый `Submission` с выставленным `score` и `feedback`.

---

### 6.5. Тесты (Quiz) — базовая ручная проверка

`DemoDataInitializer` в профиле `dev` создаёт простой квиз с одним вопросом и двумя вариантами ответов.  
Для ручной проверки нужно узнать идентификаторы квиза, вопроса и вариантов через SQL.

#### 6.5.1. Найти id квиза, вопросов и вариантов

```bash
psql -h localhost -U edu -d edu_platform -c "select id, title from quizzes;"

# предположим, что QUIZ_ID = 1
psql -h localhost -U edu -d edu_platform -c "
  select q.id as question_id, o.id as option_id, o.text, o.correct
  from questions q
  join answer_options o on o.question_id = q.id
  where q.quiz_id = 1
  order by q.id, o.id;
"
```

Из результата возьмите:

- `QUIZ_ID` — id квиза;
- для каждого `question_id` — `option_id` у строк, где `correct = true`.

#### 6.5.2. Студент проходит тест

Предположим:

- `QUIZ_ID=1`;
- студент: `studentId = 2`;
- есть вопрос `QUESTION_ID`, у него правильный вариант `CORRECT_OPTION_ID`.

```bash
QUIZ_ID=1
QUESTION_ID=1
CORRECT_OPTION_ID=2

curl -s -X POST "$BASE_URL/api/quizzes/$QUIZ_ID/submit" \
  -H "Content-Type: application/json" \
  -d @- <<EOF
{
  "studentId": 2,
  "answers": {
    "$QUESTION_ID": [$CORRECT_OPTION_ID]
  }
}
EOF

```

В ответе будет `QuizSubmission` с полем `score` (например, `1` при одном правильном вопросе).

#### 6.5.3. Посмотреть результаты тестов

Все результаты данного студента:

```bash
curl -s "$BASE_URL/api/quizzes/submissions/by-student/2"
```

Все результаты по конкретному квизу:

```bash
curl -s "$BASE_URL/api/quizzes/submissions/by-quiz/$QUIZ_ID"
```

---

Эти команды `curl` покрывают полный путь:

- курс → модуль → урок,
- запись на курс,
- создание задания, отправка и оценивание решения,
- прохождение квиза и просмотр результатов.

Их можно просто копировать в терминал и последовательно запускать, подставляя полученные ID.
## 7. Тестирование
### 7.1. Запуск тестов
````bash
mvn test
````
Используется профиль `test` и база H2 (конфигурация в `application-test.yml`):
- 
`ddl-auto: create-drop` — схема создаётся перед тестами и удаляется после.- 
Имитация PostgreSQL через `MODE=PostgreSQL`.
### 7.2. Интеграционные тесты
Реализованы базовые интеграционные сценарии:
##### `CourseRepositoryIntegrationTest`
- Создание категории, преподавателя, курса, модуля и урока.  - 
- Проверка сохранения и связей ManyToOne/OneToMany.

##### `EnrollmentServiceIntegrationTest`
  - Запись студента на курс через `EnrollmentService`.  
  - Проверка статуса `ACTIVE` и запрет повторной записи.
##### `AssignmentServiceIntegrationTest`
  - Создание задания для урока и отправка решения студентом.  
  - Проверка уникальности пары (assignment, student).
  
 
 ##### `QuizServiceIntegrationTest`
  
- Создание квиза, вопроса и вариантов.  
- Отправка ответов и проверка корректного подсчёта `score`.

Все тесты поднимают Spring-контекст (`@SpringBootTest`) и используют реальный JPA-стек, что удовлетворяет требованию интеграционного тестирования CRUD.

---
## 8. Демонстрационные данные

При запуске в профиле `dev` автоматически выполняется `DemoDataInitializer`, который создает:
  - преподаватель `teacher@example.com` (роль `TEACHER`);  
  - студент `student@example.com` (роль `STUDENT`);  
  - категория `Programming`;  
  - курс `ORM & Hibernate Basics`;  
  - модуль `Introduction to ORM`;  
  - урок `What is ORM`;  
  - задание `First homework`;  
  - квиз `Intro quiz` с вопросом про расшифровку ORM.
  - Это позволяет сразу проверить работу API без ручного наполнения БД.
---
