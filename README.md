# xiuninvyou · AI Companion MVP

这是根据 PRD 落地的第一版可运行实现（前后端 + MySQL + SSE 流式聊天 + 隐藏后台入口）。

## 当前已实现

- Vue 3 + TypeScript 前端聊天页（毛玻璃风格、移动端响应式）
- 隐藏指令 `/sudo-admin-777` 进入后台配置页
- Spring Boot 3 后端 API
- MySQL 持久化（会话、消息、系统配置）
- SSE 流式回复（当前为 mock 文本流，预留接入真实 LLM）
- 规范化 DDL 脚本与 `docker-compose` 本地启动 MySQL

## 目录结构

- `frontend/`：Vue 3 应用
- `backend/`：Spring Boot 3 服务
- `db/schema.sql`：数据库表结构
- `docs/REQUIREMENTS.md`：完整需求文档

## 本地运行

### 1) 启动 MySQL

```bash
docker compose up -d mysql
```

### 2) 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`

### 3) 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`

## 主要接口

- `POST /api/chat/sessions` 创建会话
- `GET /api/chat/sessions/{id}/messages` 查询消息
- `POST /api/chat/stream` SSE 流式回复
- `GET /api/admin/config` 获取系统配置
- `PUT /api/admin/config` 更新系统配置

## 下一步建议

1. 接入真实 LLM（DeepSeek / Kimi / Gemini / OpenAI 兼容）
2. 增加 AI Profile（多角色）数据模型
3. 增加用户体系、权限与 API Key 安全存储
4. 扩展 ASR / TTS 与图片生成链路
