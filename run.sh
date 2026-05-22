#!/bin/bash

# Campaign Analytics Platform - Universal Launcher
# Для Mac/Linux

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_green() { echo -e "${GREEN}$1${NC}"; }
print_yellow() { echo -e "${YELLOW}$1${NC}"; }
print_red() { echo -e "${RED}$1${NC}"; }

help() {
    echo ""
    print_green "Campaign Analytics Platform - Commands"
    echo ""
    echo "  ./run.sh build      - Собрать WAR"
    echo "  ./run.sh start      - Запустить всё (Docker + миграции)"
    echo "  ./run.sh stop       - Остановить всё"
    echo "  ./run.sh restart    - Перезапустить всё"
    echo "  ./run.sh test       - Запустить тесты"
    echo "  ./run.sh import     - Загрузить данные из CSV"
    echo "  ./run.sh logs       - Показать логи"
    echo "  ./run.sh clean      - Полная очистка"
    echo "  ./run.sh status     - Статус контейнеров"
    echo "  ./run.sh check      - Проверить API"
    echo "  ./run.sh all        - Полный цикл"
    echo ""
}

build() {
    print_green "📦 Building WAR..."
    cd backend
    mvn clean package -DskipTests
    cd ..
    print_green "✅ Build complete"
}

start() {
    print_green "🐳 Starting Docker containers..."
    docker compose up -d --build

    print_green "⏳ Waiting for PostgreSQL..."
    sleep 10

    print_green "🔄 Running Flyway migrations..."
    cd backend
    mvn flyway:migrate
    cd ..

    print_green "✅ All services started!"
    echo ""
    print_green "📍 Backend: http://localhost:8080/api/health"
    print_green "📍 PostgreSQL: localhost:5432"
}

stop() {
    print_yellow "🛑 Stopping all services..."
    docker compose down
    print_green "✅ All services stopped"
}

restart() {
    stop
    start
}

test() {
    print_green "🧪 Running tests..."
    cd backend
    mvn test
    cd ..
    print_green "✅ Tests complete"
}

import_data() {
    print_green "📥 Importing impressions..."
    curl -s -X POST -F "file=@interview.X.csv" http://localhost:8080/api/metrics/import/impressions

    print_green "📥 Importing events..."
    curl -s -X POST -F "file=@interview.y.csv" http://localhost:8080/api/metrics/import/events

    print_green "✅ Import complete"
}

logs() {
    docker compose logs -f
}

clean() {
    print_red "🧹 Cleaning everything..."
    docker compose down -v
    cd backend
    mvn clean
    cd ..
    print_green "✅ Clean complete"
}

status() {
    docker compose ps
}

check() {
    print_green "🔍 Checking API..."
    echo ""
    print_yellow "Health:"
    curl -s http://localhost:8080/api/health
    echo ""
    echo ""
    print_yellow "Event types:"
    curl -s http://localhost:8080/api/metrics/events/types
    echo ""
    print_green "✅ API check complete"
}

all() {
    clean
    build
    start
    test
    check
    print_green "🎉 Complete! Everything is ready."
}

case "$1" in
    build)      build ;;
    start)      start ;;
    stop)       stop ;;
    restart)    restart ;;
    test)       test ;;
    import)     import_data ;;
    logs)       logs ;;
    clean)      clean ;;
    status)     status ;;
    check)      check ;;
    all)        all ;;
    *)          help ;;
esac