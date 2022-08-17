CREATE TABLE IF NOT EXISTS sys_log
(
    id           bigint       NOT NULL auto_increment,
    user_name    varchar(100) NOT NULL,
    operation    varchar(64)  NOT NULL,
    operate_time bigint       NULL,
    method       varchar(200) NULL     DEFAULT '''',
    params       varchar(500) NULL     DEFAULT '''',
    ip           varchar(64)  NULL     DEFAULT '''',
    create_time  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);