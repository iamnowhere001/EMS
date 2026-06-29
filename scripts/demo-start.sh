#!/usr/bin/env bash
set -euo pipefail

MYSQL_DATA_DIR="/var/lib/mysql"
MYSQL_RUN_DIR="/run/mysqld"
INIT_MARKER="${MYSQL_DATA_DIR}/.ems_demo_initialized"

mkdir -p "${MYSQL_RUN_DIR}" "${MYSQL_DATA_DIR}"
chown -R mysql:mysql "${MYSQL_RUN_DIR}" "${MYSQL_DATA_DIR}"

if [ ! -d "${MYSQL_DATA_DIR}/mysql" ]; then
  mariadb-install-db --user=mysql --datadir="${MYSQL_DATA_DIR}" --skip-test-db >/dev/null
fi

mysqld_safe --datadir="${MYSQL_DATA_DIR}" --skip-networking=0 --bind-address=127.0.0.1 &
MYSQL_PID=$!

for i in {1..60}; do
  if mysqladmin ping --silent; then
    break
  fi
  if ! kill -0 "${MYSQL_PID}" 2>/dev/null; then
    echo "MariaDB failed to start"
    exit 1
  fi
  sleep 1
done

mysql -uroot <<SQL
CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'%' IDENTIFIED BY '${DB_PASSWORD}';
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USERNAME}'@'%';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USERNAME}'@'localhost';
FLUSH PRIVILEGES;
SQL

if [ ! -f "${INIT_MARKER}" ]; then
  mysql -uroot "${DB_NAME}" < /app/schema.sql
  touch "${INIT_MARKER}"
fi

exec java ${JAVA_OPTS:-"-Xms256m -Xmx512m -XX:+UseG1GC"} -jar /app/app.jar
