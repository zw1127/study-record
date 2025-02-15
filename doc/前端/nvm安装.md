    在 Windows 11 环境下，绿色安装 NVM（Node Version Manager）可以帮助你轻松管理多个 Node.js 版本。以下是具体步骤：

### 1. 下载 NVM
1. 访问 [nvm-windows](https://github.com/coreybutler/nvm-windows/releases) 的 GitHub 发布页面。
2. 下载最新版本的 `nvm-setup.zip` 或 `nvm-noinstall.zip`。

### 2. 安装 NVM
- **使用 `nvm-setup.zip`**：
    1. 解压 `nvm-setup.zip`。
    2. 运行 `nvm-setup.exe`，按照提示完成安装。

- **使用 `nvm-noinstall.zip`**：
    1. 解压 `nvm-noinstall.zip` 到指定目录，如 `C:\nvm`。
    2. 以管理员身份打开命令提示符，执行以下命令设置环境变量：
       ```cmd
       setx NVM_HOME "C:\nvm"
       setx NVM_SYMLINK "C:\Program Files\nodejs"
       setx PATH "%PATH%;%NVM_HOME%;%NVM_SYMLINK%"
       ```

### 3. 验证安装
1. 打开命令提示符，输入 `nvm version`，确认安装成功。

### 4. 安装 Node.js
1. 使用以下命令安装指定版本的 Node.js：
   ```cmd
   nvm install <version>
   ```
   例如，安装 Node.js 16：
   ```cmd
   nvm install 16
   ```

2. 使用以下命令切换 Node.js 版本：
   ```cmd
   nvm use <version>
   ```
   例如，切换到 Node.js 16：
   ```cmd
   nvm use 16
   ```

### 5. 验证 Node.js 安装
1. 输入 `node -v` 和 `npm -v`，确认 Node.js 和 npm 安装成功。

### 6. 常用 NVM 命令
- 查看已安装版本：
  ```cmd
  nvm list
  ```
- 卸载指定版本：
  ```cmd
  nvm uninstall <version>
  ```
- 设置默认版本：
  ```cmd
  nvm alias default <version>
  ```

### 注意事项
- 确保安装路径无空格和特殊字符。
- 安装或切换版本时可能需要管理员权限。

通过这些步骤，你可以在 Windows 11 上绿色安装并管理多个 Node.js 版本。

