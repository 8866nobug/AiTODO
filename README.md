# AI ToDo

AI ToDo 是一个基于 Spring Boot 3 与 LangChain4j 构建的智能待办管理后端。它不只是传统的任务 CRUD，而是把大模型能力接入任务创建、任务拆解、历史回顾、未来规划、语义检索和报告导出流程，让待办系统具备“理解目标、拆解计划、追踪上下文、生成总结”的能力。

## 项目亮点

### 1. 自然语言创建任务

用户可以直接输入类似“我要两个月内减到 120 斤”或“1 小时后取快递”的自然语言指令，系统会调用 AI 自动判断任务复杂度：

- 简单任务：补全标题、提醒时间、重要性等字段，生成一条可执行待办。
- 复杂目标：按阶段拆解为多个子任务，并补充具体行动、周期、量化目标和提醒时间。

这种设计让任务录入从“手动填表”升级为“表达目标”，更适合个人计划、学习规划、项目推进等场景。

### 2. AI Agent 任务管家

项目内置 AI Agent，对外提供流式对话接口 `/agent/chat`。Agent 通过 LangChain4j 工具调用能力，可以根据用户问题自动选择合适工具：

- 获取当前系统时间，准确处理“昨天”“下周”“上个月”等相对时间。
- 查询历史任务，用于复盘过去一段时间的执行情况。
- 查询未来任务，用于规划日程、检查提醒和 DDL。
- 检索历史知识，用于结合用户习惯和过往记录生成建议。
- 导出 PDF，将周报、月报、任务总结沉淀为正式文档。

这让系统从单纯的待办应用扩展为可以对话、分析、整理和输出的个人任务助手。

### 3. RAG 语义记忆检索

任务保存时，系统会将 AI 优化后的任务内容写入向量库，形成可检索的长期记忆。后续 Agent 在需要了解用户习惯、历史计划或上下文背景时，可以通过 Milvus 进行语义检索。

检索链路包含：

- 使用 Embedding 模型将任务内容向量化。
- 通过 Milvus 存储和召回历史任务片段。
- 结合时间衰减逻辑，让更近期的记录拥有更高优先级。
- 使用重排模型对候选内容进行二次排序，提高返回内容相关性。

相比普通关键词查询，这种方式更适合处理“我之前类似的计划是怎么安排的”“参考我过去的学习节奏”等模糊问题。

### 4. 多模型厂商动态适配

项目抽象了大模型配置工厂，支持从数据库读取模型配置并动态构建模型实例。目前预留支持：

- OpenAI
- 通义千问 Qwen
- DeepSeek
- Ollama 本地模型

这种设计降低了模型切换成本，可以根据实际环境选择云端模型、本地模型或 OpenAI 兼容接口。

### 5. 流式响应与实时状态反馈

AI Agent 聊天接口使用 SSE 返回流式内容，前端可以边生成边展示，减少等待感。

同时项目集成 WebSocket/STOMP，在工具执行过程中推送 Agent 状态，例如正在查询历史任务、正在语义检索、正在生成报告等，让用户能看到 AI 正在做什么，而不是面对一个无反馈的黑盒。

### 6. PDF 报告导出与自动清理

当用户明确要求生成周报、月报、总结文档时，Agent 会先查询真实任务数据，再进行内容整理，最后通过 PDFBox 生成 PDF 文件并返回下载链接。

项目还提供定时清理任务，每天自动清理过期 PDF 文件，避免导出文件长期堆积。

### 7. 标准任务与分类管理能力

除了 AI 能力，项目也保留了完整的待办基础能力：

- 任务新增、删除、更新、详情查询。
- 按分类查询任务。
- 查询今日任务、重要任务、过期任务。
- 分类新增、删除、更新、列表查询。
- Swagger 接口文档，便于调试和联调。

## 技术栈

- Java 21
- Spring Boot 3.5
- Spring Web / WebSocket / Scheduling
- LangChain4j
- MyBatis-Plus
- MySQL
- Redis
- Milvus
- PDFBox
- SpringDoc OpenAPI
- Maven

## 核心模块

```text
src/main/java/com/wanger/aitodo
├── ai
│   ├── factory      # 大模型与 AI Service 构建
│   ├── guardrail    # AI 输入安全防护
│   ├── memory       # Redis 对话记忆
│   ├── rag          # Milvus 向量库与 RAG 配置
│   ├── service      # LangChain4j AI 服务接口
│   └── tool         # Agent 可调用工具
├── config           # Web、WebSocket 等配置
├── controller       # REST API
├── mapper           # MyBatis-Plus Mapper
├── pojo             # DTO、Entity、AI VO、统一返回结果
├── service          # 业务服务
└── task             # 定时任务
```

## 快速开始

### 1. 准备环境

请先准备以下基础服务：

- JDK 21
- Maven 3.9+
- MySQL 8+
- Redis
- Milvus
- 可用的大模型 API Key，或本地 Ollama 服务

### 2. 配置应用

默认配置位于 `src/main/resources/application.yml`，支持通过环境变量覆盖常用连接信息：

```bash
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=123456
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
MILVUS_HOST=localhost
MILVUS_PORT=19530
```

大模型、Embedding、Rerank、Milvus 等配置也可以在配置文件中调整。生产环境建议将 API Key、数据库密码等敏感信息放入环境变量或安全配置中心。

### 3. 启动项目

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
./mvnw spring-boot:run
```

启动后默认访问地址：

- 后端服务：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 常用接口

### AI Agent

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/agent/chat?prompt=...` | 与 AI Agent 流式对话 |

### 任务模块

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/tasks/add` | 输入自然语言，AI 自动拆解并创建任务 |
| PUT | `/tasks/update` | 更新任务 |
| DELETE | `/tasks/delect/{taskId}/{categoryId}` | 删除任务及分类关联 |
| GET | `/tasks/get/{taskId}` | 查看任务详情 |
| GET | `/tasks/get/list/{categoryId}` | 按分类查询任务 |
| GET | `/tasks/get/today` | 查询今日任务 |
| GET | `/tasks/get/important` | 查询重要任务 |
| GET | `/tasks/get/overdue` | 查询过期任务 |

### 分类模块

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/categories/add` | 新增分类 |
| DELETE | `/categories/delete` | 删除分类 |
| PUT | `/categories/update` | 修改分类 |
| GET | `/categories/getAll` | 查询分类列表 |

### 模型配置

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/config/model` | 更新模型厂商、模型名称、API Key 等配置 |

## 使用示例

创建一个复杂任务：

```bash
curl -X POST http://localhost:8080/tasks/add \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"我想两个月内系统学习 Java 后端并做一个项目\",\"categoryId\":1}"
```

与 AI Agent 对话：

```bash
curl "http://localhost:8080/agent/chat?prompt=帮我总结一下最近一周的重要任务"
```

## 适用场景

- 个人待办管理
- 学习计划拆解
- 项目阶段规划
- 周报、月报、任务复盘生成
- 基于历史行为的个性化任务建议
- AI Agent 与业务系统结合的实践样例

## 项目定位

AI ToDo 的核心价值在于把“任务管理”与“大模型智能编排”结合起来：数据库负责保存结构化任务，向量库负责沉淀长期记忆，Agent 负责理解意图和调用工具，最终形成一个可持续学习用户习惯的智能任务管理后端。
