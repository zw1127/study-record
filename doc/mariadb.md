### 1、以 root 身份进入 MySQL/MariaDB 服务器命令行工具：

```shell
mysql -u root -p
```

### 2、通过执行以下查询设置通用日志文件路径/var/logs/mysql/general-query.log。

```mariadb
SET GLOBAL general_log_file = '/var/log/mysql/general-query.log';
```

默认情况下，通用查询日志写入保存数据库子目录本身（通常为 /var/lib/mysql）的同一数据目录中，日志文件名默认为主机名。但是，如您所见，这是可以更改的。

### 3、启用服务器通用日志：

```mariadb
SET GLOBAL general_log = 1;
```

让我们再次检查一下 MySQL/MariaDB 通用查询日志状态：

```mariadb
SHOW VARIABLES LIKE "general_log%";

#查询processlist
SHOW PROCESSLIST;
```

