@echo off
setlocal enabledelayedexpansion

REM Campaign Analytics Platform - Universal Launcher
REM Для Windows

echo.
if "%1"=="" goto :help
if "%1"=="build" goto :build
if "%1"=="start" goto :start
if "%1"=="stop" goto :stop
if "%1"=="restart" goto :restart
if "%1"=="test" goto :test
if "%1"=="import" goto :import
if "%1"=="logs" goto :logs
if "%1"=="clean" goto :clean
if "%1"=="status" goto :status
if "%1"=="check" goto :check
if "%1"=="all" goto :all
goto :help

:help
echo ========================================
echo Campaign Analytics Platform - Commands
echo ========================================
echo.
echo   run.bat build      - Собрать WAR
echo   run.bat start      - Запустить всё (Docker + миграции)
echo   run.bat stop       - Остановить всё
echo   run.bat restart    - Перезапустить всё
echo   run.bat test       - Запустить тесты
echo   run.bat import     - Загрузить данные из CSV
echo   run.bat logs       - Показать логи
echo   run.bat clean      - Полная очистка
echo   run.bat status     - Статус контейнеров
echo   run.bat check      - Проверить API
echo   run.bat all        - Полный цикл
echo.
goto :eof

:build
echo [INFO] Building WAR...
cd backend
call mvn clean package -DskipTests
cd ..
echo [OK] Build complete
goto :eof

:start
echo [INFO] Starting Docker containers...
docker compose up -d --build
echo [INFO] Waiting for PostgreSQL...
timeout /t 10 /nobreak >nul
echo [INFO] Running Flyway migrations...
cd backend
call mvn flyway:migrate
cd ..
echo [OK] All services started!
echo.
echo [INFO] Backend: http://localhost:8080/api/health
echo [INFO] PostgreSQL: localhost:5432
goto :eof

:stop
echo [INFO] Stopping all services...
docker compose down
echo [OK] All services stopped
goto :eof

:restart
call :stop
call :start
goto :eof

:test
echo [INFO] Running tests...
cd backend
call mvn test
cd ..
echo [OK] Tests complete
goto :eof

:import
echo [INFO] Importing impressions...
curl -X POST -F "file=@interview.X.csv" http://localhost:8080/api/metrics/import/impressions
echo.
echo [INFO] Importing events...
curl -X POST -F "file=@interview.y.csv" http://localhost:8080/api/metrics/import/events
echo.
echo [OK] Import complete
goto :eof

:logs
docker compose logs -f
goto :eof

:clean
echo [WARN] Cleaning everything...
docker compose down -v
cd backend
call mvn clean
cd ..
echo [OK] Clean complete
goto :eof

:status
docker compose ps
goto :eof

:check
echo [INFO] Checking API...
echo.
echo Health:
curl -s http://localhost:8080/api/health
echo.
echo.
echo Event types:
curl -s http://localhost:8080/api/metrics/events/types
echo.
echo [OK] API check complete
goto :eof

:all
call :clean
call :build
call :start
call :test
call :check
echo.
echo [OK] Complete! Everything is ready.
goto :eof