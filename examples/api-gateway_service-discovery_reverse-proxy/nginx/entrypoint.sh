#!/bin/bash

export NAMESERVER=`cat /etc/resolv.conf | grep "^nameserver" | awk '{print $2}' | tr '\n' ' '`
echo "> setup dns server $NAMESERVER"

CONFIG_FILE=/etc/nginx/nginx.conf
CONFIG_FILE_TPL=/etc/nginx/nginx.conf.tpl
cp ${CONFIG_FILE} ${CONFIG_FILE_TPL}

envsubst '${NAMESERVER}'  <${CONFIG_FILE_TPL} >${CONFIG_FILE}
echo "> configured: $(cat $CONFIG_FILE)"
echo "> starting nginx server"

exec nginx -g 'daemon off;'

