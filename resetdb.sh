#!/bin/bash
set -euo pipefail

# resetdb.sh
# Removes TuneTailor-related keys from Redis without flushing the entire database.

detect_redis_cli() {
    if command -v redis-cli >/dev/null 2>&1; then
        echo "redis-cli"
    elif command -v redis6-cli >/dev/null 2>&1; then
        echo "redis6-cli"
    else
        echo "Error: redis-cli or redis6-cli not found." >&2
        exit 1
    fi
}

REDIS_CLI="$(detect_redis_cli)"
REDIS_DB="${REDIS_DB:-0}"

echo "Using ${REDIS_CLI} on Redis DB ${REDIS_DB}"

PATTERNS=(
    "solo:*"
    "pair:*"
    "survey:*"
    "tunetailor:*"
)

deleted=0

for pattern in "${PATTERNS[@]}"; do
    while IFS= read -r key; do
        if [ -n "${key}" ]; then
            "${REDIS_CLI}" -n "${REDIS_DB}" DEL "${key}" >/dev/null
            echo "Deleted: ${key}"
            deleted=$((deleted + 1))
        fi
    done < <("${REDIS_CLI}" -n "${REDIS_DB}" --raw KEYS "${pattern}")
done

echo "Done. Removed ${deleted} TuneTailor-related key(s)."
