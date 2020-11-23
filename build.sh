cd account-center
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-shanghai.aliyuncs.com/showcase/account-center-service:1.0.0
docker push registry.cn-shanghai.aliyuncs.com/showcase/account-center-service:1.0.0

cd ../point-center
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-shanghai.aliyuncs.com/showcase/point-center-service:1.0.0
docker push registry.cn-shanghai.aliyuncs.com/showcase/point-center-service:1.0.0

cd ../bff-account
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-shanghai.aliyuncs.com/showcase/bff-account-service:1.0.0
docker push registry.cn-shanghai.aliyuncs.com/showcase/bff-account-service:1.0.0
