# Trabalho Final — Etapa 3

Trabalho para aula de Desenvolvimento Web3.

## Pré-requisitos

* Java 17+
* Maven 3.8+
* MySQL 8+
* Node.js 18+
* Conta gratuita no CloudAMQP
* Conta Gmail com senha de aplicativo configurada
* Postman

---

## Configuração do banco de dados

Execute o script abaixo no MySQL:

```sql
CREATE DATABASE IF NOT EXISTS ms_user;
CREATE DATABASE IF NOT EXISTS ms_email;
```

---

## Configuração do RabbitMQ / CloudAMQP

No CloudAMQP, foi criada uma instância gratuita no plano Little Lemur.

A fila utilizada no projeto é:

```text
default.email
```

Nos arquivos `application.properties` dos serviços, foi configurada a URI AMQP:

```properties
spring.rabbitmq.addresses=amqps://usuario:senha@host.cloudamqp.com/vhost
broker.queue.email.name=default.email
```

---

## Configuração do User Service

Arquivo:

```text
user-service/src/main/resources/application.properties
```

Exemplo de configuração:

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/ms_user?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=secretkeysecretkeysecretkeysecretkey
jwt.expiration=3600000

spring.rabbitmq.addresses=amqps://usuario:senha@host.cloudamqp.com/vhost
broker.queue.email.name=default.email
```

---

## Configuração do Email Service

Arquivo:

```text
email-service/src/main/resources/application.properties
```

Exemplo de configuração:

```properties
server.port=8082

spring.datasource.url=jdbc:mysql://localhost:3306/ms_email?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.rabbitmq.addresses=amqps://usuario:senha@host.cloudamqp.com/vhost
broker.queue.email.name=default.email

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seuemail@gmail.com
spring.mail.password=SUA_SENHA_DE_APP
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Observação: a senha utilizada no campo `spring.mail.password` deve ser uma senha de aplicativo do Gmail, não a senha comum da conta.

---

## Como executar os serviços

Abra três terminais e rode cada aplicação separadamente.

### Terminal 1 — User Service

```bash
cd user-service
mvn spring-boot:run
```

O serviço será iniciado na porta:

```text
http://localhost:8081
```

---

### Terminal 2 — Email Service

```bash
cd email-service
mvn spring-boot:run
```

O serviço será iniciado na porta:

```text
http://localhost:8082
```

O Email Service ficará escutando a fila `default.email` no RabbitMQ.

---

### Terminal 3 — Frontend

```bash
cd frontend
npm install
node server.js
```

O frontend será iniciado na porta:

```text
http://localhost:3000
```

---

## Endpoints — User Service

| Método | Endpoint             | Acesso  | Descrição                                                                   |
| ------ | -------------------- | ------- | --------------------------------------------------------------------------- |
| POST   | `/auth/request-code` | Público | Gera código OTP de 6 dígitos, salva em cache e publica mensagem no RabbitMQ |
| POST   | `/auth/verify-code`  | Público | Valida o código OTP informado pelo usuário                                  |

---

## Endpoint — Solicitar código

```http
POST /auth/request-code
```

Exemplo de corpo da requisição:

```json
{
  "email": "usuario@email.com"
}
```

Resposta esperada:

```json
{
  "message": "Código enviado para o e-mail informado."
}
```

---

## Endpoint — Validar código

```http
POST /auth/verify-code
```

Exemplo de corpo da requisição:

```json
{
  "email": "usuario@email.com",
  "code": "123456"
}
```

Resposta esperada para código válido:

```json
{
  "valid": true,
  "message": "Código validado com sucesso."
}
```

Resposta esperada para código inválido ou expirado:

```json
{
  "valid": false,
  "message": "Código inválido ou expirado."
}
```

---

## Frontend Node.js

Estrutura:

```text
frontend/
├── package.json
├── server.js
└── public/
    ├── index.html
    └── verify.html
```

---

## Rotas do Frontend

| Método | Rota           | Descrição                                                                 |
| ------ | -------------- | ------------------------------------------------------------------------- |
| GET    | `/`            | Exibe a tela para digitar o e-mail                                        |
| POST   | `/send-code`   | Envia o e-mail para o User Service e redireciona para a tela de validação |
| GET    | `/verify`      | Exibe a tela para digitar o código recebido                               |
| POST   | `/verify-code` | Envia o e-mail e o código para validação no User Service                  |
| GET    | `/dashboard`   | Tela provisória para a próxima etapa                                      |

---

## Teste integrado

Para testar a integração completa:

1. Iniciar o User Service.
2. Iniciar o Email Service.
3. Iniciar o Frontend.
4. Acessar:

```text
http://localhost:3000
```

5. Informar um e-mail real.
6. Confirmar se o e-mail chegou na caixa de entrada.
7. Copiar o código recebido.
8. Informar o código na tela de validação.
9. Validar a resposta do sistema.

---
