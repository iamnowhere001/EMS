# 员工信息管理系统 (EMS)

> 基于 Spring Boot 3 + Vue 3 的企业级员工信息管理系统

## 功能特性

| 模块 | 功能 | 说明 |
|------|------|------|
| 员工管理 | 增删改查、批量操作 | 支持 Excel 导入导出、逻辑删除、排序拖拽 |
| 考勤管理 | 签到签退、工时计算 | 迟到早退判断、统计报表 |
| 薪资管理 | 薪资结构、工资计算 | 五险一金、个税自动计算 |
| 合同管理 | 合同信息、到期提醒 | 自动预警即将到期合同 |
| 试用期管理 | 试用期评估 | 自动提醒试用期结束 |
| 入转调离 | 入职、转正、调岗、离职 | 完整流程记录 |
| 组织架构 | 部门、岗位、职级管理 | 字典数据维护 |
| 用户管理 | 用户CRUD、角色权限 | admin/普通用户角色控制 |
| 数据看板 | 统计图表、数据可视化 | ECharts 数据展示 |
| 操作日志 | 完整审计记录 | 字段变更详情追踪 |

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.5
- **ORM**: MyBatis-Plus 3.5.6
- **认证**: JWT (JJWT 0.12.5)
- **缓存**: Redis + Caffeine (混合模式)
- **数据库**: MySQL 8.0+
- **Excel**: EasyExcel 3.3.4
- **监控**: Spring Boot Actuator

### 前端
- **框架**: Vue 3.4 + TypeScript
- **构建**: Vite 6.5
- **UI**: Element Plus 2.8
- **路由**: Vue Router 4
- **图表**: ECharts 5
- **拖拽排序**: SortableJS

### 部署
- **容器**: Docker + Docker Compose
- **环境**: Dev / Prod 配置分离

## 项目结构

```
EMS/
├── src/main/java/com/ems/          # 后端代码
│   ├── controller/                 # REST API 控制层
│   ├── service/                    # 业务逻辑层
│   ├── mapper/                     # 数据访问层
│   ├── entity/                     # 数据库实体
│   ├── dto/                        # 数据传输对象
│   ├── vo/                         # 视图对象
│   ├── config/                     # 配置类
│   ├── interceptor/                # 拦截器
│   └── common/                     # 通用工具
├── src/main/resources/
│   ├── application.yml             # 基础配置
│   ├── application-dev.yml         # 开发环境配置
│   ├── application-prod.yml        # 生产环境配置
│   └── db/schema.sql               # 数据库初始化脚本
├── ems-frontend/                   # 前端代码
│   ├── src/api/                    # API 接口
│   ├── src/components/             # 公共组件
│   ├── src/views/                  # 页面视图
│   ├── src/router/                 # 路由配置
│   └── src/utils/                  # 工具函数
├── Dockerfile                      # Docker 镜像构建
├── docker-compose.yml              # 容器编排
└── pom.xml                         # Maven 依赖管理
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+ (可选，Docker 模式自带)
- Redis 7+ (可选，Docker 模式自带)

### 方式一：Docker 一键部署

```bash
# 克隆项目
git clone https://github.com/iamnowhere001/EMS.git
cd EMS

# 打包项目
mvn package -DskipTests

# 启动容器
docker-compose up -d

# 查看服务状态
docker-compose ps
```

服务启动后访问：
- 前端: http://localhost:5173
- 后端: http://localhost:8080
- 健康检查: http://localhost:8080/actuator/health

### 方式二：本地开发

**后端启动**

```bash
# 创建数据库
mysql -u root -e "CREATE DATABASE IF NOT EXISTS ems_db DEFAULT CHARACTER SET utf8mb4;"

# 导入初始化脚本
mysql -u root ems_db < src/main/resources/db/schema.sql

# 启动后端
mvn spring-boot:run
```

**前端启动**

```bash
cd ems-frontend
npm install
npm run dev
```

## 默认账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |

## API 接口

### 认证模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |
| POST | /api/auth/refresh | 刷新 Token |
| PUT | /api/auth/changePassword | 修改密码 |

### 员工模块

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/employee/page | 分页查询员工 |
| GET | /api/employee/{id} | 获取员工详情 |
| POST | /api/employee | 新增员工 |
| PUT | /api/employee | 更新员工 |
| DELETE | /api/employee/{id} | 删除员工 |
| POST | /api/employee/import | 导入员工 |
| GET | /api/employee/export | 导出员工 |
| POST | /api/employee/sort | 排序更新 |
| POST | /api/employee/batch/transfer | 批量调岗 |
| POST | /api/employee/batch/salary | 批量调薪 |
| POST | /api/employee/batch/delete | 批量删除 |

### 薪资模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/salary/generate | 生成月度工资 |
| POST | /api/salary/confirm | 确认工资 |
| POST | /api/salary/batchConfirm | 批量确认 |
| POST | /api/salary/batchPay | 批量发放 |
| GET | /api/salary/record/page | 查询工资记录 |

### 考勤模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/attendance/checkIn | 签到 |
| POST | /api/attendance/checkOut | 签退 |
| GET | /api/attendance/page | 查询考勤记录 |

### 社保公积金模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/socialSecurity | 保存配置 |
| GET | /api/socialSecurity | 查询配置 |

### 健康检查

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /actuator/health | 健康状态 |
| GET | /actuator/info | 应用信息 |

## 环境变量

### 数据库配置
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_HOST | mysql | 数据库主机 |
| DB_PORT | 3306 | 数据库端口 |
| DB_NAME | ems_db | 数据库名 |
| DB_USERNAME | ems_user | 用户名 |
| DB_PASSWORD | - | 密码 |

### Redis 配置
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| REDIS_HOST | redis | Redis 主机 |
| REDIS_PORT | 6379 | Redis 端口 |
| REDIS_PASSWORD | - | Redis 密码 |

### JWT 配置
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| JWT_SECRET | - | JWT 密钥 |
| JWT_EXPIRATION | 86400000 | Token 过期时间(ms) |

### 加密配置
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| ENCRYPTION_KEY | - | AES 加密密钥(32位) |

### 缓存配置
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| CACHE_REDIS_ENABLED | true | 是否启用 Redis |
| CACHE_DICTIONARY_TTL | 600 | 字典缓存过期时间(s) |
| CACHE_STATISTICS_TTL | 300 | 统计缓存过期时间(s) |

## 安全特性

- ✅ JWT 认证 + Token 刷新机制
- ✅ 角色权限控制 (@RequireRole)
- ✅ 敏感字段 AES 加密存储 (身份证、银行卡)
- ✅ 数据脱敏显示
- ✅ 操作日志审计
- ✅ 逻辑删除防止数据误删
- ✅ 参数校验 (JSR-303)

## 开发规范

### 代码规范
- Java: 遵循 Google Java Style Guide
- TypeScript: 遵循 ESLint + Prettier

### 命名规范
- 类名: PascalCase
- 方法/变量: camelCase
- 常量: UPPER_SNAKE_CASE
- 数据库表: snake_case

### 提交规范
```
feat: 新增功能
fix: 修复 Bug
refactor: 重构代码
docs: 更新文档
style: 代码格式调整
test: 添加测试
chore: 构建/工具更新
```

## License

MIT License

## 联系方式

如有问题或建议，欢迎提交 Issue 或 PR。
