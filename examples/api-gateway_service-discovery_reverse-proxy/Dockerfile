FROM nginx:1.23
COPY nginx/reverse-proxy.conf /etc/nginx/conf.d/default.conf
COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY nginx/entrypoint.sh /app/entrypoint.sh
ENTRYPOINT /app/entrypoint.sh
