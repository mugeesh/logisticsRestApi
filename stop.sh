#bin/bash
echo Stopping dockercompose
docker-compose down
docker-compose rm
echo "stopped"