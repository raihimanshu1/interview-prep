# ⚙️ Problem 65: Configuration Management System

> **Difficulty**: ⭐⭐ | **Company Fit**: Any company with microservices  
> **Est. Time**: 60 min | **Patterns**: Observer, Strategy, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a centralized configuration management system."

**What the interviewer tests**:
```
1. Can you store config centrally? (Single source of truth)
2. Can you push config changes? (Hot reload)
3. Can you version configs? (Rollback)
4. Can you handle environments? (Dev, staging, prod)
```

### Step 2: The "Aha!" Moment

The key insight: **Config management = key-value store with watch.**

```
WITHOUT CENTRALIZED:
  Service A: config in application.yml
  Service B: config in application.yml
  Service C: config in application.yml
  
  Change requires: Deploy all services
  
WITH CENTRALIZED:
  Config Server: {db.url: "...", cache.ttl: 300}
   ↓
  Service A: Pull on startup + watch for changes
  Service B: Pull on startup + watch for changes
  Service C: Pull on startup + watch for changes
  
  Change requires: Update config server only
```

### Step 3: How to handle config changes?

```
PUSH vs PULL:
  Push: Server pushes changes to clients (WebSocket)
  Pull: Clients poll for changes (every 30s)
  Watch: Long-polling connection
  
HOT RELOAD:
  1. Config changed
  2. Notify services
  3. Services pull new config
  4. Apply changes (graceful restart)
```

---

## 💻 Core Implementation

```java
package com.config;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ConfigServer stores and distributes configuration.
 */
public class ConfigServer {
    
    private final Map<String, Map<String, String>> configs;
    private final Map<String, List<ConfigWatcher>> watchers;
    private final ScheduledExecutorService scheduler;

    public ConfigServer() {
        this.configs = new ConcurrentHashMap<>();
        this.watchers = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    /**
     * Set configuration for environment.
     */
    public synchronized void setConfig(String environment, String key, String value) {
        configs.computeIfAbsent(environment, k -> new ConcurrentHashMap<>())
               .put(key, value);
        
        // Notify watchers
        notifyWatchers(environment, key, value);
    }

    /**
     * Get configuration.
     */
    public String getConfig(String environment, String key) {
        Map<String, String> envConfig = configs.get(environment);
        if (envConfig == null) {
            throw new IllegalArgumentException("Environment not found: " + environment);
        }
        return envConfig.get(key);
    }

    /**
     * Watch for config changes.
     */
    public void watch(String environment, String key, ConfigWatcher watcher) {
        watchers.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>())
                .add(watcher);
    }

    /**
     * Notify watchers of config change.
     */
    private void notifyWatchers(String environment, String key, String newValue) {
        List<ConfigWatcher> keyWatchers = watchers.get(key);
        if (keyWatchers != null) {
            for (ConfigWatcher watcher : keyWatchers) {
                watcher.onConfigChanged(environment, key, newValue);
            }
        }
    }

    /**
     * Rollback to previous version.
     */
    public synchronized void rollback(String environment, String key) {
        Map<String, String> envConfig = configs.get(environment);
        if (envConfig != null && envConfig.containsKey(key)) {
            String current = envConfig.get(key);
            // Simplified: remove current, use default
            envConfig.remove(key);
            notifyWatchers(environment, key, null);
        }
    }
}

interface ConfigWatcher {
    void onConfigChanged(String environment, String key, String newValue);
}

class ServiceConfigWatcher implements ConfigWatcher {
    private final String serviceName;

    ServiceConfigWatcher(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void onConfigChanged(String environment, String key, String newValue) {
        System.out.println(serviceName + " config changed: " + key + " = " + newValue);
        // Trigger hot reload
    }
}

/**
 * Configuration manager (client-side).
 */
class ConfigManager {
    private final ConfigServer configServer;
    private final String serviceName;
    private final String environment;
    private final Map<String, String> localConfig;

    ConfigManager(ConfigServer configServer, String serviceName, String environment) {
        this.configServer = configServer;
        this.serviceName = serviceName;
        this.environment = environment;
        this.localConfig = new ConcurrentHashMap<>();
        
        // Pull initial config
        pullConfig();
        
        // Watch for changes
        configServer.watch(environment, "*", this::onConfigChanged);
    }

    String get(String key) {
        return localConfig.get(key);
    }

    private void pullConfig() {
        // Simplified: pull all keys
        System.out.println(serviceName + " pulling config for " + environment);
    }

    private void onConfigChanged(String env, String key, String value) {
        if (environment.equals(env)) {
            if (value != null) {
                localConfig.put(key, value);
            } else {
                localConfig.remove(key);
            }
            
            System.out.println(serviceName + " hot reloaded config: " + key);
        }
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle config in Kubernetes?"
> "ConfigMaps, Secrets. Mount as files. Watch for changes via API."

### Q2: "How to handle sensitive configs?"
> "Encryption at rest. Vault integration. Kubernetes Secrets."

### Q3: "How to handle rollback?"
> "Version history. Git-backed config. One-click rollback."

### Q4: "How to validate configs?"
> "Schema validation. Type checking. Staged rollout."