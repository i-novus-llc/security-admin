set DOCKER_REGISTRY=local
cd ../../
cd .docker-compose/target
docker-compose -p security-admin -f docker-compose.build.yml build
cd ../../
cd .k8s/target
#--dry-run --debug
helm upgrade -i security-admin --set global.dockerRegistry=%DOCKER_REGISTRY% --dependency-update --wait -f values.local.yaml .
