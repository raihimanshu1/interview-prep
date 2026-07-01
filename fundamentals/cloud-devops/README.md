# Docker, Kubernetes, CI/CD — Complete Deep Dive

## 1. Why This Concept Matters

Modern applications run in containers orchestrated by Kubernetes. Every backend engineer must understand Dockerfiles, container lifecycle, Kubernetes pods/deployments/services, horizontal pod autoscaling, config maps, secrets, rolling updates, and CI/CD pipelines. Interviewers test this for senior roles because deployment and operations are your responsibility — not just writing code.

## 2. Docker

**What is Docker:** Packages an application and its dependencies into a container image. Runs consistently anywhere (dev, CI, production).

```dockerfile
# Multi-stage Dockerfile for Spring Boot
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline  # Download dependencies (cached)
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (minimal image)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy only the built JAR (not the build tools)
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Docker best practices:**
- Multi-stage builds (small final image ~150MB vs 800MB)
- `.dockerignore` to exclude `node_modules`, `.git`, `target/`
- Use specific tags (`eclipse-temurin:21-jre-alpine`), not `latest`
- HEALTHCHECK for container health
- Don't run as root — use `USER 1000`
- Minimize layers (combine RUN commands)

**Docker commands:**
```bash
docker build -t myapp:1.0 .
docker run -p 8080:8080 --name myapp myapp:1.0
docker ps
docker logs -f myapp
docker exec -it myapp sh
docker compose up -d  # Multi-container (app + DB + Redis)
```

## 3. Kubernetes

**Architecture:**
- **Cluster**: set of nodes (machines)
- **Node**: worker machine (physical or VM), runs pods
- **Pod**: smallest deployable unit — 1+ containers, shared IP/volume
- **Deployment**: manages pod replicas, rolling updates, rollbacks
- **Service**: stable network endpoint to access pods (ClusterIP, NodePort, LoadBalancer)
- **ConfigMap**: non-sensitive configuration (env vars, files)
- **Secret**: sensitive data (passwords, API keys) — base64 encoded, encrypted at rest
- **Ingress**: HTTP/HTTPS routing to services (like API gateway)

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # Can create 1 extra pod during update
      maxUnavailable: 0   # Must keep all pods available
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: order-service
        image: myregistry/order-service:1.2.3
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        resources:
          requests:           # Minimum guaranteed
            cpu: "500m"       # 0.5 CPU cores
            memory: "512Mi"
          limits:             # Maximum allowed
            cpu: "1000m"      # 1 CPU core
            memory: "1Gi"
        livenessProbe:        # Is the app alive? (restart if fails)
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:       # Is the app ready to serve traffic?
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP  # Internal only (use LoadBalancer for external)
---
# hpa.yaml (Horizontal Pod Autoscaler)
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 2
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70  # Scale up when CPU > 70%
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

**Deployment strategies:**
```yaml
# Rolling Update (default) — gradual, zero-downtime
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 25%        # Can exceed desired count by 25%
    maxUnavailable: 25%  # Can have 25% pods unavailable

# Blue-Green — new version (green) gets full traffic after verification
# 1. Deploy green deployment alongside blue
# 2. Run tests against green
# 3. Switch service selector to green
# 4. Delete blue

# Canary — incremental traffic shift to new version
# 1. Deploy 1 canary pod with new version
# 2. Route 5% traffic to canary
# 3. Monitor errors/latency
# 4. Gradually increase to 100%
```

## 4. CI/CD

```yaml
# .github/workflows/deploy.yml (GitHub Actions)
name: Build, Test, Deploy
on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with: { java-version: '21', distribution: 'temurin' }
    - name: Run tests
      run: mvn verify
  
  build-and-push:
    needs: test
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Build Docker image
      run: docker build -t myapp .
    - name: Push to registry
      run: |
        docker tag myapp registry.example.com/myapp:${{ github.sha }}
        docker push registry.example.com/myapp:${{ github.sha }}
  
  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/order-service \
          order-service=registry.example.com/myapp:${{ github.sha }}
        kubectl rollout status deployment/order-service
```

## 5. Key kubectl commands

```bash
kubectl get pods -w                     # Watch pod status
kubectl logs -f deployment/order-service # Tail logs
kubectl describe pod order-service-xxx   # Detailed info
kubectl exec -it pod-name -- sh         # Shell into container
kubectl get events --sort-by='.lastTimestamp' # Cluster events
kubectl rollout status deployment/order-service  # Deploy progress
kubectl rollout undo deployment/order-service    # Rollback
kubectl top pods                         # CPU/Memory usage
```

## 6. Final 30-Second Answer

**Docker**: multi-stage builds, `.dockerignore`, HEALTHCHECK, non-root user. **K8s**: Pod (smallest unit), Deployment (replicas + rolling updates), Service (stable networking), HPA (auto-scale by CPU/memory), ConfigMap/Secret (configuration). **Deploy strategies**: RollingUpdate (gradual, default), Blue-Green (switch at once), Canary (5%→100% traffic). **CI/CD**: build → test → push image → deploy (kubectl set image). **Probes**: liveness (restart if dead), readiness (stop sending traffic). Never: use `latest` tag, run as root, skip probes, forget resource limits.