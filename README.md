# xiuninvyou · AI Companion

这是基于你提供 PRD 的持续落地版本（前后端 + MySQL）。

## 已落地能力（当前）

- 聊天页（Vue3 + TS + 毛玻璃 + 移动端适配）
- 多会话列表与切换
- 隐藏后台入口：`/sudo-admin-777`
- 后台系统配置管理
- AI 角色（Profile）管理：增删查
- 记忆金库（Memory Vault）管理：增删查
- Spring Boot 3 API + MySQL 持久化
- SSE 流式回复（已支持 OpenAI-Compatible 真实 LLM，未配置 key 时自动回退 mock）

## 目录

- `frontend/` 前端工程
- `backend/` 后端工程
- `db/schema.sql` 数据库建表
- `docs/REQUIREMENTS.md` 完整需求文档

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

- 聊天
  - `POST /api/chat/sessions`
  - `GET /api/chat/sessions/{id}/messages`
  - `POST /api/chat/stream`
- 会话管理
  - `GET /api/sessions`
  - `PUT /api/sessions/{id}/title`
  - `DELETE /api/sessions/{id}`
- 系统配置
  - `GET /api/admin/config`
  - `PUT /api/admin/config`
- 角色管理
  - `GET /api/profiles`
  - `POST /api/profiles`
  - `PUT /api/profiles/{id}`
  - `DELETE /api/profiles/{id}`
- 记忆金库
  - `GET /api/memory`
  - `POST /api/memory`
  - `DELETE /api/memory/{id}`
- 图片资产
  - `GET /api/assets/{sessionId}`
  - `POST /api/assets/generate`
- 语音
  - `POST /api/voice/asr`
  - `POST /api/voice/tts`

## 真实模型接入说明

在后台 `/admin` 配置：

- `API Base URL`（例如 `https://api.deepseek.com` 或 OpenAI 兼容网关）
- `API Key`
- `modelName`（例如 `deepseek-chat`）

保存后聊天将调用真实模型；未配置 API Key 时自动使用本地回退回复。

## 下一步（我可以继续直接做）

1. 接入真实第三方 ASR / TTS 服务（替换占位接口）
2. 接入真实图像生成供应商（Leonardo / SeaArt）
3. 增加鉴权与多用户
4. 会话与记忆的权限隔离
