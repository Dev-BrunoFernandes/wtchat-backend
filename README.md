# WTChat Backend

Backend do desafio WTC – FIAP 2025. Plataforma de comunicação com clientes via chat em tempo real.

## Tecnologias

- Java 21 + Spring Boot 3.2.5
- Spring Security + JWT (stateless, 24h)
- Spring Data MongoDB (Atlas em produção / embutido em desenvolvimento)
- WebSocket STOMP para mensagens em tempo real
- Lombok

---

## Pré-requisitos

- [Java 21+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)

> Não é necessário instalar o MongoDB para rodar localmente — o perfil `local` usa MongoDB embutido.

---

## Executando localmente

```bash
# 1. Clone o repositório
git clone https://github.com/Dev-BrunoFernandes/wtchat-backend.git
cd wtchat-backend

# 2. Execute com perfil local (MongoDB embutido)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

O servidor sobe em `http://localhost:8081`

> **Primeira execução:** o Maven baixa automaticamente o binário do MongoDB (~300 MB).  
> A partir da segunda execução o startup é imediato.

---

## Executando com MongoDB Atlas (produção local)

Defina as variáveis de ambiente antes de rodar:

**Linux / macOS:**
```bash
export MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net/wtchat"
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

**Windows (PowerShell):**
```powershell
$env:MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net/wtchat"
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run
```

**Windows (Prompt de Comando):**
```cmd
set MONGODB_URI=mongodb+srv://usuario:senha@cluster.mongodb.net/wtchat
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `local` | `local` = MongoDB embutido \| `prod` = Atlas |
| `MONGODB_URI` | — | URI do MongoDB Atlas (obrigatório no perfil `prod`) |
| `JWT_SECRET` | chave interna | Chave secreta JWT (mínimo 256 bits) |
| `PORT` | `8081` | Porta HTTP do servidor |

---

## Endpoints

> Rotas marcadas com **🔒** exigem header `Authorization: Bearer <token>`.  
> Rotas marcadas com **🔑 OPERATOR** exigem token com role `OPERATOR`.

### Autenticação — público

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastra novo usuário |
| POST | `/auth/login` | Autentica e retorna JWT |
| POST | `/auth/social` | Login/cadastro via provedor social (Google, LinkedIn, Facebook) |

**POST /auth/register**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "123456",
  "role": "CLIENT"
}
```
> `role` aceita: `CLIENT` ou `OPERATOR`

**POST /auth/login**
```json
{
  "email": "joao@email.com",
  "password": "123456"
}
```

**POST /auth/social**
```json
{
  "provider": "google",
  "email": "joao@gmail.com",
  "name": "João Silva"
}
```
> `provider` aceita: `google`, `linkedin`, `facebook`

**Resposta (todos os endpoints de autenticação):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "6649abc123...",
  "name": "João Silva",
  "email": "joao@email.com",
  "role": "CLIENT"
}
```

---

### Usuários 🔒

| Método | Rota | Descrição |
|---|---|---|
| GET | `/users?search=termo` | Busca usuários por nome ou e-mail |

---

### Mensagens 🔒

| Método | Rota | Descrição |
|---|---|---|
| POST | `/messages` | Envia mensagem |
| GET | `/inbox/{userId}` | Caixa de entrada do usuário |
| GET | `/conversation/{otherUserId}` | Conversa 1:1 com outro usuário |
| PATCH | `/messages/{id}/read` | Marca mensagem como lida |

**POST /messages**
```json
{
  "recipientId": "6649abc123...",
  "content": "Olá!",
  "type": "TEXT"
}
```

---

### Clientes 🔒

| Método | Rota | Role | Descrição |
|---|---|---|---|
| GET | `/customers` | Qualquer | Lista clientes (`?search=&status=`) |
| POST | `/customers` | OPERATOR | Cria cliente |
| PUT | `/customers/{id}` | OPERATOR | Atualiza cliente |
| DELETE | `/customers/{id}` | OPERATOR | Remove cliente |
| GET | `/customers/{id}/timeline` | Qualquer | Perfil 360° do cliente |

---

### Segmentos 🔒

| Método | Rota | Role |
|---|---|---|
| GET | `/segments` | Qualquer |
| POST | `/segments` | OPERATOR |
| PUT | `/segments/{id}` | OPERATOR |
| DELETE | `/segments/{id}` | OPERATOR |

---

### Campanhas 🔑 OPERATOR

| Método | Rota | Descrição |
|---|---|---|
| POST | `/campaigns` | Cria campanha |
| POST | `/campaigns/{id}/send` | Dispara campanha para o segmento vinculado |
| GET | `/campaigns` | Lista campanhas |

---

### Auditoria 🔑 OPERATOR

| Método | Rota | Descrição |
|---|---|---|
| GET | `/audit` | Todos os logs de auditoria |
| GET | `/audit/user/{userId}` | Logs de um usuário específico |

---

## WebSocket (mensagens em tempo real)

**Endpoint local:** `ws://localhost:8081/ws-native/websocket`  
**Endpoint produção:** `wss://web-production-d961e.up.railway.app/ws-native/websocket`

Conectar com cliente STOMP enviando o header:
```
Authorization: Bearer <token_jwt>
```

| Operação | Destino STOMP |
|---|---|
| Enviar mensagem | `/app/chat.send` |
| Receber mensagens em tempo real | `/user/{userId}/queue/messages` |
| Receber campanhas em tempo real | `/user/{userId}/queue/campaigns` |

---

## Modelo de Dados (MongoDB)

| Coleção | Campos principais |
|---|---|
| `users` | `id`, `name`, `email`, `password` (hash bcrypt), `role`, `createdAt` |
| `messages` | `id`, `senderId`, `recipientId`, `content`, `type`, `status`, `createdAt`, `readAt` |
| `customers` | `id`, `name`, `email`, `phone`, `segmentId`, `tags[]`, `score`, `status`, `notes[]` |
| `segments` | `id`, `name`, `description`, `customerIds[]` |
| `campaigns` | `id`, `title`, `body`, `url`, `segmentId`, `status`, `sentAt`, `createdBy` |
| `audit_logs` | `id`, `userId`, `userEmail`, `action`, `resource`, `resourceId`, `details`, `timestamp` |

---

## Usando as rotas protegidas (Postman / Insomnia)

1. Faça POST em `/auth/login` ou `/auth/register`
2. Copie o campo `token` da resposta
3. Em todas as outras requisições, adicione o header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Deploy em produção

| Item | Valor |
|---|---|
| URL | `https://web-production-d961e.up.railway.app` |
| Plataforma | Railway |
| Banco de dados | MongoDB Atlas (região São Paulo) |
| Java | 21 (Nixpacks) |
