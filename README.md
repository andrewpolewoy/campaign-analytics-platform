
# Campaign Analytics Platform

Аналитическая платформа для рекламных кампаний.  
Обрабатывает большие CSV-файлы (~955 000 строк), строит материализованные представления и предоставляет метрики **CTR** и **EvPM** через REST API.

---

## 🚀 Быстрый старт

### Требования
- Docker Desktop
- Java 17+
- Maven 3.9+

### Запуск одной командой

**Mac / Linux:**
```bash
chmod +x run.sh
./run.sh all
```

**Windows:**
```cmd
run.bat all
```

---

## 📋 Доступные команды

| Команда          | Mac/Linux              | Windows               | Описание                          |
|------------------|------------------------|-----------------------|-----------------------------------|
| Сборка           | `./run.sh build`       | `run.bat build`       | Собрать WAR-файл                  |
| Запуск           | `./run.sh start`       | `run.bat start`       | Запустить приложение              |
| Остановка        | `./run.sh stop`        | `run.bat stop`        | Остановить контейнеры             |
| Перезапуск       | `./run.sh restart`     | `run.bat restart`     | Перезапустить всё                 |
| Импорт данных    | `./run.sh import`      | `run.bat import`      | Загрузить CSV-файлы               |
| Логи             | `./run.sh logs`        | `run.bat logs`        | Показать логи                     |
| Статус           | `./run.sh status`      | `run.bat status`      | Статус контейнеров                |
| Проверка API     | `./run.sh check`       | `run.bat check`       | Тестовые запросы к API            |
| Полный цикл      | `./run.sh all`         | `run.bat all`         | Сборка + запуск + импорт          |

---

## 📥 Загрузка данных

Поместите файлы `interview.X.csv` и `interview.y.csv` в корень проекта и выполните:

```bash
./run.sh import
```

Или вручную через API:
```bash
curl -X POST -F "file=@interview.X.csv" http://localhost:8080/api/metrics/import/impressions
curl -X POST -F "file=@interview.y.csv" http://localhost:8080/api/metrics/import/events
```

---

## 📊 Основные API Endpoints

| Метод | URL                                      | Описание                              |
|-------|------------------------------------------|---------------------------------------|
| GET   | `/api/health`                            | Проверка здоровья приложения          |
| GET   | `/api/metrics/events/types`              | Список доступных типов событий        |
| GET   | `/api/metrics/timeseries?tag=click`      | Временной ряд (CTR / EvPM)            |
| GET   | `/api/metrics/site?tag=click`            | Агрегация по `site_id`                |
| GET   | `/api/metrics/dma?tag=registration`      | Агрегация по `mm_dma`                 |
| POST  | `/api/metrics/import/impressions`        | Загрузка impressions (X.csv)          |
| POST  | `/api/metrics/import/events`             | Загрузка events (Y.csv)               |

---

## 🧪 Проверка работы

```bash
curl http://localhost:8080/api/health

curl "http://localhost:8080/api/metrics/timeseries?tag=click"
curl "http://localhost:8080/api/metrics/site?tag=click"
curl "http://localhost:8080/api/metrics/dma?tag=registration"
```

---

## 🗄️ Миграции Flyway

Миграции применяются автоматически при запуске. Основные:

- `V1__create_tables.sql` — создание таблиц `impression` и `event`
- `V2__create_views.sql` — обычные представления
- `V3__create_materialized_views.sql` — материализованные представления
- `V4__create_indexes.sql` — индексы для производительности
- `V5__refresh_function.sql` — функция обновления MV

---

## 📈 Метрики

**CTR (Click-Through Rate)** = `100 * click_count / impression_count` **(%)**

**EvPM (Events per Mille)** = `1000 * event_count / impression_count` **(‰)**

**Нормализация событий:**
- `fclick` → `click`
- `vregistration` → `registration`
- `vpurchase` → `purchase`
- `vinstall` → `install`
- `vcontent` → `content`

---

## 🛠️ Технологии

- **Backend**: Jakarta EE 10, JAX-RS, CDI, Payara Micro
- **Database**: PostgreSQL 16 + Flyway
- **Парсинг**: uniVocity Parsers (streaming)
- **Build**: Maven
- **Тестирование**: JUnit 5 + Testcontainers

---

## 📁 Структура проекта

```
campaign-analytics-platform/
├── backend/
│   ├── pom.xml
│   ├── src/main/java/... 
│   └── src/main/resources/db/migration/
├── docker-compose.yml
├── run.sh
├── run.bat
├── .env
└── README.md
```

---
