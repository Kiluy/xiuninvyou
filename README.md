# xiuninvyou · AI Companion

这是基于你提供 PRD 的持续落地版本（前后端 + MySQL）。

## 已落地能力（当前）

- 账号体系：注册 / 登录（多用户基础）
- 聊天页（Vue3 + TS + 毛玻璃 + 移动端适配）
- 多会话列表与切换（按用户隔离）
- 隐藏后台入口：`/sudo-admin-777`
- 后台系统配置管理
- AI 角色（Profile）管理：增删查（按用户隔离）
- 记忆金库（Memory Vault）管理：增删查（按用户隔离）
- OpenAI-Compatible 真实 LLM 接入（未配置 key 自动回退）
- 语音输入（录音 + ASR 占位）
- 场景关键词触发图片资产卡片
- 主动消息推送（会话空闲 30 分钟）

## 本地启动

```bash
# 1) MySQL
docker compose up -d mysql

# 2) Backend
cd backend
mvn spring-boot:run

# 3) Frontend
cd frontend
npm install
npm run dev
```

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`

## API（核心）

- 鉴权
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- 聊天
  - `POST /api/chat/sessions`
  - `GET /api/chat/sessions/{id}/messages`
  - `POST /api/chat/stream`
- 会话管理 `GET /api/sessions`
- 角色管理 `GET/POST/PUT/DELETE /api/profiles`
- 记忆金库 `GET/POST/DELETE /api/memory`
- 图片资产 `GET /api/assets/{sessionId}`
- 语音 `POST /api/voice/asr`

> 除 `/api/auth/*` 外，其它业务接口通过 `X-User-Id` 识别用户。
