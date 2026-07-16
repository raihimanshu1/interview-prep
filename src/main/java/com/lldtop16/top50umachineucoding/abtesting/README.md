# 🧪 Problem 67: A/B Testing System (Like Optimizely)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: SaaS, product companies  
> **Est. Time**: 90 min | **Patterns**: Strategy, Observer, Factory

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design an A/B testing system for experiments."

**What the interviewer tests**:
```
1. Can you split traffic? (50/50, 90/10)
2. Can you track metrics? (Conversion, revenue)
3. Can you determine winners? (Statistical significance)
4. Can you handle multiple experiments? (No interference)
```

### Step 2: The "Aha!" Moment

The key insight: **A/B testing = traffic splitting + metric tracking + stats.**

```
FLOW:
  1. Create experiment (variant A vs B)
  2. Assign users to variants (consistent hashing)
  3. Track metrics per variant
  4. Analyze results (p-value, confidence)
  5. Declare winner or iterate
  
USER ASSIGNMENT:
  user123 → variant A (consistent)
  user456 → variant B (consistent)
  
  Same user always sees same variant
```

### Step 3: How to ensure statistical validity?

```
SAMPLE SIZE:
  - Baseline conversion: 10%
  - Minimum detectable effect: +2%
  - Significance: 95%
  - Power: 80%
  → Need ~10,000 users per variant
  
AVOID INTERFERENCE:
  - Mutual exclusion: User in only 1 experiment
  - Layering: Stack experiments carefully
```

---

## 💻 Core Implementation

```java
package com.abtesting;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ABTestService manages experiments.
 */
public class ABTestService {
    
    private final Map<String, Experiment> experiments;
    private final Map<String, ExperimentAssignment> assignments;

    public ABTestService() {
        this.experiments = new ConcurrentHashMap<>();
        this.assignments = new ConcurrentHashMap<>();
    }

    /**
     * Create experiment.
     */
    public void createExperiment(Experiment experiment) {
        experiments.put(experiment.getId(), experiment);
    }

    /**
     * INTUITION: Assign user to variant.
     * 
     * Consistent hashing ensures same user → same variant.
     */
    public Variant assignUser(String experimentId, String userId) {
        Experiment experiment = experiments.get(experimentId);
        if (experiment == null || !experiment.isActive()) {
            return null;
        }
        
        // Check if already assigned
        String key = experimentId + ":" + userId;
        ExperimentAssignment existing = assignments.get(key);
        if (existing != null) {
            return existing.getVariant();
        }
        
        // Assign based on consistent hashing
        List<Variant> variants = experiment.getVariants();
        int hash = Math.abs(userId.hashCode());
        int index = hash % variants.size();
        Variant variant = variants.get(index);
        
        // Store assignment
        ExperimentAssignment assignment = new ExperimentAssignment(
            userId, experimentId, variant
        );
        assignments.put(key, assignment);
        
        return variant;
    }

    /**
     * Track metric for user.
     */
    public void trackMetric(String experimentId, String userId, String metricName, 
                           double value) {
        Experiment experiment = experiments.get(experimentId);
        if (experiment == null) return;
        
        // Get user's variant
        Variant variant = assignUser(experimentId, userId);
        if (variant == null) return;
        
        // Record metric
        Metric metric = new Metric(userId, experimentId, variant.getName(), 
                                   metricName, value);
        experiment.recordMetric(metric);
    }

    /**
     * Get experiment results.
     */
    public ExperimentResult getResults(String experimentId) {
        Experiment experiment = experiments.get(experimentId);
        if (experiment == null) return null;
        
        return experiment.calculateResults();
    }
}

/**
 * Experiment.
 */
class Experiment {
    private final String experimentId;
    private String name;
    private boolean active;
    private final List<Variant> variants;
    private final Map<String, List<Metric>> metrics;

    Experiment(String experimentId, String name) {
        this.experimentId = experimentId;
        this.name = name;
        this.variants = new ArrayList<>();
        this.metrics = new ConcurrentHashMap<>();
    }

    void addVariant(Variant variant) {
        variants.add(variant);
    }

    void recordMetric(Metric metric) {
        String variantName = metric.getVariantName();
        metrics.computeIfAbsent(variantName, k -> new CopyOnWriteArrayList<>())
               .add(metric);
    }

    ExperimentResult calculateResults() {
        ExperimentResult result = new ExperimentResult();
        
        for (Variant variant : variants) {
            List<Metric> variantMetrics = metrics.getOrDefault(
                variant.getName(), new ArrayList<>()
            );
            
            // Calculate conversion rate
            long conversions = variantMetrics.stream()
                .filter(m -> "conversion".equals(m.getMetricName()))
                .count();
            
            double rate = variantMetrics.isEmpty() ? 0 : 
                         (double) conversions / variantMetrics.size();
            
            result.addVariantResult(variant.getName(), rate, variantMetrics.size());
        }
        
        return result;
    }

    public String getId() { return experimentId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Variant> getVariants() { return variants; }
}

class Variant {
    private final String name;
    private double trafficSplit;

    Variant(String name, double trafficSplit) {
        this.name = name;
        this.trafficSplit = trafficSplit;
    }

    public String getName() { return name; }
}

class Metric {
    private final String userId;
    private final String experimentId;
    private final String variantName;
    private final String metricName;
    private final double value;
    private final long timestamp;

    Metric(String userId, String experimentId, String variantName, 
           String metricName, double value) {
        this.userId = userId;
        this.experimentId = experimentId;
        this.variantName = variantName;
        this.metricName = metricName;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUserId() { return userId; }
    public String getVariantName() { return variantName; }
    public String getMetricName() { return metricName; }
    public double getValue() { return value; }
}

class ExperimentAssignment {
    private final String userId;
    private final String experimentId;
    private final Variant variant;

    ExperimentAssignment(String userId, String experimentId, Variant variant) {
        this.userId = userId;
        this.experimentId = experimentId;
        this.variant = variant;
    }

    public Variant getVariant() { return variant; }
}

class ExperimentResult {
    private final Map<String, VariantResult> results;

    ExperimentResult() {
        this.results = new LinkedHashMap<>();
    }

    void addVariantResult(String variantName, double rate, int sampleSize) {
        results.put(variantName, new VariantResult(variantName, rate, sampleSize));
    }

    public Map<String, VariantResult> getResults() { return results; }
}

class VariantResult {
    private final String variantName;
    private final double conversionRate;
    private final int sampleSize;

    VariantResult(String variantName, double rate, int sampleSize) {
        this.variantName = variantName;
        this.conversionRate = rate;
        this.sampleSize = sampleSize;
    }

    public String getVariantName() { return variantName; }
    public double getConversionRate() { return conversionRate; }
    public int getSampleSize() { return sampleSize; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multiple simultaneous experiments?"
> "Mutual exclusion groups. Domain-based bucketing. Layered bucketing."

### Q2: "How to determine statistical significance?"
> "P-value calculation. Chi-square test. Confidence intervals."

### Q3: "How to handle novelt effect?"
> "Run experiment for 2+ weeks. Check day-1 vs day-14 retention."

### Q4: "How to handle personalization?"
> "User segments. Contextual bandits. ML-based targeting."