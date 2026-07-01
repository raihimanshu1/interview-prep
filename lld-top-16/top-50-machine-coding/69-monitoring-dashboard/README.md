# 📈 Problem 69: Monitoring Dashboard (Like Grafana)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any production company  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a real-time monitoring dashboard."

**What the interviewer tests**:
```
1. Can you collect metrics? (CPU, memory, latency)
2. Can you visualize them? (Charts, graphs)
3. Can you alert? (Threshold, anomaly)
4. Can you handle scale? (1000s of metrics/sec)
```

### Step 2: The "Aha!" Moment

The key insight: **Monitoring = time-series data + aggregation + visualization.**

```
METRICS:
  - System: CPU%, memory%, disk%
  - Application: QPS, latency, error rate
  - Business: signups, purchases, revenue
  
COLLECTION:
  Agent → Push or Pull → Time-series DB
   ↓
Aggregation → Downsampling
   ↓
Dashboard → Visualization
   ↓
Alert → Notification
```

### Step 3: How to handle high cardinality?

```
CARDINALITY:
  Low: cpu.usage (1 metric)
  High: http.requests.by.user.{user_id} (1000s of users)
  
SOLUTIONS:
  - Pre-aggregate: Count per endpoint, not per user
  - Sampling: Collect 10% of high-cardinality metrics
  - Tagging: Use labels, not unique series
```

---

## 💻 Core Implementation

```java
package com.monitoring;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: MonitoringDashboard collects and displays metrics.
 */
public class MonitoringDashboard {
    
    private final Map<String, TimeSeriesMetric> metrics;
    private final List<AlertRule> alertRules;
    private final ScheduledExecutorService collector;

    public MonitoringDashboard() {
        this.metrics = new ConcurrentHashMap<>();
        this.alertRules = new CopyOnWriteArrayList<>();
        this.collector = Executors.newScheduledThreadPool(10);
    }

    /**
     * Register metric.
     */
    public void registerMetric(String name, TimeSeriesMetric metric) {
        metrics.put(name, metric);
    }

    /**
     * Record data point.
     */
    public void record(String metricName, double value) {
        TimeSeriesMetric metric = metrics.get(metricName);
        if (metric != null) {
            metric.addPoint(value);
        }
    }

    /**
     * Get current metric value.
     */
    public double getMetric(String metricName) {
        TimeSeriesMetric metric = metrics.get(metricName);
        if (metric == null) return 0;
        return metric.getCurrentValue();
    }

    /**
     * Add alert rule.
     */
    public void addAlertRule(AlertRule rule) {
        alertRules.add(rule);
    }

    /**
     * Check alerts.
     */
    public void checkAlerts() {
        for (AlertRule rule : alertRules) {
            TimeSeriesMetric metric = metrics.get(rule.getMetricName());
            if (metric == null) continue;
            
            double value = metric.getCurrentValue();
            
            if (rule.evaluate(value)) {
                triggerAlert(rule, value);
            }
        }
    }

    private void triggerAlert(AlertRule rule, double value) {
        System.out.println("🚨 Alert: " + rule.getMetricName() + 
                          " = " + value + " (" + rule.getOperator() + 
                          " " + rule.getThreshold() + ")");
    }

    /**
     * Start collection (simulated).
     */
    public void startCollection() {
        collector.scheduleAtFixedRate(() -> {
            // Simulate metric collection
            record("cpu.usage", Math.random() * 100);
            record("memory.usage", Math.random() * 100);
            record("qps", Math.random() * 1000);
            
            // Check alerts
            checkAlerts();
        }, 0, 10, TimeUnit.SECONDS);
    }
}

/**
 * Time-series metric.
 */
class TimeSeriesMetric {
    private final String name;
    private final Deque<DataPoint> dataPoints;
    private final int maxPoints;

    TimeSeriesMetric(String name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;
        this.dataPoints = new LinkedList<>();
    }

    void addPoint(double value) {
        dataPoints.add(new DataPoint(value, System.currentTimeMillis()));
        
        // Keep only last N points
        while (dataPoints.size() > maxPoints) {
            dataPoints.poll();
        }
    }

    double getCurrentValue() {
        return dataPoints.isEmpty() ? 0 : dataPoints.peekLast().getValue();
    }

    public String getName() { return name; }
}

class DataPoint {
    private final double value;
    private final long timestamp;

    DataPoint(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() { return value; }
    public long getTimestamp() { return timestamp; }
}

class AlertRule {
    private final String metricName;
    private final String operator;
    private final double threshold;

    AlertRule(String metricName, String operator, double threshold) {
        this.metricName = metricName;
        this.operator = operator;
        this.threshold = threshold;
    }

    public String getMetricName() { return metricName; }
    public String getOperator() { return operator; }
    public double getThreshold() { return threshold; }

    boolean evaluate(double value) {
        switch (operator) {
            case ">": return value > threshold;
            case "<": return value < threshold;
            case ">=": return value >= threshold;
            case "<=": return value <= threshold;
            case "==": return value == threshold;
            default: return false;
        }
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle millions of metrics?"
> "Downsampling. Aggregation. Tiered storage (hot/warm/cold)."

### Q2: "How to detect anomalies?"
> "Standard deviation. Moving average. ML-based (Twitter's Seasonal Hybrid ESD)."

### Q3: "How to handle alert storms?"
> "Group similar alerts. Deduplication. Throttling (max 1 per minute)."

### Q4: "How to visualize in real-time?"
> "WebSocket push. Canvas rendering. Incremental updates."