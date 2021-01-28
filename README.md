# 概述
此DEMO主要是展示一个典型的银行账户场景，并且将SOFA的关键功能点融入其中，详情请查看: https://yuque.antfin-inc.com/yummdb/oza56a/ao8s53，当前为LDC版本

# 应用说明
- account-center: 提供账户服务，以RPC-BOLT协议暴露，包括：扣款（TCC），加钱（TCC），查账，结息（每个账户添加1元）
- point-center: 提供积分服务，以RPC-BOLT协议暴露，包括：积分查询，增加积分（MQ消费者）
- bff-account: 服务封装层，以RPC-REST协议暴露，包括：转账（TCC发起者），查账，积分查询
- web-client：界面（暂未实现）

# 功能说明
- 转账：bff-account发起分布式事务，调用account-center的扣款和加钱TCC服务，并发送事务型消息到SOFAMQ，point-center消费该消息，并给转账账户添加X积分，其中X的值默认是5，但可通过动态配置调整
- 查账：bff-account通过RPC-BOLT调用account-center的账户查询服务
- 查积分：bff-account通过泛化调用point-center的积分查询服务
- 结息：任务调度通过account-center给每个账户增加1元钱，第一层拆分为分片，第二层拆分为分页，每页的数量可通过自定义参数rangeSize配置

# 单元化改造说明
- 应用感知单元化：添加单元化相关-D参数com.alipay.ldc.datacenter，com.alipay.ldc.zone，zmode，zonemng_zone_url，从环境变量中获取注入
- 自定义RPC路由规则：bff-account中实现类CustomLdcRouteProvider，自定义规则为取目标服务的第一个参数的前两位为uid
- DTX的单元化路由：发起方bff-account，将DtxTransaction注解更新为使用dtxService.start方法；参与者TCC接口添加uid作为第一个参数
- 消息的单元化路由：消息添加UID属性，另外MQ的管控界面上的路由配置也需添加RZone到RZone的路由
- 任务调度单元化感知：通过ZoneClientUtil获取当前应用运行实例所在单元负责的uid范围作为第一层拆分的shard

# 部署说明
1. 数据库访问代理和数据库
    - 创建一个ODP实例，4C8G即可，并加入物理数据源，至少一个RDS实例或者OB租户
    - 创建逻辑数据库，按照对应的sql文件说明创建，配置成100库100表
        - account-db: /account-center/account-center-dal/src/main/resources/sql/initDB.sql
        - point-db: /point-center/point-center-service/src/main/resources/sql/initDB.sql
        - trade-db: /bff-account/bff-account-service/src/main/resources/sql/initDB.sql
    - 更新各个应用中的application.properties中的数据库和ODP相关信息

    ```
    # dbp configs
    dbp_db_name=逻辑数据库名称，比如account_db
    dbp_instance_id=odp实例名称
        
    # db connection configs
    account_point_db_url=jdbc:mysql://odp链接信息:8306/逻辑数据库名称比如account_db
    account_point_db_username=逻辑数据库的用户名
    account_point_db_password=逻辑数据库的密码
    ```

2. 动态配置
    - 域：showcase
    - 应用：point-center
    - 类标识：com.aliyun.gts.financial.showcases.sofa.dynamic.PointConfig
    - 属性：pointValue
3. 消息队列配置
    - Topic: ACCT_POINT
    - 消息类型：事务型消息
    - Group ID：GID_SHOWCASE
4. 任务调度配置
    - 类型：集群任务
    - 任务名称：结息
    - 应用名称：account-center
    - 调度类型：事件触发/Cron都行
    - 拆分处理器（第一层）：RATE_SCALCULATE_SHARD_SPLITER
    - 拆分处理器（第二层）：RATES_CALCULATE_SECOND_SPLITER
    - 执行处理器：BATCH_SETTLE_EXECUTOR
    - 自定义参数：rangeSize，INT，5
    - 通信方式：CALLBACK
5. 更新SOFA四元组
    - 更新各个应用中的application.properties中的com.alipay.instanceid，com.antcloud.antvip.endpoint，com.antcloud.mw.access，com.antcloud.mw.secret
6. 创建镜像中心，并且本地登陆验证
7. 本地编译，build并打包镜像，参考build.sh，注意替换其中的镜像地址
8. 使用容器应用服务进行发布，其中镜像地址从镜像中心获取，并使用对应的凭证。注意事项：
    - bff-account应用需要配置公网访问的service，端口映射为8341:8341
    - 监控目录设置为：/home/admin/logs
    - 日志服务配置中路径为：/home/admin/logs
9. 初始化数据：GET http://bff-account的service地址:8341/webapi/account/init/XXXX，其中XXXX可以为任意4个数字，会作为账号的组成部分，默认会创建10000个账号，余额均为10000.

# 使用说明：
- 查账：GET http://bff-account的service地址:8341/webapi/account/balance/00800001
- 转账：POST http://bff-account的service地址:8341/webapi/account/transfer/tcc   body（JSON格式）：{"requestId":"00000000000001","fromAccountNo":"00800001","toAccountNo":"01800001","transferAmount":100}
- 查积分：GET http://bff-account的service地址:8341/webapi/account/point/00800001