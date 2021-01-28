CREATE DATABASE IF NOT EXISTS point_db DEFAULT CHARACTER SET = utf8mb4;

create table if not exists `account_point`(
    `account_no` varchar(8) NOT NULL ,
    `point` DOUBLE NOT NULL,
    `status` int NOT NULL,
    PRIMARY KEY (`account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户积分';

-- 用于幂等性判断
create table if not exists `point_record`(
    `stream_id` varchar(12) NOT NULL ,
    `account_no` varchar(8) NOT NULL ,
    PRIMARY KEY (`stream_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分记录';