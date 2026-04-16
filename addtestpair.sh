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

# Fixed users
USER1="Andrew"
USER2="Ryan"

# Redis keys (matches your bot structure)
KEY1="pair:answers:1"
KEY2="pair:answers:2"

# Clear old data
"${REDIS_CLI}" -n "${REDIS_DB}" DEL "${KEY1}" >/dev/null
"${REDIS_CLI}" -n "${REDIS_DB}" DEL "${KEY2}" >/dev/null

# Insert answers
"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY1}" \
    0 "wired" \
    2 "sound" >/dev/null

"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY2}" \
    1 "slow" \
    3 "alone" >/dev/null

echo "Inserted pair test data for ${USER1} and ${USER2}:"
echo

echo "${KEY1}:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY1}"
echo

echo "${KEY2}:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY2}"