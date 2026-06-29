# 免费线上 Demo 部署

本项目提供了 `Dockerfile.demo` 和 `render.yaml`，用于部署一个可直接访问体验的免费 Demo 服务。

## 部署方案

- 平台：Render Free Web Service
- 服务形态：单容器部署
- 前端：Vue 构建后打包进 Spring Boot 静态资源
- 后端：Spring Boot
- 数据库：容器内 MariaDB，首次启动自动导入 `src/main/resources/db/schema.sql`
- 访问方式：Render 部署完成后会生成一个 `https://xxx.onrender.com` 链接

## 注意事项

- 这是演示环境，不适合生产数据。
- 免费服务空闲后会休眠，首次访问可能需要等待几十秒唤醒。
- 数据库在容器内运行，免费服务重建或重启后数据可能恢复为初始化演示数据。
- 生产部署仍建议使用独立 MySQL、Redis、对象存储和固定域名。

## Render 部署步骤

1. 将以下新增文件提交并推送到 GitHub：
   - `.dockerignore`
   - `Dockerfile.demo`
   - `render.yaml`
   - `scripts/demo-start.sh`
   - `docs/free-deploy.md`
   - `src/main/java/com/ems/controller/SpaForwardController.java`

2. 打开 Render：
   - https://render.com

3. 使用 GitHub 登录，并授权 Render 访问你的项目仓库。

4. 选择 `New` -> `Blueprint`。

5. 选择当前 GitHub 仓库，Render 会自动读取根目录下的 `render.yaml`。

6. 确认服务名为 `ems-demo`，套餐选择 `Free`，点击部署。

7. 等待构建完成后，Render 会提供访问链接，例如：

   ```text
   https://ems-demo.onrender.com
   ```

## 测试账号

部署完成后可使用 README 中的测试账号登录，例如：

```text
superadmin / 123456
hradmin / 123456
hrspecialist / 123456
deptmanager / 123456
安之 / 123456
徐勇_0001 / 123456
```

## 手动创建 Web Service

如果不使用 Blueprint，也可以手动创建：

- Runtime: Docker
- Dockerfile Path: `./Dockerfile.demo`
- Branch: `main`
- Health Check Path: `/actuator/health`
- Plan: Free

环境变量通常不需要手动配置，`Dockerfile.demo` 已提供 Demo 默认值。若要覆盖，可设置：

```text
SPRING_PROFILES_ACTIVE=prod
CACHE_REDIS_ENABLED=false
MANAGEMENT_HEALTH_REDIS_ENABLED=false
JWT_SECRET=自定义随机字符串
ENCRYPTION_KEY=32位AES密钥
```
