#!/bin/bash

new=$1
server=$ (awk '/server/{print $2;exit;}' etc/nginx/nginx.conf | cut -d ':' -f1)

if [ "$new" == "$server" ];
then echo "Your container is already connected to this server."
else

	sed -i 's/.*:6379;/    server '$1':6379;/' etc/nginx/nginx.conf
	/usr/sbin/nginx -s reload

fi
