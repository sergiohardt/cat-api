# Case Cat API - Arquitetura CQRS
- **Hexagonal + DDD + CQRS** (controllers separados de **commands** e **query**).
- **PostgreSQL** para persistência, **Redis** para cache de consultas.

## Justificativa Técnica

### Escolha do PostgreSQL
- **Relacionamentos**: Dados estruturados com relacionamentos entre raças e imagens
- **Performance**: Índices otimizados para consultas por temperamento e origem
- **Escalabilidade**: Suporte a particionamento e replicação
- **Compatibilidade**: similar ao RDS da AWS

### Arquitetura CQRS
- **Separação de responsabilidades**: Commands e Queries independentes
- **Performance**: Otimizações específicas para leitura e escrita
- **Escalabilidade**: Possibilidade de escalar leitura e escrita independentemente
- **Threading**: Processamento paralelo em Commands e Queries assíncronas
## Funcionalidades

### Coleta de Dados (Commands)
- Coleta de raças de gatos da API externa (https://thecatapi.com/)
- Coleta de imagens por raça (3 imagens por raça)
- Coleta de imagens especiais (gatos com chapéu e óculos)
- Processamento paralelo para otimização de performance

### Consulta de Dados (Queries)
- Listar todas as raças
- Buscar raça por ID
- Buscar raças por temperamento
- Buscar raças por origem
- Suporte a processamento assíncrono com threading


## Estrutura do Projeto

```
src/main/java/com/sencon/catapi/
├── domain/               # Entidades de domínio
├── infrastructure/       # Camada de infraestrutura
│   ├── persistence/      # JPA entities, repositories
│   └── external/         # Clientes API externa
├── application/          # Camada de aplicação (CQRS)
│   ├── command/          # Commands para escrita
│   └── query/            # Queries para leitura
├── presentation/         # Controllers REST
└── config/               # Configurações
```

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring WebFlux** (para chamadas assíncronas)
- **PostgreSQL** (banco de dados)
- **Redis** (cache)
- **Liquibase** (migrações de banco)
- **Docker & Docker Compose**
- **OpenAPI/Swagger** (documentação)

## Executando o Projeto

### Pré-requisitos
- Docker
- Java 21+ (opcional, para desenvolvimento local)
- Maven Wrapper incluído no projeto
- user a **URL http://ec2-100-24-9-6.compute-1.amazonaws.com:8090/ para testes em nuvem**

### Usando Docker (Recomendado)

1. **Subir toda a infraestrutura:**
```bash
docker-compose up -d
```

2. **Verificar logs:**
```bash
docker-compose logs -f cat-api
```

### Problemas com Imagens Docker

Se você encontrar problemas com compatibilidade de arquitetura, tente usar o Dockerfile alternativo:

```bash
# Build com Ubuntu (mais compatível)
docker build -f Dockerfile.ubuntu -t cat-api .

# Ou force a plataforma
docker build --platform linux/amd64 -t cat-api .
```

### Desenvolvimento Local

1. **Subir apenas as dependências:**
```bash
docker-compose up postgres redis adminer -d
```

2. **Executar a aplicação:**
```bash
./mvnw spring-boot:run
```

## Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8090/api-docs

ou:

- **Swagger UI**: http://ec2-100-24-9-6.compute-1.amazonaws.com:8090/swagger-ui.html

## Endpoints Principais

### Commands (Coleta de Dados)

#### Coletar Raças
```http
POST /api/commands/collect-breeds?forceUpdate=false
```

#### Coletar Imagens
```http
POST /api/commands/collect-images
Content-Type: application/json

{
  "collectBreedImages": true,
  "collectHatImages": true,
  "collectSunglassesImages": true,
  "imagesPerBreed": 3,
  "specialImagesCount": 3
}
```

#### Coleta Completa
```http
POST /api/commands/collect-all
Content-Type: application/json

{
  "forceUpdate": false,
  "collectBreedImages": true,
  "collectHatImages": true,
  "collectSunglassesImages": true,
  "imagesPerBreed": 3,
  "specialImagesCount": 3
}
```

### Queries (Consulta de Dados)

#### Listar Todas as Raças
```http
GET /api/breeds?includeImages=false&sortBy=name&sortDirection=ASC
```

#### Buscar Raça por ID
```http
GET /api/breeds/{id}?includeImages=false
```

#### Buscar por Temperamento
```http
GET /api/breeds/by-temperament?temperament=calm&includeImages=false
```

#### Buscar por Origem
```http
GET /api/breeds/by-origin?origin=Egypt&includeImages=false
```

## Threading e Performance

O projeto implementa processamento paralelo em:

1. **Coleta de imagens**: `CompletableFuture` para coletar imagens de múltiplas raças simultaneamente
2. **Queries assíncronas**: `@Async` para processamento paralelo das consultas
3. **Stream paralelas**: Para processamento de listas grandes
4. **Cache**: Caffeine para otimizar consultas frequentes

## 🗄️ Banco de Dados

### PostgreSQL
- **Host**: localhost:5432
- **Database**: cat_api_db
- **User**: catapi
- **Password**: catapi123

### Adminer (Interface Web)
- **URL**: http://localhost:8080
- **System**: PostgreSQL
- **Server**: postgres
- **Username**: catapi
- **Password**: catapi123
- **Database**: cat_api_db

## 📊 Monitoramento

### Health Check
```http
GET /api/health
```

### Actuator Endpoints
- **Health**: http://localhost:8090/actuator/health
- **Metrics**: http://localhost:8090/actuator/metrics
- **Info**: http://localhost:8090/actuator/info

## Exemplo de Uso Completo

1. **Iniciar aplicação:**
```bash
docker-compose up -d
```

2. **Coletar dados:**
```bash
curl -X POST http://localhost:8090/api/commands/collect-all \
  -H "Content-Type: application/json" \
  -d '{
    "forceUpdate": false,
    "collectBreedImages": true,
    "collectHatImages": true,
    "collectSunglassesImages": true,
    "imagesPerBreed": 3,
    "specialImagesCount": 3
  }'
```

3. **Consultar raças:**
```bash
curl http://localhost:8090/api/breeds
```

4. **Buscar por temperamento:**
```bash
curl "http://localhost:8090/api/breeds/by-temperament?temperament=playful"
```

## Build e Configurações

### Compilar o Projeto
```bash
# Com Maven Wrapper (recomendado)
./mvnw clean compile

# Build completo com JAR
./mvnw clean package

# Pular testes durante o build
./mvnw clean package -DskipTests
```

### Configurações Principais
- **`pom.xml`**: Dependências e plugins Maven
- **`application.properties`**: Configurações da aplicação
  - **Threading**: Pool de threads configurável
  - **Cache**: TTL e tamanho configuráveis
  - **API Externa**: Timeout e retry configuráveis
  - **Database**: Connection pool configurável
