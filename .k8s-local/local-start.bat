cd ..
start mvn clean -DskipTests=true install
echo Please wait mvn clean install
pause
cd .k8s-local/target/
call local-helm.bat