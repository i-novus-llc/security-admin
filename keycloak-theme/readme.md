### Добавление темы в keycloak:
1. Выполнить команду
$KEYCLOAK_HOME/bin/jboss-cli.sh --command="module add --name=net.n2o.theme --resources=target/keycloak-n2o-theme.jar"

1. В файле standalone/configuration/standalone.xml добавить:

 <theme>
        ...
        <modules>
            <module>net.n2o.theme</module>
        </modules>
    </theme>