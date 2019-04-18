CREATE TABLE `zero_flow_error_log` (
`id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
`code` int NOT NULL DEFAULT '0' COMMENT '错误码',
`message` VARCHAR(2048) NOT NULL DEFAULT  '' COMMENT '错误消息',
`user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
`flow_name` VARCHAR(128) NOT NULL DEFAULT  '' COMMENT '流程名称',
`unique_code` varchar(128) NOT NULL DEFAULT  '' COMMENT '流程唯一编码',
`retry_num` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数',
`type` int(11) NOT NULL DEFAULT '0' COMMENT '执行类型0完成，1重试，2异常不重试',
`exception_command` VARCHAR(128) NOT NULL DEFAULT  '' COMMENT '异常命令点',
`command_record`  VARCHAR(1024)  NOT NULL DEFAULT  '' '命令已执行记录',
`step_result`   VARCHAR(20480)   NOT NULL DEFAULT  '' COMMENT '运行数据结果集',
`is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0 未删除 1 已删除',
`create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'

PRIMARY KEY (`id`),
KEY `idx_zfel_user_id` (`user_id`),
KEY `idx_zfel_unique_code` (`unique_code`),
KEY `idx_zfel_type` (`type`),
KEY `idx_zfel_retry_num_index` (`retry_num`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='zeroFlow错误异常表';