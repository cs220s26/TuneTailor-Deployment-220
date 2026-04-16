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

if [ "$#" -ne 4 ]; then
    echo "Usage: $0 <answer0> <answer1> <answer2> <answer3>"
    exit 1
fi

REDIS_CLI="$(detect_redis_cli)"
REDIS_DB="${REDIS_DB:-0}"

ANSWER0="$1"
ANSWER1="$2"
ANSWER2="$3"
ANSWER3="$4"

KEY1="pair:answers:1"
KEY2="pair:answers:2"

"${REDIS_CLI}" -n "${REDIS_DB}" DEL "${KEY1}" >/dev/null
"${REDIS_CLI}" -n "${REDIS_DB}" DEL "${KEY2}" >/dev/null

"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY1}" \
    0 "${ANSWER0}" \
    2 "${ANSWER2}" >/dev/null

"${REDIS_CLI}" -n "${REDIS_DB}" HSET "${KEY2}" \
    1 "${ANSWER1}" \
    3 "${ANSWER3}" >/dev/null

echo "Added pair answers."
echo
echo "${KEY1}:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY1}"
echo
echo "${KEY2}:"
"${REDIS_CLI}" -n "${REDIS_DB}" HGETALL "${KEY2}"