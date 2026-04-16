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

if [ "$#" -lt 5 ]; then
    echo "Usage: $0 <userId> <answer0> <answer1> <answer2> <answer3>"
    exit 1
fi

REDIS_CLI="$(detect_redis_cli)"
REDIS_DB="${REDIS_DB:-0}"

USER_ID="$1"
ANSWER0="$2"
ANSWER1="$3"
ANSWER2="$4"
ANSWER3="$5"

KEY="solo:answers:${USER_ID}"

"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY}" \
    0 "${ANSWER0}" \
    1 "${ANSWER1}" \
    2 "${ANSWER2}" \
    3 "${ANSWER3}" >/dev/null

echo "Added solo answers:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY}"
