# 概述
此DEMO主要是展示一个典型的银行账户场景如何基于SOFA框架和服务来实现

# 应用说明
- account-center: 提供账户服务，以RPC-BOLT协议暴露，包括：扣款（TCC），加钱（TCC），查账，结息（每个账户添加1元）
- point-center: 提供积分服务，以RPC-BOLT协议暴露，包括：积分查询，增加积分（MQ消费者）
- bff-account: 服务封装层，以RPC-REST协议暴露，包括：转账（TCC发起者），查账，积分查询

# 功能说明
- 转账：bff-account发起分布式事务，调用account-center的扣款和加钱TCC服务，并发送事务型消息到SOFAMQ，point-center消费该消息，并给转账账户添加X积分，其中X的值默认是5，但可通过动态配置调整
- 查账：bff-account通过RPC-BOLT调用account-center的账户查询服务
- 查积分：bff-account通过泛化调用point-center的积分查询服务
- 结息：任务调度通过account-center给每个账户增加1元钱，第一层拆分为分片，第二层拆分为分页，每页的数量可通过自定义参数rangeSize配置

# 部署和使用说明

详细部署步骤请参考：deployment.md
