# 概述
此DEMO主要是展示一个典型的银行账户场景，并且将SOFA的关键功能点融入其中，当前为LDC版本

# 单元化改造说明
- 应用感知单元化：添加单元化相关-D参数com.alipay.ldc.datacenter，com.alipay.ldc.zone，zmode，zonemng_zone_url，从环境变量中获取注入
- 自定义RPC路由规则：bff-account中实现类CustomLdcRouteProvider，自定义规则为取目标服务的第一个参数的前两位为uid
- DTX的单元化路由：发起方bff-account，将DtxTransaction注解更新为使用dtxService.start方法；参与者TCC接口添加uid作为第一个参数
- 消息的单元化路由：消息添加UID属性，另外MQ的管控界面上的路由配置也需添加RZone到RZone的路由
- 任务调度单元化感知：通过ZoneClientUtil获取当前应用运行实例所在单元负责的uid范围作为第一层拆分的shard