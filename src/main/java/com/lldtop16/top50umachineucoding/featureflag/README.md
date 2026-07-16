# 🚩 Problem 66: Feature Flag System (LaunchDarkly-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: SaaS, product companies  
> **Est. Time**: 90 min | **Patterns**: Strategy, Observer, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a feature flag system for gradual rollouts."

**What the interviewer tests**:
```
1. Can you enable/disable features? (Toggle)
2. Can you target users? (Percentage, segments)
3. Can you do gradual rollouts? (10% → 50% → 100%)
4. Can you A/B test? (Multiple variants)
```

### Step 2: The "Aha!" Moment

The key insight: **Feature flags = rules engine for feature access.**

```
FLAG TYPES:
1. Boolean: feature_enabled = true/false
2. Multivariate: variant = "control" | "treatment_a" | "treatment_b"
3. Percentage: rollout = 10% of users

USE CASES:
  - Gradual rollout: 10% → 50% → 100%
  - Kill switch: Disable broken feature instantly
  - A/B testing: Test two variants
  - Targeted: Enable for beta users only
```

### Step 3: How to evaluate flags?

```
EVALUATION:
  1. Get user context (user_id, country, tier)
  2. Get flag rules
  3. Match rules against context
  4. Return flag value
  
RULES:
  - If user in segment "beta" → true
  - If user_id hash % 100 < 10 → true (10% rollout)
  - Default: false
```

---

## 💻 Core Implementation

```java
package com.featureflag;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: FeatureFlagService manages feature flags.
 */
public class FeatureFlagService {
    
    private final Map<String, FeatureFlag> flags;
    private final Map<String, UserSegment> segments;
    private final Random random;

    public FeatureFlagService() {
        this.flags = new ConcurrentHashMap<>();
        this.segments = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    /**
     * Create feature flag.
     */
    public void createFlag(FeatureFlag flag) {
        flags.put(flag.getName(), flag);
    }

    /**
     * INTUITION: Evaluate flag for user.
     */
    public boolean evaluate(String flagName, UserContext user) {
        FeatureFlag flag = flags.get(flagName);
        if (flag == null || !flag.isEnabled()) {
            return false;
        }
        
        return evaluateRules(flag, user);
    }

    /**
     * Evaluate flag rules against user context.
     */
    private boolean evaluateRules(FeatureFlag flag, UserContext user) {
        // Check user segments
        for (UserSegment segment : flag.getSegments()) {
            if (user.getSegments().contains(segment.getName())) {
                return true;
            }
        }
        
        // Check percentage rollout
        if (flag.getRolloutPercentage() < 100) {
            int hash = Math.abs(user.getUserId().hashCode());
            int bucket = hash % 100;
            return bucket < flag.getRolloutPercentage();
        }
        
        return flag.isEnabled();
    }

    /**
     * Update flag (hot reload).
     */
    public synchronized void updateFlag(String flagName, boolean enabled, int rollout) {
        FeatureFlag flag = flags.get(flagName);
        if (flag != null) {
            flag.setEnabled(enabled);
            flag.setRolloutPercentage(rollout);
        }
    }
}

/**
 * Feature flag.
 */
class FeatureFlag {
    private final String name;
    private boolean enabled;
    private int rolloutPercentage;
    private final List<UserSegment> segments;

    FeatureFlag(String name, boolean enabled, int rollout) {
        this.name = name;
        this.enabled = enabled;
        this.rolloutPercentage = rollout;
        this.segments = new ArrayList<>();
    }

    void addSegment(UserSegment segment) {
        segments.add(segment);
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRolloutPercentage() { return rolloutPercentage; }
    public void setRolloutPercentage(int pct) { this.rolloutPercentage = pct; }
    public List<UserSegment> getSegments() { return segments; }
}

/**
 * User segment (targeting group).
 */
class UserSegment {
    private final String name;
    private final Set<String> userIds;

    UserSegment(String name) {
        this.name = name;
        this.userIds = new HashSet<>();
    }

    void addUser(String userId) {
        userIds.add(userId);
    }

    boolean contains(String userId) {
        return userIds.contains(userId);
    }

    public String getName() { return name; }
}

/**
 * User context for flag evaluation.
 */
class UserContext {
    private final String userId;
    private final Set<String> segments;
    private final Map<String, String> attributes;

    UserContext(String userId) {
        this.userId = userId;
        this.segments = new HashSet<>();
        this.attributes = new HashMap<>();
    }

    void addSegment(String segment) {
        segments.add(segment);
    }

    public String getUserId() { return userId; }
    public Set<String> getSegments() { return segments; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle flag dependencies?"
> "Flag hierarchy. Parent-child flags. Evaluate parents first."

### Q2: "How to handle emergency kill switch?"
> "Global override. Cache flag locally with TTL. Push update."

### Q3: "How to track flag performance?"
> "Metrics: impressions, conversions. Analytics integration."

### Q4: "How to handle flag cleanup?"
> "Lifecycle management. Sunset after 30 days. Automated cleanup."