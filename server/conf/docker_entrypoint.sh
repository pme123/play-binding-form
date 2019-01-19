#!/usr/bin/env bash
set -Ee

trap "Error on line $LINENO" ERR

secretPath="/run/secrets"

if [ -e "${secretPath}/db_pwd" ]; then
    export DB_PWD="$(< "${secretPath}/db_pwd")"
fi

CMD="/pme123/bin/play-binding-form-server"
exec ${CMD}
