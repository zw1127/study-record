使用 **NVM**（Node Version Manager）安装前端环境主要是通过 NVM 来管理 Node.js 版本，然后利用 Node.js 自带的 **npm**（Node Package Manager）或 **yarn** 来安装前端开发所需的工具和依赖。以下是详细步骤：

---

### 1. 安装 NVM
如果你还没有安装 NVM，请参考之前的步骤安装 NVM。

---

### 2. 使用 NVM 安装 Node.js
Node.js 是前端开发的基础环境，它自带了 npm（Node Package Manager），可以用来安装前端工具和依赖。

1. 打开命令提示符（CMD）或 PowerShell。
2. 使用以下命令安装指定版本的 Node.js：
   ```bash
   nvm install <version>
   ```
   例如，安装最新的 LTS 版本（长期支持版本）：
   ```bash
   nvm install 18
   ```
   或者安装最新版本：
   ```bash
   nvm install latest
   ```

3. 安装完成后，使用以下命令切换到刚安装的版本：
   ```bash
   nvm use <version>
   ```
   例如：
   ```bash
   nvm use 18
   ```

4. 验证 Node.js 和 npm 是否安装成功：
   ```bash
   node -v
   npm -v
   ```

---

### 3. 安装前端工具
使用 npm 或 yarn 安装前端开发所需的工具和依赖。

#### 安装全局工具
1. **安装 yarn**（可选，npm 的替代工具）：
   ```bash
   npm install -g yarn
   ```

2. **安装前端构建工具**：
    - 例如，安装 `webpack`：
      ```bash
      npm install -g webpack webpack-cli
      ```
    - 或者安装 `vite`：
      ```bash
      npm install -g vite
      ```

3. **安装脚手架工具**：
    - 例如，安装 `create-react-app`：
      ```bash
      npm install -g create-react-app
      ```
    - 或者安装 `vue-cli`：
      ```bash
      npm install -g @vue/cli
      ```

---

### 4. 创建前端项目
使用脚手架工具快速创建一个前端项目。

#### 使用 `create-react-app` 创建 React 项目
1. 运行以下命令：
   ```bash
   npx create-react-app my-react-app
   ```
2. 进入项目目录：
   ```bash
   cd my-react-app
   ```
3. 启动开发服务器：
   ```bash
   npm start
   ```

#### 使用 `vite` 创建 Vue 项目
1. 运行以下命令：
   ```bash
   npm create vite@latest my-vue-app --template vue
   ```
2. 进入项目目录：
   ```bash
   cd my-vue-app
   ```
3. 安装依赖：
   ```bash
   npm install
   ```
4. 启动开发服务器：
   ```bash
   npm run dev
   ```

---

### 5. 安装项目依赖
在项目目录中，运行以下命令安装项目所需的依赖：
```bash
npm install
```
或者使用 yarn：
```bash
yarn install
```

---

### 6. 运行项目
根据项目类型，运行以下命令启动开发服务器：
- React 项目：
  ```bash
  npm start
  ```
- Vue 项目：
  ```bash
  npm run dev
  ```
- 其他项目：
  查看项目的 `package.json` 文件中的 `scripts` 部分，找到对应的启动命令。

---

### 7. 切换 Node.js 版本（可选）
如果你需要为不同的项目使用不同的 Node.js 版本，可以使用 NVM 切换版本。

1. 查看已安装的 Node.js 版本：
   ```bash
   nvm list
   ```

2. 切换到指定版本：
   ```bash
   nvm use <version>
   ```

3. 如果没有安装目标版本，可以先安装：
   ```bash
   nvm install <version>
   ```

---

### 8. 常用命令总结
- **NVM 命令**：
    - 安装 Node.js：`nvm install <version>`
    - 切换版本：`nvm use <version>`
    - 查看已安装版本：`nvm list`
    - 设置默认版本：`nvm alias default <version>`

- **npm 命令**：
    - 安装依赖：`npm install`
    - 安装全局工具：`npm install -g <package>`
    - 运行项目：`npm start` 或 `npm run dev`

- **yarn 命令**：
    - 安装依赖：`yarn install`
    - 安装全局工具：`yarn global add <package>`
    - 运行项目：`yarn start` 或 `yarn dev`

---

通过以上步骤，你可以使用 NVM 安装并管理前端开发环境，快速创建和运行前端项目。