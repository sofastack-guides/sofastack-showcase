-- 创建库（拆分数量在界面上设置）
CREATE DATABASE IF NOT EXISTS account_db DEFAULT CHARACTER SET = utf8mb4;

-- 创建表（拆分数量在界面上设置）

-- 账号表，account_no为拆分健，取前两位
CREATE TABLE IF NOT EXISTS `account`(
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物理主键',
  `account_no` varchar(8) not null COMMENT '账户号,唯一标示,前两位为uid',
  `balance` decimal(10, 0) DEFAULT '0' COMMENT '账户余额',
  `freeze_amount` decimal(10, 0) DEFAULT '0' COMMENT '冻结资金',
  `unreach_amount` decimal(10, 0) DEFAULT '0' COMMENT '未达资金',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`account_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '账户表';
-- 防悬挂表，sharding_key为拆分健，取前两位
CREATE TABLE IF NOT EXISTS `dtx_tcc_action` (
  `action_id` varchar(96) NOT NULL COMMENT '分支事务号',
  `action_name` varchar(64) DEFAULT NULL COMMENT '参与者名称',
  `tx_id` varchar(128) NOT NULL COMMENT '主事务号',
  `action_group` varchar(32) DEFAULT NULL COMMENT 'action group',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `param_data` varchar(4000) DEFAULT NULL COMMENT '一阶段方法参数数据',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `sharding_key` varchar(128) DEFAULT NULL COMMENT '分库分表字段',
  PRIMARY KEY (`action_id`),
  UNIQUE KEY `idx_tx_id` (`tx_id`, `action_name`)
);
-- 记录每一笔分布式事务操作的账号、操作金额，TCC模式下才会使用到此表，sharding_key为拆分健，取前两位
CREATE TABLE IF NOT EXISTS account_transaction(
  `tx_id` varchar(128) not null COMMENT '事务id+actionid',
  `account_no` varchar(8) not null COMMENT '账号',
  `amount` decimal(10, 0) not null COMMENT '金额',
  `operation` varchar(20) not null COMMENT '操作：入还是出',
  `status` varchar(10) not null COMMENT '状态',
  `sharding_key` varchar(8) not null COMMENT '分库分表字段',
  primary key (`tx_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '事务业务关联表';
