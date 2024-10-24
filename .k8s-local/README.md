Установить:
- docker desktop, включить kubernetes
- Helm Chart
- Lens

В Lens создать ресурс

```
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: retain
provisioner: docker.io/hostpath
reclaimPolicy: Retain
volumeBindingMode: Immediate
```

Установить Ingress и Ingress Controller

```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm upgrade --install nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace nginx --create-namespace
```
В Lens->Network->Services убедиться, что у ingress-nginx-controller выставлен External IP(по умолчанию localhost) и
в hosts прописано 127.0.0.1 kubernetes.docker.internal

Сервисы будут доступ по https://kubernetes.docker.internal

Развертывание

Настройка: `.k8s/chart/values-local.yaml` - выбрать сервисы для подъема установив `enabled: true`


Старт сервисов: local-start.bat/local-start.sh

Остановка сервисов: local-stop.bat
