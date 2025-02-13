## Ubuntu安装MySQL
### 介绍：
```text
Ubuntu 是一款基于Linux操作系统的免费开源发行版，广受欢迎。它以稳定性、安全性和用户友好性而闻名，适用于桌面和服务器环境。
Ubuntu提供了大量的软件包和应用程序，拥有庞大的社区支持和活跃的开发者社区。它的长期支持（LTS）版本获得5年的安全更新，
为企业和个人提供了可靠的选择。 Ubuntu的桌面版本具有直观的用户界面，而服务器版本则适用于构建强大的Web服务器和云计算平台。
当你需要在Ubuntu上安装MySQL时，有两种主要的方式：在线安装和离线安装。在线安装是通过Ubuntu软件包管理器直接下载和安装MySQL，而离线安装则涉及手动下载MySQL安装包并在离线环境中进行安装。
```

### Ubuntu和MySQL默认版本对照
以下是一个以表格形式列出了不同Ubuntu版本和它们通常默认安装的MySQL版本：

| Ubuntu 版本 | 默认 MySQL 版本   |
|-----------|-----|
| Ubuntu 22.04 LTS | MySQL 8.0  | 
| Ubuntu 20.04 LTS | MySQL 8.0  |
| Ubuntu 18.04 LTS | MySQL 5.7  |
| Ubuntu 16.04 LTS | MySQL 5.7  |
| Ubuntu 14.04 LTS | MySQL 5.5  |
| Ubuntu 12.04 LTS | MySQL 5.5  |

### 在线安装MySQL
#### 步骤1：更新软件包列表
在进行任何软件安装之前，请确保你的系统的软件包列表是最新的。打开终端并运行以下命令：
```shell
sudo apt update
```

#### 步骤2：安装MySQL服务器
在更新软件包列表后，这里我们可以查看一下可使用的MySQL安装包：
```shell
# 查看可使用的安装包
sudo apt search mysql-server
Sorting... Done
Full Text Search... Done
default-mysql-server/jammy,jammy 1.0.8 all
  MySQL database server binaries and system database setup (metapackage)

default-mysql-server-core/jammy,jammy 1.0.8 all
  MySQL database server binaries (metapackage)

mysql-server/jammy-updates,jammy-updates,jammy-security,jammy-security,now 8.0.41-0ubuntu0.22.04.1 all [installed]
  MySQL database server (metapackage depending on the latest version)

mysql-server-8.0/jammy-updates,jammy-security,now 8.0.41-0ubuntu0.22.04.1 amd64 [installed,automatic]
  MySQL database server binaries and system database setup

mysql-server-core-8.0/jammy-updates,jammy-security,now 8.0.41-0ubuntu0.22.04.1 amd64 [installed,automatic]
  MySQL database server binaries

```
接下来可以使用以下命令安装MySQL服务器：
```shell
# 安装最新版本
sudo apt install -y mysql-server
# 安装指定版本
sudo apt install -y mysql-server-8.0
```
如果不加-y 会在安装过程中，系统将提示你设置MySQL的root密码。确保密码足够强，且记住它，因为你将在以后需要用到它。
#### 步骤3：启动MySQL服务
安装完成后，MySQL服务会自动启动，未启动则使用以下命令启动MySQL服务：
```shell
sudo systemctl start mysql
```
并将MySQL设置为开机自启动：
```shell
sudo systemctl enable mysql
```

#### 步骤4：检查MySQL状态
可以使用以下命令来检查MySQL是否正在运行：
```shell
sudo systemctl status mysql
● mysql.service - MySQL Community Server
     Loaded: loaded (/lib/systemd/system/mysql.service; enabled; vendor preset: enabled)
     Active: active (running) since Thu 2025-02-13 10:04:39 CST; 1h 37min ago
   Main PID: 6634 (mysqld)
     Status: "Server is operational"
      Tasks: 45 (limit: 4551)
     Memory: 405.1M
        CPU: 28.540s
     CGroup: /system.slice/mysql.service
             └─6634 /usr/sbin/mysqld

2月 13 10:04:39 zhaowei-virtual-machine systemd[1]: Starting MySQL Community Server...
2月 13 10:04:39 zhaowei-virtual-machine systemd[1]: Started MySQL Community Server.

```
至此，你已经成功在线安装了MySQL服务器。

#### 步骤5：修改密码、权限
```shell
# 登录mysql，在默认安装时如果没有让我们设置密码，则直接回车就能登录成功。
sudo mysql -uroot -p
# 设置密码 mysql8.0
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '新密码';
# 设置密码 mysql5.7
set password=password('新密码');
# 配置IP 5.7
grant all privileges on *.* to root@"%" identified by "密码";
# 刷新缓存
flush privileges;
```
```text
注意：配置8.0版本参考：我这里通过这种方式没有实现所有IP都能访问；
我是通过直接修改配置文件才实现的，MySQL8.0版本把配置文件 my.cnf 拆分成mysql.cnf 和mysqld.cnf，
我们需要修改的是mysqld.cnf文件：
```
```shell
sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf
```
修改 bind-address，保存后重启MySQL即可。
```shell
bind-address            = 0.0.0.0
```
重启MySQL重新加载一下配置：
```shell
sudo systemctl restart mysql
```

## MySQL 的用户权限配置问题
以下是解决这个问题的步骤：
### 1. **检查 MySQL 用户权限**
MySQL 用户需要具有从指定主机（IP 地址）连接的权限。你可以通过以下步骤检查和修改用户权限：

#### 1.1 登录 MySQL 服务器
使用具有管理员权限的账户（如 `root`）登录 MySQL：
```bash
mysql -u root -p
```

#### 1.2 查看用户权限
运行以下 SQL 查询，检查用户是否允许从 `192.168.175.1` 连接：
```sql
SELECT user, host FROM mysql.user;
```
- 如果用户没有从 `192.168.175.1` 连接的权限，你需要为该用户添加权限。

#### 1.3 修改用户权限
如果用户已经存在，但主机限制不正确，可以更新用户的主机权限：
```sql
UPDATE mysql.user SET host = '192.168.175.1' WHERE user = 'your_username';
FLUSH PRIVILEGES;
```
- 如果你想允许从任何主机连接，可以将 `host` 设置为 `'%'`：
  ```sql
  UPDATE mysql.user SET host = '%' WHERE user = 'your_username';
  FLUSH PRIVILEGES;
  ```

#### 1.4 创建新用户（可选）
如果用户不存在，可以创建一个新用户并授予权限：
```sql
CREATE USER 'your_username'@'192.168.175.1' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON *.* TO 'your_username'@'192.168.175.1';
FLUSH PRIVILEGES;
```

---

### 2. **检查 MySQL 配置文件**
MySQL 的配置文件（通常是 `my.cnf` 或 `my.ini`）可能限制了连接的主机。

#### 2.1 找到配置文件
- Linux 系统：`/etc/my.cnf` 或 `/etc/mysql/my.cnf`
- Windows 系统：`C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`

#### 2.2 修改绑定地址
确保 `bind-address` 配置允许外部连接：
```ini
bind-address = 0.0.0.0
```
- `0.0.0.0` 表示允许所有 IP 地址连接。
- 如果设置为 `127.0.0.1`，则只允许本地连接。

#### 2.3 重启 MySQL 服务
修改配置文件后，重启 MySQL 服务以使更改生效：
- Linux：
  ```bash
  sudo systemctl restart mysql
  ```
- Windows：
  在服务管理器中重启 MySQL 服务。

---

### 3. **检查防火墙设置**
确保服务器的防火墙允许来自 `192.168.175.1` 的 MySQL 连接（默认端口为 3306）。

#### 3.1 Linux 防火墙
```bash
sudo ufw allow from 192.168.175.1 to any port 3306
```

#### 3.2 Windows 防火墙
在 Windows 防火墙设置中，添加一条入站规则，允许 TCP 端口 3306。

---

### 4. **检查网络连接**
确保客户端和服务器之间的网络连接正常：
- 使用 `ping` 命令检查网络连通性：
  ```bash
  ping 192.168.175.1
  ```
- 使用 `telnet` 或 `nc` 检查 MySQL 端口是否开放：
  ```bash
  telnet 192.168.175.1 3306
  ```

---

### 5. **总结**
通过以上步骤，你应该能够解决 `Host '192.168.175.1' is not allowed to connect to this MySQL server` 的问题。如果问题仍然存在，请检查 MySQL 日志文件（通常位于 `/var/log/mysql/error.log` 或 MySQL 安装目录下的 `data` 文件夹）以获取更多错误信息。
