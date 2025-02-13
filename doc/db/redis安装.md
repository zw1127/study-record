在 Ubuntu 上安装 Redis 非常简单，以下是详细的步骤：

---

### 1. **更新系统包列表**
在安装 Redis 之前，先更新系统的包列表，确保获取最新的软件包信息：
```bash
sudo apt update
```

---

### 2. **安装 Redis**
使用 `apt` 包管理器安装 Redis：
```bash
sudo apt install redis-server
```

---

### 3. **启动 Redis 服务**
安装完成后，Redis 服务会自动启动。你可以通过以下命令检查 Redis 服务的状态：
```bash
sudo systemctl status redis-server
```
- 如果服务未启动，可以手动启动：
  ```bash
  sudo systemctl start redis-server
  ```
- 设置 Redis 开机自启：
  ```bash
  sudo systemctl enable redis-server
  ```

---

### 4. **测试 Redis**
使用 Redis 客户端连接到 Redis 服务器，测试是否正常运行：
```bash
redis-cli
```
在 Redis 命令行中，输入以下命令测试：
```bash
ping
```
如果返回 `PONG`，说明 Redis 正常运行。

---

### 5. **配置 Redis（可选）**
默认情况下，Redis 只允许本地连接。如果需要远程访问或修改其他配置，可以编辑 Redis 配置文件。

#### 5.1 打开配置文件
Redis 的配置文件通常位于 `/etc/redis/redis.conf`：
```bash
sudo nano /etc/redis/redis.conf
```

#### 5.2 允许远程访问
找到 `bind` 配置项，将其修改为：
```bash
bind 0.0.0.0
```
这样 Redis 将允许所有 IP 地址连接。

#### 5.3 设置密码（可选）
找到 `requirepass` 配置项，取消注释并设置密码：
```bash
requirepass your_password
```

#### 5.4 保存并重启 Redis
保存配置文件后，重启 Redis 服务以应用更改：
```bash
sudo systemctl restart redis-server
```

---

### 6. **防火墙设置（如果需要远程访问）**
如果服务器启用了防火墙，需要开放 Redis 的默认端口（6379）：
```bash
sudo ufw allow 6379
```

---

### 7. **验证远程连接（可选）**
从另一台机器使用 Redis 客户端连接 Redis 服务器：
```bash
redis-cli -h your_server_ip -p 6379
```
如果设置了密码，使用以下命令认证：
```bash
auth your_password
```

---

### 8. **卸载 Redis（可选）**
如果需要卸载 Redis，可以运行以下命令：
```bash
sudo apt remove --purge redis-server
sudo apt autoremove
```

---

通过以上步骤，你应该已经成功在 Ubuntu 上安装并配置了 Redis。如果有其他问题，请随时提问！