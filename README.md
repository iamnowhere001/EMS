# EMS · Employee Management System

<p align="center">
  <b>一套覆盖员工档案、组织架构、考勤请假、入转调离、薪资社保、权限审计的数据化人事管理系统。</b>
</p>

<p align="center">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img alt="Vue" src="https://img.shields.io/badge/Vue-3.5-42B883?style=for-the-badge&logo=vuedotjs&logoColor=white">
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-6-3178C6?style=for-the-badge&logo=typescript&logoColor=white">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img alt="Redis" src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white">
</p>

<p align="center">
  <code>Spring Boot 3</code> · <code>Vue 3</code> · <code>Element Plus</code> · <code>JWT RBAC</code> · <code>MyBatis-Plus</code> · <code>Redis + Caffeine</code>
</p>

---

## 项目概览

EMS 是一个面向中小型企业 HR 场景的员工信息管理平台。它不是单纯的 CRUD 系统，而是围绕“员工全生命周期”设计：从员工入职建档、组织关系维护、日常考勤请假，到调岗调薪、转正离职、工资社保、权限审计，形成一条完整的人事业务闭环。

系统同时支持管理端和员工端：

| 入口 | 使用对象 | 核心目标 |
|---|---|---|
| 管理端 | 超级管理员、HR 管理员、HR 专员、部门经理 | 管理组织、员工、流程、考勤、请假、薪资、账号和权限 |
| 员工端 | 普通员工 | 查看个人资料、考勤记录、工资信息权限内内容，完成打卡和请假申请 |

## 亮点能力

| 能力 | 说明 |
|---|---|
| RBAC 权限模型 | 预设 `SUPER_ADMIN`、`HR_ADMIN`、`HR_SPECIALIST`、`DEPT_MANAGER`、`EMPLOYEE` 五类角色，菜单和接口统一鉴权 |
| 员工默认账号 | 存量员工可批量生成默认账号，新增员工自动生成账号；重名员工使用 `姓名_工号` 避免冲突 |
| 员工自助工作台 | 普通员工只展示个人中心、考勤打卡、请假申请，避免看到无关管理模块 |
| 入转调离台账 | 支持调岗、调薪、转正、离职变更记录，形成可追溯的人事流程历史 |
| 薪资社保一体化 | 薪资结算、个税计算、工资确认/发放、社保公积金配置统一在薪资社保模块 |
| 数据看板 | 用图表展示员工分布、入职趋势、薪资区间、年龄、工龄、学历、职级等统计指标 |
| 安全加固 | JWT、登录失败锁定、接口限流、AES 敏感字段加密、手机号哈希唯一约束、文件上传校验 |
| 审计追踪 | 操作日志记录字段级变更，支持按模块、操作类型、操作人、时间范围过滤 |
| 缓存降级 | Redis + Caffeine 混合缓存，Redis 不可用时自动降级本地缓存 |
| 深浅色适配 | 前端支持浅色/暗色模式，个人中心和全局布局已适配主题变量 |

## 业务说明

### 员工生命周期

```text
员工建档 -> 自动生成账号 -> 分配部门/岗位/职级 -> 日常考勤/请假
        -> 调岗/调薪/转正/离职 -> 变更台账 -> 操作日志审计
```

### 权限与数据范围

| 角色 | 业务定位 | 可访问范围 |
|---|---|---|
| `SUPER_ADMIN` | 系统最高权限 | 所有菜单、所有接口、角色权限配置 |
| `HR_ADMIN` | 人事负责人 | 人事、组织、考勤、请假、薪资、系统用户，不维护角色权限 |
| `HR_SPECIALIST` | HR 专员 | 员工资料、考勤、请假、流程管理，不查看薪资和系统设置 |
| `DEPT_MANAGER` | 部门经理 | 员工基础信息、考勤和请假审批、流程台账查看 |
| `EMPLOYEE` | 普通员工 | 仅本人个人中心、本人考勤、请假申请/撤销 |

### 数据安全规则

| 规则 | 实现 |
|---|---|
| 身份证、银行卡 | AES 加密存储 |
| 手机号唯一 | SHA-256 哈希字段做数据库唯一约束 |
| 员工删除 | 逻辑删除，查询统一过滤 `deleted = 0` |
| 非法越权 | 后端根据登录用户和权限做强制拦截 |
| 登录爆破 | 5 次失败后锁定 30 分钟 |
| API 滥用 | Redis 限流，默认 60 次/分钟 |

## 功能模块

| 模块 | 页面入口 | 功能清单 |
|---|---|---|
| 个人中心 | `/personal` | 个人档案、资料完整度、联系方式维护、我的考勤、我的请假、工资条权限展示 |
| 数据看板 | `/dashboard` | KPI 指标、部门分布、入职趋势、薪资统计、年龄/工龄/学历/职级分析 |
| 员工管理 | `/employee` | 员工分页、详情、创建、编辑、删除、导入导出、批量调岗、批量调薪、拖拽排序 |
| 组织设置 | `/organization` | 部门树、岗位字典、职级字典、系统基础字典维护 |
| 流程管理 | `/workflow` | 入转调离台账、调岗、调薪、转正、离职记录查询与撤销 |
| 考勤管理 | `/attendance` | 签到签退、本人/员工考勤查询、状态统计、迟到早退记录 |
| 请假管理 | `/leave` | 请假提交、审批、拒绝、撤销、本人/管理视角列表 |
| 薪资社保 | `/salary` | 薪资生成、确认、发放、个税计算、社保公积金配置 |
| 系统设置 | `/system` | 用户管理、账号状态、操作日志查询 |
| 角色管理 | `/role-manage` | 角色列表、权限树分配、角色启停 |

## 技术架构

```text
┌────────────────────────────────────────────────────────────┐
│ Frontend: Vue 3 + TypeScript + Vite + Element Plus          │
│ - Router Guard  - RBAC Menu  - Axios Interceptor  - Theme   │
└──────────────────────────────┬─────────────────────────────┘
                               │ HTTP / JSON
┌──────────────────────────────▼─────────────────────────────┐
│ Backend: Spring Boot 3 + MyBatis-Plus + JWT + AOP           │
│ - Permission Aspect - Rate Limit - Audit Log - Cache Layer  │
└───────────────┬──────────────────────────────┬─────────────┘
                │                              │
┌───────────────▼──────────────┐   ┌───────────▼─────────────┐
│ MySQL 8                       │   │ Redis 7 + Caffeine       │
│ business data / RBAC / logs   │   │ cache / rate limit       │
└───────────────────────────────┘   └─────────────────────────┘
```

### 后端栈

| 技术 | 版本/说明 |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| MyBatis-Plus | 3.5.6 |
| MySQL | 8.0+ |
| Redis | 7+ |
| JWT | JJWT 0.12.5 |
| Excel | EasyExcel 3.3.4 |
| Cache | Redis + Caffeine |
| Monitor | Spring Boot Actuator |

### 前端栈

| 技术 | 版本/说明 |
|---|---|
| Vue | 3.5.x |
| TypeScript | 6.x |
| Vite | 8.x |
| Element Plus | 2.14.x |
| Vue Router | 5.x |
| ECharts | 6.x |
| SortableJS | 1.15.x |

## 项目结构

```text
.
├── src/main/java/com/ems
│   ├── aspect                 # 权限切面、日志切面
│   ├── common                 # Result、异常、JWT、权限注解等
│   ├── config                 # Web、缓存、文件、安全配置
│   ├── controller             # REST API
│   ├── dto                    # 请求 DTO
│   ├── entity                 # MyBatis-Plus 实体
│   ├── interceptor            # 登录、限流拦截器
│   ├── mapper                 # Mapper 接口
│   ├── service                # 业务服务
│   └── vo                     # 响应 VO
├── src/main/resources
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── db/schema.sql
│   └── mapper                 # MyBatis XML
├── ems-frontend
│   ├── src/api                # 前端 API 封装
│   ├── src/assets             # 全局样式、主题变量
│   ├── src/components         # 复用组件
│   ├── src/router             # 路由和权限守卫
│   ├── src/utils              # 请求、权限、格式化工具
│   └── src/views              # 页面模块
├── migrate_rbac.sql           # RBAC 角色权限迁移脚本
├── migrate_employee_accounts.sql
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## 快速开始

### 环境要求

| 环境 | 要求 |
|---|---|
| macOS | 已验证，推荐 Homebrew 安装依赖 |
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | `^22.18.0` 或 `>=24.12.0` |
| MySQL | 8.0+，本地开发默认 `root` 无密码 |
| Redis | 7+，可选；不可用时业务缓存会降级 |

### 本地开发启动

1. 创建数据库并导入结构。

```bash
mysql -uroot -e "CREATE DATABASE IF NOT EXISTS ems_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -uroot ems_db < src/main/resources/db/schema.sql
mysql -uroot ems_db < migrate_rbac.sql
mysql -uroot ems_db < migrate_employee_accounts.sql
```

2. 启动 Redis。

```bash
redis-server
```

如果本机没有 Redis，也可以临时关闭 Redis 缓存：

```bash
export CACHE_REDIS_ENABLED=false
```

3. 启动后端。

```bash
mvn spring-boot:run
```

4. 启动前端。

```bash
cd ems-frontend
npm install
npm run dev
```

5. 访问系统。

| 服务 | 地址 |
|---|---|
| 前端 | `http://localhost:5173` |
| 后端 API | `http://localhost:8080` |
| 健康检查 | `http://localhost:8080/actuator/health` |

### Docker 启动

```bash
mvn package -DskipTests
docker-compose up -d
docker-compose ps
```

Docker Compose 会启动 MySQL、Redis 和后端服务。前端开发服务仍建议在 `ems-frontend` 下用 `npm run dev` 启动。

### 免费线上 Demo

仓库已提供 Render 免费部署配置，适合生成一个可直接访问体验的公开链接：

```text
Dockerfile.demo
render.yaml
docs/free-deploy.md
```

部署完成后，Render 会生成类似 `https://ems-demo.onrender.com` 的访问地址。详细步骤见 [免费线上 Demo 部署](docs/free-deploy.md)。

## 测试账号

默认密码均为 `123456`。

| 用户名 | 密码 | 角色 | 建议体验路径 |
|---|---|---|---|
| `superadmin` | `123456` | 超级管理员 | 全量菜单、角色管理、系统设置 |
| `admin` | `123456` | 超级管理员 | 历史管理员账号，拥有最高权限 |
| `hradmin` | `123456` | HR 管理员 | 员工、组织、流程、考勤、请假、薪资、用户管理 |
| `hrspecialist` | `123456` | HR 专员 | 员工资料、流程、考勤、请假，不看薪资 |
| `deptmanager` | `123456` | 部门经理 | 员工查看、流程查看、考勤和请假审批 |
| `安之` | `123456` | 普通员工 | 个人中心、考勤打卡、请假申请 |
| `徐勇_0001` | `123456` | 普通员工 | 重名账号规则示例：`姓名_工号` |

员工账号规则：

```text
姓名唯一：账号名 = 员工姓名
姓名重复：账号名 = 员工姓名_员工工号
默认密码：123456
默认角色：EMPLOYEE
```

## 常用命令

```bash
# 后端测试
mvn test

# 后端完整清理测试
mvn clean test

# 前端类型检查 + 构建
cd ems-frontend
npm run build

# 查看数据库角色权限
mysql -uroot ems_db -e "SELECT code, name FROM sys_permission ORDER BY sort_order;"

# 查看应用健康状态
curl http://localhost:8080/actuator/health
```

## 关键 API

| 模块 | API 前缀 | 说明 |
|---|---|---|
| 登录认证 | `/api/auth` | 登录、登出、刷新 Token、修改密码 |
| 员工档案 | `/api/employee` | 员工 CRUD、导入导出、批量操作、个人资料 |
| 组织架构 | `/api/department` | 部门树和组织维护 |
| 字典配置 | `/api/dictionary` | 岗位、职级、基础字典 |
| 入转调离 | `/api/workflow` | 调岗、调薪、转正、离职台账 |
| 考勤 | `/api/attendance` | 签到签退、考勤分页、统计 |
| 请假 | `/api/leave` | 请假提交、审批、拒绝、撤销 |
| 薪资 | `/api/salary` | 薪资结构、工资记录、确认、发放 |
| 社保公积金 | `/api/socialSecurity` | 社保公积金配置 |
| 数据看板 | `/api/dashboard` | 统计图表数据 |
| 系统用户 | `/api/user` | 用户管理 |
| 角色权限 | `/api/role`、`/api/permission` | 角色和权限树 |
| 操作日志 | `/api/operation-log` | 审计日志查询 |

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Spring 运行环境 |
| `DB_HOST` | `localhost` / Docker: `mysql` | 数据库主机 |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `ems_db` | 数据库名 |
| `DB_USERNAME` | dev: `root` / Docker: `ems_user` | 数据库用户名 |
| `DB_PASSWORD` | dev 为空 | 数据库密码 |
| `REDIS_HOST` | `localhost` | Redis 主机 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | 空 | Redis 密码 |
| `JWT_SECRET` | 开发默认值 | JWT 签名密钥，生产必须替换 |
| `JWT_EXPIRATION` | `86400000` | Access Token 有效期，单位毫秒 |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh Token 有效期，单位毫秒 |
| `ENCRYPTION_KEY` | 开发默认值 | AES 加密密钥，生产必须替换 |
| `CACHE_REDIS_ENABLED` | `true` | 是否启用 Redis 缓存 |
| `CACHE_DICTIONARY_TTL` | `600` | 字典缓存 TTL，单位秒 |
| `CACHE_STATISTICS_TTL` | `300` | 统计缓存 TTL，单位秒 |
| `SECURITY_LOGIN_MAX_ATTEMPTS` | `5` | 登录失败锁定阈值 |
| `SECURITY_LOGIN_LOCK_DURATION` | `1800` | 锁定时长，单位秒 |
| `SECURITY_RATE_LIMIT_MAX_REQUESTS` | `60` | 单用户每分钟请求上限 |

## 安全与合规

| 安全项 | 状态 |
|---|---|
| JWT 登录认证 | 已支持 |
| 接口级权限注解 `@RequiresPermission` | 已支持 |
| 菜单级权限控制 | 已支持 |
| 登录失败锁定 | 已支持 |
| API 访问限流 | 已支持 |
| 身份证/银行卡 AES 加密 | 已支持 |
| 手机号哈希唯一约束 | 已支持 |
| 前端敏感信息脱敏 | 已支持 |
| 操作日志审计 | 已支持 |
| 文件上传白名单、大小限制、内容校验 | 已支持 |
| Redis 异常降级 | 已支持 |

## 开发约定

| 类型 | 约定 |
|---|---|
| Java 类名 | `PascalCase` |
| Java 方法/变量 | `camelCase` |
| TypeScript 变量 | `camelCase` |
| 常量 | `UPPER_SNAKE_CASE` |
| 数据库表/字段 | `snake_case` |
| 逻辑删除 | 查询默认过滤 `deleted = 0` |
| 员工工号 | 4 位数字，`00` 开头，系统自动生成 |
| 统计 SQL | 聚合字段别名统一使用 `name`、`value` |

提交信息建议使用 Conventional Commits：

```text
feat: 新增功能
fix: 修复问题
docs: 更新文档
style: 样式调整
refactor: 重构代码
test: 添加或更新测试
chore: 工程配置调整
```

## 验证状态

当前项目已通过：

```bash
npm run build
mvn clean test
```

说明：前端构建可能出现来自第三方 `@vueuse/core` 的 `#__PURE__` 注释警告和 chunk 体积提示，不影响构建结果。

---

<p align="center">
  <b>EMS</b> · 让员工数据、权限边界和人事流程都清清楚楚。
</p>

## License

MIT License

## 联系方式

如有问题或建议，欢迎提交 Issue 或 PR。
