cd account-center
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-hangzhou.aliyuncs.com/showcase/account-center-service-ldc:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/showcase/account-center-service-ldc:1.0.0

cd ../point-center
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-hangzhou.aliyuncs.com/showcase/point-center-service-ldc:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/showcase/point-center-service-ldc:1.0.0

cd ../bff-account
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-hangzhou.aliyuncs.com/showcase/bff-account-service-ldc:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/showcase/bff-account-service-ldc:1.0.0