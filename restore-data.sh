#!/bin/bash
set -e

# Vérifier si le fichier dump existe
if [ -f /docker-entrypoint-initdb.d/init-data.backup ]; then
  echo "Restoring database from backup..."
  pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" /docker-entrypoint-initdb.d/init-data.backup
  echo "Database restored successfully."
else
  echo "No backup file found. Skipping restore."
fi
