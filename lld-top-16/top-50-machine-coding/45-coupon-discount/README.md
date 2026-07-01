# 🎟️ Problem 45: Coupon & Discount System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: E-commerce, retail companies  
> **Est. Time**: 90 min | **Patterns**: Strategy, Chain of Responsibility, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a coupon and discount management system."

**What the interviewer tests**:
```
1. Can you model different discount types? (Percentage, fixed, BOGO)
2. Can you apply coupon rules? (Min spend, validity, user-specific)
3. Can you stack coupons? (Combine multiple)
4. Can you prevent fraud? (Abuse detection)
```

### Step 2: The "Aha!" Moment

The key insight: **Discounts are composable rules.**

```
DISCOUNT TYPES:
  1. Percentage: 20% off
  2. Fixed: $10 off
  3. BOGO: Buy 1 Get 1 Free
  4. Tiered: Spend $100 save $20, spend $200 save $50

RULES:
  - Minimum order value: $50
  - Valid for: New users only
  - Category: Electronics only
  - Time: Black Friday 2026

STACKING:
  User has: SAVE20 (20% off) + FREESHIP (free shipping)
  Cart: $100
  Apply: $100 - 20% = $80, then free shipping
  Total saved: $20 + $5 shipping = $25
```

### Step 3: How to prevent abuse?

```
FRAUD DETECTION:
  - Max uses per user: 1
  - Max total uses: 1000
  - IP tracking: same IP, different accounts → suspicious
  - Velocity check: 5 uses in 1 minute → block

BUDGET:
  - Marketing budget: $10,000
  - Each discount costs X
  - Stop issuing when budget exhausted
```

---

## 💻 Core Implementation

```java
package com.coupon;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: CouponService manages discount application.
 * 
 * Flow:
 * 1. User applies coupon
 * 2. Validate coupon (exists, active, not expired)
 * 3. Check eligibility (user, cart, time)
 * 4. Calculate discount
 * 5. Apply to cart
 */
public class CouponService {
    
    private final Map<String, Coupon> coupons;
    private final Map<String, List<String>> userCouponUsage;
    private final DiscountStrategyFactory strategyFactory;
    private final CouponUsageTracker usageTracker;

    public CouponService() {
        this.coupons = new ConcurrentHashMap<>();
        this.userCouponUsage = new ConcurrentHashMap<>();
        this.strategyFactory = new DiscountStrategyFactory();
        this.usageTracker = new CouponUsageTracker();
        
        // Initialize sample coupons
        initializeCoupons();
    }

    /**
     * INTUITION: Apply coupon to cart.
     */
    public synchronized DiscountResult applyCoupon(String userId, String couponCode, Cart cart) {
        Coupon coupon = coupons.get(couponCode);
        if (coupon == null) {
            return DiscountResult.failure("Invalid coupon code");
        }
        
        // Step 1: Validate coupon
        if (!coupon.isActive()) {
            return DiscountResult.failure("Coupon is not active");
        }
        
        if (coupon.isExpired()) {
            return DiscountResult.failure("Coupon has expired");
        }
        
        // Step 2: Check user eligibility
        if (!isUserEligible(userId, coupon)) {
            return DiscountResult.failure("You are not eligible for this coupon");
        }
        
        // Step 3: Check cart eligibility
        if (!isCartEligible(cart, coupon)) {
            return DiscountResult.failure("Cart does not meet minimum requirements");
        }
        
        // Step 4: Check usage limits
        if (!usageTracker.canUse(coupon.getId(), userId)) {
            return DiscountResult.failure("Coupon usage limit reached");
        }
        
        // Step 5: Calculate discount
        DiscountStrategy strategy = strategyFactory.getStrategy(coupon.getType());
        double discount = strategy.calculateDiscount(cart, coupon);
        
        // Step 6: Apply discount
        cart.applyDiscount(discount);
        
        // Step 7: Record usage
        usageTracker.recordUsage(coupon.getId(), userId);
        
        return DiscountResult.success(coupon, discount);
    }

    /**
     * INTUITION: Check if user is eligible for coupon.
     */
    private boolean isUserEligible(String userId, Coupon coupon) {
        // Check user-specific restrictions
        if (coupon.getUserType() == UserType.NEW && !isNewUser(userId)) {
            return false;
        }
        
        // Check if user already used this coupon
        List<String> used = userCouponUsage.getOrDefault(userId, Collections.emptyList());
        if (used.contains(coupon.getId())) {
            return false;
        }
        
        return true;
    }

    /**
     * INTUITION: Check if cart meets coupon requirements.
     */
    private boolean isCartEligible(Cart cart, Coupon coupon) {
        // Minimum order value
        if (coupon.getMinOrderValue() > 0 && cart.getSubtotal() < coupon.getMinOrderValue()) {
            return false;
        }
        
        // Category restriction
        if (coupon.getAllowedCategories() != null && !coupon.getAllowedCategories().isEmpty()) {
            boolean hasAllowedCategory = cart.getItems().stream()
                .anyMatch(item -> coupon.getAllowedCategories().contains(item.getCategory()));
            if (!hasAllowedCategory) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isNewUser(String userId) {
        // Check if user's first order
        List<String> used = userCouponUsage.getOrDefault(userId, Collections.emptyList());
        return used.isEmpty();
    }

    public Coupon createCoupon(String code, DiscountType type, double value, 
                                Date validFrom, Date validUntil) {
        Coupon coupon = new Coupon(code, type, value, validFrom, validUntil);
        coupons.put(code, coupon);
        return coupon;
    }

    private void initializeCoupons() {
        // Sample coupons
        createCoupon("SAVE20", DiscountType.PERCENTAGE, 20.0, 
            new Date(), new Date(System.currentTimeMillis() + 86400000L));
        createCoupon("FLAT10", DiscountType.FIXED, 10.0,
            new Date(), new Date(System.currentTimeMillis() + 86400000L));
        createCoupon("BOGO50", DiscountType.BUY_ONE_GET_ONE, 50.0,
            new Date(), new Date(System.currentTimeMillis() + 86400000L));
    }
}
```

```java
package com.coupon;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Coupon represents a discount offer.
 */
public class Coupon {
    private final String code;
    private final DiscountType type;
    private final double value;
    private final Date validFrom;
    private final Date validUntil;
    private final int maxUses;
    private final int maxUsesPerUser;
    private final double minOrderValue;
    private final Set<String> allowedCategories;
    private final UserType userType;
    private final AtomicInteger totalUsed;
    private boolean active;

    public Coupon(String code, DiscountType type, double value, 
                  Date validFrom, Date validUntil) {
        this(code, type, value, validFrom, validUntil, 1000, 1, 0, null, UserType.ALL, true);
    }

    public Coupon(String code, DiscountType type, double value, 
                  Date validFrom, Date validUntil,
                  int maxUses, int maxUsesPerUser, double minOrderValue,
                  Set<String> allowedCategories, UserType userType, boolean active) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.maxUses = maxUses;
        this.maxUsesPerUser = maxUsesPerUser;
        this.minOrderValue = minOrderValue;
        this.allowedCategories = allowedCategories;
        this.userType = userType;
        this.active = active;
        this.totalUsed = new AtomicInteger(0);
    }

    public boolean isExpired() {
        return new Date().after(validUntil);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getTotalUsed() { return totalUsed.get(); }
    public void incrementUsage() { totalUsed.incrementAndGet(); }

    // Getters
    public String getCode() { return code; }
    public DiscountType getType() { return type; }
    public double getValue() { return value; }
    public int getMaxUses() { return maxUses; }
    public int getMaxUsesPerUser() { return maxUsesPerUser; }
    public double getMinOrderValue() { return minOrderValue; }
    public Set<String> getAllowedCategories() { return allowedCategories; }
    public UserType getUserType() { return userType; }
}

enum DiscountType {
    PERCENTAGE,           // 20% off
    FIXED,                // $10 off
    BUY_ONE_GET_ONE,      // BOGO
    TIERED                // Spend $100 save $20
}

enum UserType {
    NEW,                  // First-time users only
    EXISTING,             // Existing users only
    ALL                   // Everyone
}
```

```java
package com.coupon;

import java.util.*;

/**
 * INTUITION: DiscountStrategy calculates discount amount.
 * 
 * Strategy pattern: different strategies for different discount types.
 */
interface DiscountStrategy {
    double calculateDiscount(Cart cart, Coupon coupon);
}

/**
 * Percentage discount: 20% off
 */
class PercentageDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Cart cart, Coupon coupon) {
        return cart.getSubtotal() * (coupon.getValue() / 100.0);
    }
}

/**
 * Fixed amount discount: $10 off
 */
class FixedDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Cart cart, Coupon coupon) {
        return Math.min(coupon.getValue(), cart.getSubtotal());
    }
}

/**
 * Buy One Get One: Buy N Get M free (here: buy 1 get 1)
 */
class BuyOneGetOneStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Cart cart, Coupon coupon) {
        // Buy 1 get 1: price of cheapest item is free
        return cart.getItems().stream()
            .mapToDouble(CartItem::getPrice)
            .min()
            .orElse(0);
    }
}

/**
 * Tiered discount: different discounts at different thresholds
 */
class TieredDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(Cart cart, Coupon coupon) {
        double subtotal = cart.getSubtotal();
        
        // Example: Spend $100 save $20, spend $200 save $50
        if (subtotal >= 200) return 50.0;
        if (subtotal >= 100) return 20.0;
        return 0.