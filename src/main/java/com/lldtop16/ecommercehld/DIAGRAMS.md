# 📊 E-Commerce Platform - Quick Reference Diagrams

> **Quick visual reference for the complete E-Commerce HLD**

---

## 🎯 Complete System Flow (Single Diagram)

```mermaid
flowchart TD
    Start([User Request]) --> Device{Device Type}
    
    Device -->|Mobile/Web| CDN[CDN Layer<br/>Static Assets]
    Device -->|API| GW_Check{API Request?}
    
    CDN --> Cache_Check{Cache Hit?}
    Cache_Check -->|Yes| Return_Static[Return Cached Asset]
    Cache_Check -->|No| Origin[Fetch from Origin]
    Origin --> Cache_Store[Store in CDN]
    Cache_Store --> Return_Static
    Return_Static --> End([Response])
    
    GW_Check -->|Yes| WAF[WAF & DDoS Protection]
    WAF --> GLB[Global Load Balancer<br/>Route53]
    
    GLB -->|Geo Route| Region{Nearest Region}
    Region -->|US-East| RLB1[Regional LB<br/>us-east-1]
    Region -->|EU-West| RLB2[Regional LB<br/>eu-west-1]
    Region -->|APAC| RLB3[Regional LB<br/>ap-south-1]
    
    RLB1 --> AGW1[API Gateway<br/>Kong/Istio]
    RLB2 --> AGW2[API Gateway<br/>Kong/Istio]
    RLB3 --> AGW3[API Gateway<br/>Kong/Istio]
    
    AGW1 --> Auth{Authenticate?}
    Auth -->|Invalid| Error401[401 Unauthorized]
    Auth -->|Valid| Rate{Rate Limit?}
    
    Rate -->|Exceeded| Error429[429 Too Many Requests]
    Rate -->|OK| Route{Route Request}
    
    Route -->|/users/*| US[User Service<br/>:8081]
    Route -->|/products/*| PS[Product Service<br/>:8082]
    Route -->|/orders/*| OS[Order Service<br/>:8083]
    Route -->|/payments/*| PYS[Payment Service<br/>:8084]
    
    US --> SD[Service Discovery<br/>K8s DNS]
    PS --> SD
    OS --> SD
    PYS --> SD
    
    SD --> LB{Load Balance}
    LB -->|Pod 1| US1[User Pod 1]
    LB -->|Pod 2| US2[User Pod 2]
    LB -->|Pod 3| US3[User Pod 3]
    
    US1 --> Cache_Layer{Multi-Level Cache}
    US2 --> Cache_Layer
    US3 --> Cache_Layer
    
    Cache_Layer -->|L1 Hit| Browser[Browser Cache]
    Cache_Layer -->|L2 Hit| CDN_App[CDN App Cache]
    Cache_Layer -->|L3 Hit| GW_Cache[Gateway Cache]
    Cache_Layer -->|L4 Hit| Redis[Redis Cluster]
    Cache_Layer -->|Miss| Database[(PostgreSQL)]
    
    Browser --> Response[Build Response]
    CDN_App --> Response
    GW_Cache --> Response
    Redis --> Response
    Database --> Response
    
    Response --> Metrics[Prometheus<br/>Metrics]
    Response --> Trace[Jaeger<br/>Tracing]
    Response --> Log[ELK<br/>Logging]
    
    Metrics --> Grafana[Grafana Dashboard]
    Trace --> Grafana
    Log --> Kibana[Kibana Dashboard]
    
    Grafana --> Alert{Alert?}
    Alert -->|Critical| Pager[PagerDuty]
    Alert -->|Warning| Email[Email Notification]
    
    Response --> End
    
    Error401 --> End
    Error429 --> End
    
    style Start fill:#4CAF50,color:#fff
    style End fill:#4CAF50,color:#fff
    style Error401 fill:#f44336,color:#fff
    style Error429 fill:#f44336,color:#fff
    style Redis fill:#ff6b6b
    style Database fill:#ffe4e1
    style CDN fill:#4d96ff
    style AGW1 fill:#FFE4B5
```

---

## 🔄 Detailed Request Flow with 500 Users

```mermaid
sequenceDiagram
    actor U1 as User 1
    actor U2 as User 2
    actor U3 as User 3
    actor U500 as User 500
    
    participant CDN
    participant GLB as Global LB
    participant AGW as API Gateway
    participant SM as Service Mesh
    participant US as User Service
    participant PS as Product Service
    participant Redis
    participant DB as PostgreSQL
    participant Kafka
    
    Note over U1,U500: 500 Concurrent Users
    
    par User 1 Request
        U1->>CDN: GET /api/products/123
        CDN->>GLB: Forward
        GLB->>AGW: Route
        AGW->>SM: Authenticate & Route
        SM->>PS: product-service:8082
        PS->>Redis: Check cache
        Redis-->>PS: Cache HIT (90%)
        PS-->>SM: Product JSON
        SM-->>AGW: Response
        AGW-->>GLB: Response
        GLB-->>CDN: Response
        CDN-->>U1: Product Data (50ms)
    and User 2 Request
        U2->>CDN: GET /cart
        CDN->>GLB: Forward
        GLB->>AGW: Route
        AGW->>SM: Authenticate & Route
        SM->>US: user-service:8081
        US->>Redis: cart:user:456
        Redis-->>US: Cart data
        US-->>SM: Cart JSON
        SM-->>AGW: Response
        AGW-->>GLB: Response
        GLB-->>CDN: Response
        CDN-->>U2: Cart Data (30ms)
    and User 3 Request
        U3->>CDN: POST /orders
        CDN->>GLB: Forward
        GLB->>AGW: Route
        AGW->>SM: Authenticate & Route
        SM->>US: Validate User
        US-->>SM: Valid
        SM->>PS: Check Inventory
        PS->>Redis: Lock inventory
        Redis-->>PS: Locked
        PS-->>SM: Available
        SM->>US: Create Order
        US->>DB: INSERT INTO orders
        DB-->>US: Order Created
        US->>Kafka: Emit ORDER_CREATED
        US-->>SM: Order Response
        SM-->>AGW: Response
        AGW-->>GLB: Response
        GLB-->>CDN: Response
        CDN-->>U3: Order Confirmed (200ms)
    and User 500 Request
        U500->>CDN: GET /products?category=electronics
        CDN->>GLB: Forward
        GLB->>AGW: Route
        AGW->>SM: Authenticate & Route
        SM->>PS: product-service:8082
        PS->>Redis: Check cache
        Redis-->>PS: Cache HIT
        PS-->>SM: Products List
        SM-->>AGW: Response
        AGW-->>GLB: Response
        GLB-->>CDN: Response
        CDN-->>U500: Products (55ms)
    end
    
    Note over Redis: 450 cache hits/sec<br/>(90% hit ratio)
    Note over DB: 50 DB queries/sec<br/>(10% cache miss)
    Note over Kafka: Events processed async
```

---

## 🗺️ Data Flow Diagram

```mermaid
graph TD
    subgraph "Client Tier"
        WEB[Web Browser]
        MOB[Mobile App]
    end
    
    subgraph "Edge Tier"
        CDN[CDN<br/>50ms from user]
        WAF[WAF<br/>DDoS Protection]
    end
    
    subgraph "Load Balancer Tier"
        GLB[Global LB<br/>Geo-routing]
        RLB[Regional LB<br/>Round-robin]
        AGW[API Gateway<br/>Auth + Rate Limit]
    end
    
    subgraph "Service Tier"
        US[User Service<br/>3 replicas]
        PS[Product Service<br/>5 replicas]
        OS[Order Service<br/>3 replicas]
        PYS[Payment Service<br/>2 replicas]
    end
    
    subgraph "Cache Tier"
        L1[Browser Cache<br/>TTL: 1 day]
        L2[CDN Cache<br/>TTL: 1 hour]
        L3[Gateway Cache<br/>TTL: 5 min]
        L4[Redis<br/>TTL: 10 min]
    end
    
    subgraph "Database Tier"
        PG[(PostgreSQL<br/>Primary)]
        REPLICA[(PostgreSQL<br/>2 Replicas)]
        MONGO[(MongoDB<br/>Catalog)]
        ES[(Elasticsearch<br/>Search)]
    end
    
    subgraph "Message Tier"
        KAFKA[(Kafka<br/>Events)]
        ZK[(Zookeeper<br/>3 nodes)]
    end
    
    subgraph "Storage Tier"
        S3[(S3<br/>Images)]
        BACKUP[(Backup<br/>S3 Glacier)]
    end
    
    WEB -->|1. Request| CDN
    MOB -->|1. Request| CDN
    
    CDN -->|2. Cache Miss| WAF
    WAF -->|3. Filter| GLB
    GLB -->|4. Geo Route| RLB
    RLB -->|5. Load Balance| AGW
    
    AGW -->|6. Auth| US
    AGW -->|6. Auth| PS
    AGW -->|6. Auth| OS
    AGW -->|6. Auth| PYS
    
    US -->|7a. Check| L4
    US -->|7b. Read| PG
    
    PS -->|7c. Check| L4
    PS -->|7d. Read| MONGO
    PS -->|7e. Search| ES
    
    OS -->|7f. Check| L4
    OS -->|7g. Write| PG
    
    PYS -->|7h. Check| L4
    PYS -->|7i. Write| PG
    
    L4 -->|8. Cache Miss| PG
    L4 -->|8. Cache Miss| MONGO
    
    PG -->|9. Replicate| REPLICA
    
    OS -->|10. Emit Event| KAFKA
    KAFKA -->|11. Consume| NS[Notification Service]
    
    PS -->|12. Store| S3
    
    PG -->|13. Backup| BACKUP
    
    style CDN fill:#4d96ff
    style AGW fill:#FFE4B5
    style L4 fill:#ff6b6b
    style PG fill:#ffe4e1
    style KAFKA fill:#90EE90
```

---

## 🔐 Security & Load Balancer Flow

```mermaid
flowchart LR
    Request[Incoming Request] --> DDoS{DDoS Attack?}
    
    DDoS -->|Yes| WAF_Block[WAF Blocks]
    DDoS -->|No| IP_Check{IP Whitelisted?}
    
    IP_Check -->|No| IP_Deny[403 Forbidden]
    IP_Check -->|Yes| IP_Rate{Rate Limit?}
    
    IP_Rate -->|Exceeded| Rate_Deny[429 Too Many Requests]
    IP_Rate -->|OK| SSL{SSL/TLS Valid?}
    
    SSL -->|No| SSL_Error[400 Bad Request]
    SSL -->|Yes| JWT{JWT Valid?}
    
    JWT -->|No| Auth_Deny[401 Unauthorized]
    JWT -->|Yes| CSRF{CSRF Token?}
    
    CSRF -->|No| CSRF_Deny[403 Forbidden]
    CSRF -->|Yes| Role{Has Permission?}
    
    Role -->|No| Role_Deny[403 Forbidden]
    Role -->|Yes| Input{Input Valid?}
    
    Input -->|No| Input_Deny[400 Bad Request]
    Input -->|Yes| Route[Route to Service]
    
    Route --> End([Process Request])
    
    WAF_Block --> End
    IP_Deny --> End
    Rate_Deny --> End
    SSL_Error --> End
    Auth_Deny --> End
    CSRF_Deny --> End
    Role_Deny --> End
    Input_Deny --> End
    
    style Request fill:#4CAF50,color:#fff
    style WAF_Block fill:#f44336,color:#fff
    style IP_Deny fill:#f44336,color:#fff
    style Rate_Deny fill:#f44336,color:#fff
    style SSL_Error fill:#f44336,color:#fff
    style Auth_Deny fill:#f44336,color:#fff
    style CSRF_Deny fill:#f44336,color:#fff
    style Role_Deny fill:#f44336,color:#fff
    style Input_Deny fill:#f44336,color:#fff
    style Route fill:#4CAF50,color:#fff
    style End fill:#4CAF50,color:#fff
```

---

## 🏗️ Kubernetes Pod Architecture

```mermaid
graph TB
    subgraph "Kubernetes Cluster"
        subgraph "Namespace: default"
            subgraph "Deployment: api-gateway"
                GW_POD1[Pod: api-gw-1<br/>192.168.1.10]
                GW_POD2[Pod: api-gw-2<br/>192.168.1.11]
                GW_POD3[Pod: api-gw-3<br/>192.168.1.12]
            end
            
            subgraph "Deployment: user-service"
                US_POD1[Pod: user-svc-1<br/>10.0.1.5:8081]
                US_POD2[Pod: user-svc-2<br/>10.0.1.6:8081]
            end
            
            subgraph "Deployment: product-service"
                PS_POD1[Pod: prod-svc-1<br/>10.0.2.8:8082]
                PS_POD2[Pod: prod-svc-2<br/>10.0.2.9:8082]
                PS_POD3[Pod: prod-svc-3<br/>10.0.2.10:8082]
            end
            
            subgraph "Deployment: order-service"
                OS_POD1[Pod: order-svc-1<br/>10.0.3.12:8083]
                OS_POD2[Pod: order-svc-2<br/>10.0.3.13:8083]
            end
        end
        
        subgraph "Namespace: data"
            REDIS_M[(Redis Master)]
            REDIS_S1[(Redis Slave 1)]
            REDIS_S2[(Redis Slave 2)]
            
            PG_MASTER[(PostgreSQL<br/>Primary)]
            PG_REPL1[(PostgreSQL<br/>Replica 1)]
            PG_REPL2[(PostgreSQL<br/>Replica 2)]
        end
    end
    
    subgraph "Istio Control Plane"
        ISTIOD[Istiod<br/>Control Plane]
    end
    
    subgraph "Monitoring"
        PROM[Prometheus]
        GRAF[Grafana]
        JAEGER[Jaeger]
    end
    
    GW_POD1 -.->|Sidecar| ISTIOD
    GW_POD2 -.->|Sidecar| ISTIOD
    GW_POD3 -.->|Sidecar| ISTIOD
    US_POD1 -.->|Sidecar| ISTIOD
    PS_POD1 -.->|Sidecar| ISTIOD
    OS_POD1 -.->|Sidecar| ISTIOD
    
    GW_POD1 -->|Load Balance| US_POD1 & US_POD2
    GW_POD1 -->|Load Balance| PS_POD1 & PS_POD2 & PS_POD3
    GW_POD1 -->|Load Balance| OS_POD1 & OS_POD2
    
    US_POD1 & US_POD2 --> REDIS_M
    PS_POD1 & PS_POD2 & PS_POD3 --> REDIS_M
    OS_POD1 & OS_POD2 --> REDIS_M
    
    US_POD1 & US_POD2 --> PG_MASTER
    OS_POD1 & OS_POD2 --> PG_MASTER
    PS_POD1 & PS_POD2 & PS_POD3 --> PG_MASTER
    
    PG_MASTER -->|Replicate| PG_REPL1 & PG_REPL2
    REDIS_M -->|Replicate| REDIS_S1 & REDIS_S2
    
    GW_POD1 & GW_POD2 & GW_POD3 --> PROM
    US_POD1 & US_POD2 --> PROM
    PS_POD1 & PS_POD2 & PS_POD3 --> PROM
    
    PROM --> GRAF
    PROM --> JAEGER
    
    style GW_POD1 fill:#FFE4B5
    style GW_POD2 fill:#FFE4B5
    style GW_POD3 fill:#FFE4B5
    style US_POD1 fill:#e1f5ff
    style US_POD2 fill:#e1f5ff
    style PS_POD1 fill:#90EE90
    style PS_POD2 fill:#90EE90
    style OS_POD1 fill:#FFB6C1
    style OS_POD2 fill:#FFB6C1
    style REDIS_M fill:#ff6b6b
    style PG_MASTER fill:#ffe4e1
```

---

## 📈 Performance Architecture (500 Users)

```mermaid
graph TB
    subgraph "Load Testing Results"
        RPS[Target: 5000 RPS]
        USERS[Concurrent: 500 Users]
        LATENCY[Target: < 200ms p95]
    end
    
    subgraph "API Gateway Layer"
        AGW_INST[5 Instances]
        AGW_CAP[Capacity: 1000 RPS each]
        AGW_TOTAL[Total: 5000 RPS]
    end
    
    subgraph "Service Layer"
        US_INST[User Service<br/>3 pods]
        PS_INST[Product Service<br/>5 pods with cache]
        OS_INST[Order Service<br/>3 pods]
        PYS_INST[Payment Service<br/>2 pods]
    end
    
    subgraph "Cache Layer"
        REDIS_CLUSTER[Redis Cluster<br/>500K ops/sec]
        CACHE_HIT[90% Hit Rate]
    end
    
    subgraph "Database Layer"
        PG_PRIM[PostgreSQL Primary<br/>5000 reads/sec]
        PG_REPLICA1[Replica 1]
        PG_REPLICA2[Replica 2]
        CONN_POOL[100 Connections]
    end
    
    subgraph "Message Queue"
        KAFKA_CLUSTER[Kafka Cluster<br/>3 brokers]
        THROUGHPUT[100K msg/sec]
    end
    
    RPS --> AGW_INST
    USERS --> AGW_INST
    
    AGW_INST --> AGW_CAP
    AGW_CAP --> AGW_TOTAL
    
    AGW_TOTAL --> US_INST
    AGW_TOTAL --> PS_INST
    AGW_TOTAL --> OS_INST
    AGW_TOTAL --> PYS_INST
    
    US_INST --> REDIS_CLUSTER
    PS_INST --> REDIS_CLUSTER
    OS_INST --> REDIS_CLUSTER
    
    REDIS_CLUSTER --> CACHE_HIT
    CACHE_HIT -->|10% Miss| PG_PRIM
    
    PG_PRIM --> PG_REPLICA1 & PG_REPLICA2
    PG_PRIM --> CONN_POOL
    
    OS_INST --> KAFKA_CLUSTER
    KAFKA_CLUSTER --> THROUGHPUT
    
    style RPS fill:#4CAF50,color:#fff
    style CACHE_HIT fill:#ff6b6b
    style AGW_TOTAL fill:#FFE4B5
    style REDIS_CLUSTER fill:#ff6b6b
    style PG_PRIM fill:#ffe4e1
```

---

## 🔄 Cart Service Deep Dive

```mermaid
flowchart TD
    Start[Add to Cart Request] --> Auth{Authenticated?}
    
    Auth -->|No| Guest[Guest Cart]
    Auth -->|Yes| User[User Cart]
    
    Guest -->|Session ID| GuestStore[Redis<br/>cart:session:{id}]
    User -->|User ID| UserStore[Redis<br/>cart:user:{id}]
    
    GuestStore --> MergeCheck{Login?}
    MergeCheck -->|Yes| Merge[Merge Guest + User Cart]
    Merge --> UserStore
    MergeCheck -->|No| Continue[Continue]
    UserStore --> Continue
    
    Continue --> Validate[Validate Product<br/>Check Price & Stock]
    Validate --> Calc[Calculate Total<br/>Subtotal + Tax + Discount]
    
    Calc --> UpdateCache[Update Redis Cache]
    UpdateCache --> AsyncDB[Async DB Write]
    
    AsyncDB --> Notify[Cart Updated Event]
    Notify --> End([Response])
    
    style GuestStore fill:#FFE4B5
    style UserStore fill:#90EE90
    style Redis fill:#ff6b6b
```

---

## 💳 Payment Service Flow

```mermaid
flowchart TD
    Start[Create Payment Intent] --> Validate{Validate Order}
    
    Validate -->|Invalid| Error[Return Error]
    Validate -->|Valid| Idempotency{Check Idempotency}
    
    Idempotency -->|Duplicate| ReturnCached[Return Cached Result]
    Idempotency -->|New| CreateIntent[Create Payment Intent]
    
    CreateIntent --> CacheIntent[Cache Intent<br/>TTL: 15min]
    CacheIntent --> CallGateway[Call Stripe/PayPal]
    
    CallGateway -->|Success| GetClientSecret[Get Client Secret]
    CallGateway -->|Failure| Retry{Retry < 3?}
    
    Retry -->|Yes| CallGateway
    Retry -->|No| Fail[Mark as Failed]
    
    GetClientSecret --> Return[Return to Client]
    Return --> End([Response])
    
    Fail --> End
    Error --> End
    ReturnCached --> End
    
    style CacheIntent fill:#ff6b6b
    style CallGateway fill:#FFE4B5
    style Fail fill:#f44336,color:#fff
```

---

## 📊 Database Replication Strategy

```mermaid
graph TB
    subgraph "Application Layer"
        APP1[App Pod 1]
        APP2[App Pod 2]
        APP3[App Pod 3]
    end
    
    subgraph "Connection Pool"
        POOL[HikariCP<br/>100 Connections]
    end
    
    subgraph "PostgreSQL Cluster"
        PRIMARY[(Primary<br/>us-east-1a<br/>Write)]
        REPLICA1[(Replica 1<br/>us-east-1b<br/>Read)]
        REPLICA2[(Replica 2<br/>us-east-1c<br/>Read)]
    end
    
    subgraph "Connection Routing"
        WRITE_ROUTER{Write or Read?}
    end
    
    APP1 --> POOL
    APP2 --> POOL
    APP3 --> POOL
    
    POOL --> WRITE_ROUTER
    
    WRITE_ROUTER -->|Write| PRIMARY
    WRITE_ROUTER -->|Read| REPLICA1
    WRITE_ROUTER -->|Read| REPLICA2
    
    PRIMARY -->|Stream Replication| REPLICA1
    PRIMARY -->|Stream Replication| REPLICA2
    
    style PRIMARY fill:#f44336,color:#fff
    style REPLICA1 fill:#90EE90
    style REPLICA2 fill:#90EE90
```

---

## 🌍 Geographic Distribution

```mermaid
graph TB
    subgraph "User Locations"
        USER_US[Users in USA<br/>500 users]
        USER_EU[Users in EU<br/>200 users]
        USER_APAC[Users in APAC<br/>300 users]
    end
    
    subgraph "AWS Regions"
        REGION_US[us-east-1<br/>Virginia]
        REGION_EU[eu-west-1<br/>Ireland]
        REGION_APAC[ap-south-1<br/>Mumbai]
    end
    
    subgraph "Global Load Balancer"
        ROUTE53[Route53<br/>Geo DNS]
    end
    
    USER_US --> ROUTE53
    USER_EU --> ROUTE53
    USER_APAC --> ROUTE53
    
    ROUTE53 -->|USA Users| REGION_US
    ROUTE53 -->|EU Users| REGION_EU
    ROUTE53 -->|APAC Users| REGION_APAC
    
    subgraph "Regional Components"
        subgraph "us-east-1"
            LB_US[Load Balancer]
            APPS_US[App Services]
            DB_US[(PostgreSQL<br/>Primary)]
        end
        
        subgraph "eu-west-1"
            LB_EU[Load Balancer]
            APPS_EU[App Services]
            DB_REPLICA[(PostgreSQL<br/>Replica)]
        end
        
        subgraph "ap-south-1"
            LB_APAC[Load Balancer]
            APPS_APAC[App Services]
            DB_REPLICA2[(PostgreSQL<br/>Replica)]
        end
    end
    
    REGION_US --> LB_US --> APPS_US --> DB_US
    REGION_EU --> LB_EU --> APPS_EU --> DB_REPLICA
    REGION_APAC --> LB_APAC --> APPS_APAC --> DB_REPLICA2
    
    DB_US -.->|Async Replication| DB_REPLICA
    DB_US -.->|Async Replication| DB_REPLICA2
    
    style REGION_US fill:#4CAF50,color:#fff
    style REGION_EU fill:#2196F3,color:#fff
    style REGION_APAC fill:#FF9800,color:#fff
```

---

## 🚀 Deployment Pipeline

```mermaid
flowchart LR
    Code[Developer Commits Code] --> CI[CI/CD Pipeline<br/>GitHub Actions/Jenkins]
    
    CI --> Test{All Tests Pass?}
    Test -->|No| Fail[Build Failed]
    Test -->|Yes| Build[Docker Build]
    
    Build --> Push[Push to Registry<br/>ECR/DockerHub]
    Push --> Deploy[Deploy to Staging]
    
    Deploy --> StagingTest[Smoke Tests]
    StagingTest --> Prod{Approved?}
    
    Prod -->|No| Rollback[Rollback]
    Prod -->|Yes| Canary[Canary Deployment<br/>10% Traffic]
    
    Canary --> Monitor{Monitor Metrics}
    Monitor -->|Errors| Rollback
    Monitor -->|OK| Progressive{Rollout}
    
    Progressive -->|50%| Monitor2[Monitor]
    Monitor2 -->|OK| Full[100% Traffic]
    
    Full --> End([Production])
    Rollback --> End
    Fail --> End
    
    style Code fill:#4CAF50,color:#fff
    style Fail fill:#f44336,color:#fff
    style Rollback fill:#FF9800
    style End fill:#4CAF50,color:#fff
```

---

## 📝 Quick Reference

### Key Components Summary

| Component | Technology | Purpose | Scale |
|-----------|-----------|---------|-------|
| **CDN** | CloudFront | Static assets | 1000+ req/sec |
| **Global LB** | Route53 | Geo-routing | Multi-region |
| **API Gateway** | Kong/Istio | Routing, Auth | 5000 RPS |
| **Service Mesh** | Istio | Service-to-service | 1000 services |
| **User Service** | Spring Boot | User management | 3 pods |
| **Product Service** | Spring Boot | Catalog | 5 pods |
| **Order Service** | Spring Boot | Orders | 3 pods |
| **Payment Service** | Spring Boot | Payments | 2 pods |
| **Cache** | Redis Cluster | Sessions, cart | 500K ops/sec |
| **Database** | PostgreSQL | Transactions | 5000 reads/sec |
| **Catalog DB** | MongoDB | Products | Flexible schema |
| **Search** | Elasticsearch | Product search | Full-text |
| **Message Queue** | Kafka | Events | 100K msg/sec |
| **Object Storage** | S3 | Images | Unlimited |
| **Monitoring** | Prometheus | Metrics | Time-series |
| **Logging** | ELK Stack | Logs | Centralized |

### Port Reference

```
Service          Port   Protocol   Purpose
-----------      ----   --------   -------
API Gateway      443    HTTPS      External API
User Service     8081   HTTP       Internal
Product Service  8082   HTTP       Internal
Order Service    8083   HTTP       Internal
Payment Service  8084   HTTP       Internal
Inventory Service 8085  HTTP       Internal
Notification Svc 8086   HTTP       Internal
Redis            6379   TCP        Cache
PostgreSQL       5432   TCP        Database
MongoDB          27017  TCP        Catalog
Elasticsearch    9200   HTTP       Search
Kafka            9092   TCP        Messages
Zookeeper        2181   TCP        Kafka coordination
```

### Cache TTL Reference

```
Cache Layer           Data Type              TTL
-------------------   --------------------   ------
Browser Cache         Static assets          1 day
CDN Cache             Product images         1 hour
API Gateway Cache     GET /products/*       5 minutes
Redis - Cart          User cart              30 days
Redis - Session       User session           1 hour
Redis - Catalog       Products, categories   1 hour
Redis - Rate Limit    API counters           1 minute
Database Query Cache  DB query results       1 minute
```

---

## 🎓 Interview Discussion Points

### 1. Load Balancer Strategy

**Q: Why multiple load balancer layers?**
```
A: Defense in depth + specialization:
- Global LB: Geo-routing + failover
- Regional LB: DDoS protection + distribution
- API Gateway: Business logic (auth, rate-limit)
- Service Mesh: Microservice load balancing

Each layer has specific responsibility.
```

**Q: How does Istio load balancer work?**
```
A: Istio sidecar (Envoy) intercepts all traffic:
1. Service A calls service-b.namespace.svc.cluster.local
2. DNS resolves to ClusterIP
3. kube-proxy load balances to pod IPs
4. Envoy sidecar intercepts, applies:
   - Load balancing algorithm (round-robin/least-conn)
   - Circuit breaker (if failures > threshold)
   - Retry policy (if failure)
   - Timeout configuration
```

### 2. Service Discovery

**Q: How does service discovery work in Kubernetes?**
```
A: 3 main components:
1. etcd: Stores all cluster state (services, pods)
2. CoreDNS: Translates service names to ClusterIPs
3. kube-proxy: iptables/IPVS rules for load balancing

Flow:
  Service starts → Kubelet registers in etcd
  etcd → CoreDNS updates DNS records
  Client queries DNS → Gets ClusterIP
  kube-proxy → Load balances to pod IPs
```

### 3. Caching Strategy

**Q: How to handle 500 concurrent users with caching?**
```
A: Multi-level caching:
1. Browser cache: Static assets (images, CSS)
2. CDN: Product images, static content
3. API Gateway: Product listings (5 min TTL)
4. Redis: Active data (cart, sessions, products)

For 500 users (5000 RPS):
- 90% cache hit ratio = 4500 requests served from cache
- 500 requests hit DB
- PostgreSQL handles 5000+ reads/sec easily

Result: < 200ms latency for 99% of requests
```

**Q: What to cache and what not to cache?**
```
Cache:
✓ Product catalog (read-heavy, rarely changes)
✓ Product images (static assets via CDN)
✓ User cart (fast access, Redis)
✓ User sessions (JWT validation)
✓ Rate limit counters (in-memory/Redis)

Don't Cache:
✗ Payment status (must be accurate)
✗ Inventory levels (real-time required)
✗ Order creation (write operation)
✗ User authentication decisions
```

### 4. Data Storage

**Q: Why multiple databases?**
```
A: Polyglot persistence:
- PostgreSQL: ACID transactions (orders, payments)
- MongoDB: Flexible schema (products with variants)
- Redis: High-speed cache (cart, sessions)
- Elasticsearch: Full-text search (product search)
- S3: Object storage (images, files)

Each DB optimized for specific use case.
```

**Q: How to handle database scaling?**
```
A: Read/Write separation:
- Primary DB: All writes (orders, payments)
- Read Replicas: All reads (product catalog, user queries)
- Connection pooling: 100 connections max
- Connection pool per service: Isolated resources

With 500 users:
- Primary: 100 writes/sec
- Replicas: 5000 reads/sec (split across 2 replicas)
- Each replica: 2500 reads/sec
```

---

## 🎯 Key Metrics for 500 Users

### Request Volume

```
500 users × 10 actions/min = 5000 requests/min
= 83 requests/second

PEAK HOURS (5x traffic):
= 415 requests/second

With 20% buffer:
= 500 requests/second capacity needed

COMPONENT CAPACITY:
- API Gateway: 5000 RPS (5 instances × 1000 RPS)
- Product Service: 600 RPS (5 pods × 120 RPS each)
- Order Service: 300 RPS (3 pods × 100 RPS each)
- Database: 5000 reads/sec
- Redis: 500,000 ops/sec

All components exceed requirements. ✓
```

### Cost Estimate (AWS)

```
Component              Monthly Cost
-------------------    --------------
EC2 (10 pods)          $300
RDS PostgreSQL         $200
Elasticache Redis      $150
MongoDB Atlas          $100
S3 Storage             $50
Kafka (MSK)            $200
Load Balancers         $50
Data Transfer          $100
CloudWatch             $50
-------------------    --------------
TOTAL:                 $1,200/month

With reserved instances: $800/month
```

---

## 🎓 Interview Tips

### What to Emphasize

1. **Load Balancing**: Multi-layer with different algorithms
2. **Service Discovery**: Automatic with Kubernetes DNS
3. **Caching**: Multiple layers with appropriate TTLs
4. **Database**: Right database for right job (polyglot)
5. **Observability**: Metrics, logs, traces (3 pillars)
6. **Scalability**: Horizontal scaling, auto-scaling
7. **Resilience**: Circuit breakers, retries, fallbacks
8. **Security**: Defense in depth (WAF → Gateway → Service)

### Common Follow-up Questions

1. **"How to handle 5000 users instead of 500?"**
   - Scale horizontally (more pods)
   - Add more database read replicas
   - Increase Redis cluster size
   - Use CDN more aggressively

2. **"How to reduce latency from 200ms to 100ms?"**
   - More caching (longer TTLs)
   - Database query optimization
   - Connection pooling
   - Reduce service-to-service calls

3. **"How to ensure zero downtime deployment?"**
   - Blue-green deployment
   - Canary releases with Istio
   - Rolling updates
   - Health checks

4. **"How to handle database failures?"**
   - Read replicas for failover
   - Connection pool retry logic
   - Circuit breaker pattern
   - Graceful degradation

---

## 📚 Further Reading

- [Istio Documentation](https://istio.io)
- [Kubernetes Service Discovery](https://kubernetes.io/docs/concepts/services-networking/service)
- [Redis Caching Patterns](https://redis.io/topics/lru-cache)
- [Database Replication](https://www.postgresql.org/docs/current/high-availability.html)
- [Microservices Patterns](https://microservices.io/patterns/)

---

**Last Updated**: June 2026 | **Version**: 1.0