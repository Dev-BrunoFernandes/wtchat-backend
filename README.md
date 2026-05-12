# WTChat Backend

Backend do desafio WTC – FIAP 2025. Plataforma CRM de comunicação com clientes.

## Tecnologias

- Java 17 + Spring Boot 3.2
- Spring Security + JWT
- Spring Data MongoDB
- WebSocket (STOMP) para chat em tempo real
- Lombok

## Pré-requisitos

- Java 17+
- Maven 3.8+
- MongoDB rodando na porta 27017 (local ou Atlas)

## Executando localmente

```bash
# 1. Clone o projeto
git clone <repo-url>
cd wtchat-backend

# 2. Suba o MongoDB (se usar Docker)
docker run -d -p 27017:27017 --name mongo mongo:7

# 3. Execute
mvn spring-boot:run
```

O servidor sobe em `http://localhost:8080`.

## Variáveis de configuração

Arquivo: `src/main/resources/application.properties`

| Propriedade | Padrão | Descrição |
|---|---|---|
| `spring.data.mongodb.uri` | `mongodb://localhost:27017/wtchat` | URI do MongoDB |
| `jwt.secret` | (ver arquivo) | Chave secreta JWT (256 bits) |
| `jwt.expiration` | `86400` | Expiração do token em segundos (24h) |
| `server.port` | `8080` | Porta do servidor |

## Endpoints principais

### Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastra novo usuário |
| POST | `/auth/login` | Autentica e retorna JWT |

**Login body:**
```json
{ "email": "user@email.com", "password": "123456" }
```

**Register body:**
```json
{ "name": "Nome", "email": "user@email.com", "password": "123456", "role": "CLIENT" }
```

> `role` pode ser `CLIENT` ou `OPERATOR`

### Chat & Mensagens
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/messages` | Envia mensagem (também via WebSocket) | Sim |
| GET | `/inbox/{userId}` | Caixa de entrada | Sim |
| GET | `/conversation/{otherUserId}` | Conversa 1:1 | Sim |
| PATCH | `/messages/{id}/read` | Marca como lida | Sim |

### CRM – Clientes
| Método | Rota | Descrição | Role |
|---|---|---|---|
| GET | `/customers` | Lista clientes (`?search=&status=`) | AUTH |
| POST | `/customers` | Cria cliente | OPERATOR |
| PUT | `/customers/{id}` | Atualiza cliente | OPERATOR |
| DELETE | `/customers/{id}` | Remove cliente | OPERATOR |
| GET | `/customers/{id}/timeline` | Perfil 360° | AUTH |

### Segmentos
| Método | Rota | Descrição | Role |
|---|---|---|---|
| GET | `/segments` | Lista segmentos | AUTH |
| POST | `/segments` | Cria segmento | OPERATOR |
| PUT | `/segments/{id}` | Atualiza | OPERATOR |
| DELETE | `/segments/{id}` | Remove | OPERATOR |

### Campanhas
| Método | Rota | Descrição | Role |
|---|---|---|---|
| POST | `/campaigns` | Cria campanha | OPERATOR |
| POST | `/campaigns/{id}/send` | Dispara campanha | OPERATOR |
| POST | `/campaigns/{id}/schedule` | Agenda campanha | OPERATOR |
| GET | `/campaigns` | Lista campanhas | OPERATOR |

### Auditoria & Logs
| Método | Rota | Descrição | Role |
|---|---|---|---|
| GET | `/audit` | Todos os logs | OPERATOR |
| GET | `/audit/user/{userId}` | Logs por usuário | OPERATOR |

## WebSocket (Diferencial)

Endpoint: `ws://localhost:8080/ws-native/websocket`

Após conectar com STOMP + header `Authorization: Bearer <token>`:

- **Receber mensagens:** subscribe em `/user/{userId}/queue/messages`
- **Receber campanhas:** subscribe em `/user/{userId}/queue/campaigns`
- **Enviar mensagens:** send para `/app/chat.send`

## Modelo de Dados (MongoDB)

```
users         → id, name, email, password, role, createdAt
customers     → id, userId, name, email, phone, segmentId, tags[], score, status, notes[]
segments      → id, name, description, customerIds[]
messages      → id, senderId, recipientId, content, type, status, actions[], createdAt, readAt
campaigns     → id, title, body, url, segmentId, actions[], status, sentAt, createdBy
audit_logs    → id, userId, userEmail, action, resource, resourceId, details, timestamp
```

## Autenticação no Swagger/Postman

Adicione o header em todas as requisições autenticadas:
```
Authorization: Bearer <token_jwt>
```
