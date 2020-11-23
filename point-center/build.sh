cd ../point-center
mvn clean install -Dmaven.test.skip=true
docker build . -t registry.cn-shanghai.aliyuncs.com/showcase/point-center-service:1.0.0
docker push registry.cn-shanghai.aliyuncs.com/showcase/point-center-service:1.0.0