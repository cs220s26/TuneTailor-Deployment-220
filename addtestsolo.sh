#!/bin/bash
set -euo pipefail

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

# Fixed test user + answers
USER_ID="Coleman"

KEY="solo:answers:${USER_ID}"

"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY}" \
    0 "wired" \
    1 "slow" \
    2 "sound" \
    3 "alone" >/dev/null

echo "Inserted test solo data for ${USER_ID}:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY}"