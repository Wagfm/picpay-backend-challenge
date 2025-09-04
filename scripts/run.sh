#!/usr/bin/env bash

ROOT_FOLDER="backend-challenge-picpay"
SCRIPTS_FOLDER="scripts"
ENVIRONMENT=$1
ENVIRONMENT=${ENVIRONMENT:='dev'}

function fatal(){
    echo "$1"
    exit 3;
}

function lower() {
    printf '%s\n' "${1,,}"
}

function basename() {
    local tmp
    tmp=${1%"${1##*[!/]}"}
    tmp=${tmp##*/}
    tmp=${tmp%"${2/"$tmp"}"}
    printf '%s\n' "${tmp:-/}"
}

function setup_dev(){
    return 0;
}

CURRENT_DIR="$(basename "$(pwd)")"

if [ "$CURRENT_DIR" != "$ROOT_FOLDER" ]; then
    if [ "$CURRENT_DIR" != "$SCRIPTS_FOLDER" ]; then
        fatal 'Invalid execution folder!'
    fi
    cd ..
fi

case "$(lower "$ENVIRONMENT")" in
    "dev")
        cd docker || fatal "Folder 'docker' not found!";
            docker compose -f docker-compose.dev.yml down --remove-orphans
            docker compose -f docker-compose.dev.yml up --build
        cd ..
    ;;
    "prod")
        echo 'not implemented...'
    ;;
    *)
        fatal "Invalid environment specification!"
    ;;
esac
