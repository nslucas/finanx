#!/usr/bin/env sh

hostport="$1"
shift
cmd="$@"

host=$(echo "$hostport" | cut -d':' -f1)
port=$(echo "$hostport" | cut -d':' -f2)

echo "Aguardando $host:$port ficar disponível..."

while ! nc -z "$host" "$port"; do
  sleep 1
done

echo "$host:$port disponível. Iniciando aplicação..."

exec $cmd
