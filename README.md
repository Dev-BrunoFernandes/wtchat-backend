# WTChat Backend

Backend do desafio WTC – FIAP 2025. Plataforma de comunicação com clientes via chat em tempo real.

## Tecnologias

- Java 21 + Spring Boot 3.2.5
- Spring Security + JWT (stateless)
- Spring Data MongoDB (Atlas em produção / Embedded em desenvolvimento)
- WebSocket STOMP para mensagens em tempo real
- Lombok

## Pré-requisitos

- Java 21+
- Maven 3.8+

## Executando localmente

```bash
# 1. Clone o projeto
git clone <repo-url>
cd wtchat-backend

# 2. Execute com perfil local (MongoDB embedded — sem instalar nada)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

O servidor sobe em `http://localhost:8081`.

> O perfil `local` utiliza MongoDB embutido (flapdoodle), não requer instalação do MongoDB.

## Executando com MongoDB externo

```bash
# Defina as variáveis de ambiente
export MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net/wtchat"
export SPRING_PROFILES_ACTIVE=prod

mvn spring-boot:run
```

## Variáveis de ambiente (produção)

| Variável | Descrição |
|---|---|
| `MONGODB_URI` | URI completa do MongoDB Atlas |
| `JWT_SECRET` | Chave secreta JWT (mínimo 256 bits) |
| `PORT` | Porta do servidor (padrão: `8081`) |
| `SPRING_PROFILES_ACTIVE` | `prod` para Atlas, `local` para embedded |

## Endpoints

### Autenticação (`/auth`) — público

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastra novo usuário |
| POST | `/auth/login` | Autentica e retorna JWT |
| POST | `/auth/social` | Login/cadastro via provedor social |

**POST /auth/register**
```json
{ "name": "João Silva", "email": "joao@email.com", "password": "123456", "role": "CLIENT" }
```
> `role`: `CLIENT` ou `OPERATOR`

**POST /auth/login**
```json
{ "email": "joao@email.com", "password": "123456" }
```

**POST /auth/social**
```json
{ "provider": "google", "email": "joao@gmail.com", "name": "João Silva" }
```

**Resposta de autenticação (todos os endpoints acima):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "6649abc...",
  "name": "João Silva",
  "email": "joao@email.com",
  "role": "CLIENT"
}
```

---

### Usuários (`/users`) — requer JWT

| Método | Rota | Descrição |
|---|---|---|
| GET | `/users?search=termo` | Busca usuários por nome ou email |

---

### Mensagens — requer JWT

| Método | Rota | Descrição |
|---|---|---|
| POST | `/messages` | Envia mensagem |
| GET | `/inbox/{userId}` | Caixa de entrada do usuário |
| GET | `/conversation/{otherUserId}` | Conversa 1:1 com outro usuário |
| PATCH | `/messages/{id}/read` | Marca mensagem como lida |

**POST /messages**
```json
{ "recipientId": "6649abc...", "content": "Olá!", "type": "TEXT" }
```

---

### CRM – Clientes — requer JWT

| Método | Rota | Role | Descrição |
|---|---|---|---|
| GET | `/customers` | AUTH | Lista (`?search=&status=`) |
| POST | `/customers` | OPERATOR | Cria cliente |
| PUT | `/customers/{id}` | OPERATOR | Atualiza cliente |
| DELETE | `/customers/{id}` | OPERATOR | Remove cliente |
| GET | `/customers/{id}/timeline` | AUTH | Perfil 360° |

---

### Segmentos — requer JWT

| Método | Rota | Role |
|---|---|---|
| GET | `/segments` | AUTH |
| POST | `/segments` | OPERATOR |
| PUT | `/segments/{id}` | OPERATOR |
| DELETE | `/segments/{id}` | OPERATOR |

---

### Campanhas — requer JWT OPERATOR

| Método | Rota | Descrição |
|---|---|---|
| POST | `/campaigns` | Cria campanha |
| POST | `/campaigns/{id}/send` | Dispara campanha para segmento |
| GET | `/campaigns` | Lista campanhas |

---

### Auditoria — requer JWT OPERATOR

| Método | Rota | Descrição |
|---|---|---|
| GET | `/audit` | Todos os logs |
| GET | `/audit/user/{userId}` | Logs por usuário |

---

## WebSocket (tempo real)

**Endpoint:** `wss://web-production-d961e.up.railway.app/ws-native/websocket`

Após conectar com cliente STOMP, enviar header:
```
Authorization: Bearer <token>
```

| Operação | Destino STOMP |
|---|---|
| Enviar mensagem | `/app/chat.send` |
| Receber mensagens | `/user/{userId}/queue/messages` |
| Receber campanhas | `/user/{userId}/queue/campaigns` |

---

## Modelo de Dados (MongoDB)

```
users        → id, name, email, password(hash), role, createdAt
messages     → id, senderId, recipientId, content, type, status, actions[], createdAt, readAt
customers    → id, name, email, phone, segmentId, tags[], score, status, notes[]
segments     → id, name, description, customerIds[]
campaigns    → id, title, body, url, segmentId, actions[], status, sentAt, createdBy
audit_logs   → id, userId, userEmail, action, resource, resourceId, details, timestamp
```

## Autenticação nas requisições

Adicione o header em todas as rotas protegidas:
```
Authorization: Bearer <token_jwt>
```

## Deploy em produção

O backend está publicado em: `https://web-production-d961e.up.railway.app`

Plataforma: **Railway** | Banco: **MongoDB Atlas** (região São Paulo)
