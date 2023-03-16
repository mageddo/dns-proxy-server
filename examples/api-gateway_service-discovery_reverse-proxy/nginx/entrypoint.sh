#!/bin/bash

export NAMESERVER=`cat /etc/resolv.conf | grep "^nameserver" | awk '{print $2}' | tr '\n' ' '`
echo "> setup dns server $NAMESERVER"
CONFIG_FILE=/etc/nginx/nginx.conf
cat $CONFIG_FILE | envsubst | tee $CONFIG_FILE >/dev/null

echo "> configured: $(cat $CONFIG_FILE)"
echo "> starting nginx server"

exec nginx -g 'daemon off;'
