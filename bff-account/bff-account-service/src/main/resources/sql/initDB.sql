-- 创建库（拆分数量在界面上设置）
CREATE DATABASE IF NOT EXISTS trade_db DEFAULT CHARACTER SET = utf8mb4;

-- 创建表（拆分数量在界面上设置）

-- 交易表，stream_id是拆分健，取前两位
CREATE TABLE IF NOT EXISTS `trade_detail`(
    `stream_id` varchar(12) not null COMMENT '流水号',
    `custac_out` varchar(8) not null COMMENT '转出账户号',
    `custac_in` varchar(8) not null COMMENT '转入账户号',
    `amount` decimal(10,0) not null COMMENT '转账金额', 
    `trade_status` varchar(8) not null COMMENT '流水状态：init,done,fail',
    `trade_date` timestamp not null COMMENT '交易日期', 
    PRIMARY KEY (stream_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '流水表';

-- DBP演示：分布式序列
-- 生成分布式序列的表，sharding_col是隐藏的拆分健，取前两位
CREATE TABLE `dbp_sequence` (
  `id` INT AUTO_INCREMENT,
  `name` VARCHAR(64),
  `value` INT,
  `min_value` BIGINT,
  `max_value` BIGINT,
  `step` BIGINT,
  `gmt_create` DATETIME,
  `gmt_modified` DATETIME,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
);

