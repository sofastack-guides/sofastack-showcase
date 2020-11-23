-- 创建库（拆分数量在界面上设置）
CREATE DATABASE IF NOT EXISTS point_db DEFAULT CHARACTER SET = utf8mb4;

-- 创建表（拆分数量在界面上设置）

-- 账号表，account_no为拆分健，取前两位
create table if not exists `account_point`(
    `account_no` varchar(8) NOT NULL ,
    `point` DOUBLE NOT NULL,
    `status` int NOT NULL,
    PRIMARY KEY (`account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户积分';

# 用于幂等性判断
-- 积分流水表，stream_id为拆分健，取前两位
create table if not exists `point_record`(
    `stream_id` varchar(12) NOT NULL ,
    `account_no` varchar(8) NOT NULL ,
    PRIMARY KEY (`stream_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分记录';