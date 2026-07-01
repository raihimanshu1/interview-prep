# 🛒 Problem 57: Shopping Cart (E-Commerce)

> **Difficulty**: ⭐⭐ | **Company Fit**: E-commerce companies  
> **Est. Time**: 60 min | **Patterns**: Observer, Strategy, State

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a shopping cart for e-commerce."

**What the interviewer tests**:
```
1. Can you manage cart items? (Add, remove, update)
2. Can you handle quantities? (Stock validation)
3. Can you calculate totals? (Subtotal, tax, discount)
4. Can you persist carts? (Guest vs logged-in)
```

### Step 2: The "Aha!" Moment

The key insight: **Cart is a stateful collection with pricing rules.**

```
CART STATES:
  ACTIVE: User is shopping
  SAVED: Saved for later
  CONVERTED: Became order
  ABANDONED: Left without purchasing
  
CART TYPES:
  - Guest: stored in session/DB with token
  - User: stored in DB, synced across devices
  
PRICING:
  Item price × quantity
  + tax
  + shipping
  - discount
  = total
```

### Step 3: How to handle complex pricing?

```
PRICING RULES:
  - Bulk discount: Buy 5+ get 10% off
  - Bundle: Buy A + B together save $5
  - Coupon: SAVE20 (20% off)
  - Tiered: Free shipping on $50+
  
LOAD ORDER:
  1. Calculate subtotal
  2. Apply item-level discounts
  3. Apply cart-level discounts
  4. Add tax
  5. Add shipping
  6. Round to 2 decimals
```

---

## 💻 Core Implementation

```java
package com.cart;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ShoppingCart manages cart items and pricing.
 */
public class ShoppingCart {
    
    private final String cartId;
    private final String userId;  // null for guest
    private final Map<String, CartItem> items;
    private CartStatus status;
    private Coupon appliedCoupon;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShoppingCart(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = new ConcurrentHashMap<>();
        this.status = CartStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * INTUITION: Add item to cart.
     */
    public synchronized void addItem(String productId, int quantity, double price) {
        CartItem item = items.get(productId);
        
        if (item == null) {
            item = new CartItem(productId, quantity, price);
            items.put(productId, item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        
        updatedAt = LocalDateTime.now();
    }

    /**
     * INTUITION: Update item quantity.
     */
    public synchronized void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }
        
        CartItem item = items.get(productId);
        if (item != null) {
            item.setQuantity(quantity);
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * INTUITION: Remove item from cart.
     */
    public synchronized void removeItem(String productId) {
        items.remove(productId);
        updatedAt = LocalDateTime.now();
    }

    /**
     * INTUITION: Apply coupon.
     */
    public synchronized boolean applyCoupon(String couponCode) {
        Coupon coupon = CouponService.validate(couponCode, this);
        if (coupon != null) {
            this.appliedCoupon = coupon;
            updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * INTUITION: Calculate cart total.
     */
    public CartTotal calculateTotal() {
        double subtotal = 0;
        double itemDiscount = 0;
        
        // Calculate subtotal
        for (CartItem item : items.values()) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        
        // Apply item-level discounts (bulk, bundle)
        for (CartItem item : items.values()) {
            double discount = calculateItemDiscount(item);
            itemDiscount += discount;
        }
        
        double afterItems = subtotal - itemDiscount;
        
        // Apply coupon
        double couponDiscount = 0;
        if (appliedCoupon != null) {
            couponDiscount = calculateCouponDiscount(afterItems);
        }
        
        double afterCoupon = afterItems - couponDiscount;
        
        // Add tax
        double tax = afterCoupon * 0.08;  // 8% tax
        
        // Add shipping
        double shipping = afterCoupon >= 50 ? 0 : 5.99;
        
        double total = afterCoupon + tax + shipping;
        
        return new CartTotal(
            subtotal,
            itemDiscount,
            couponDiscount,
            tax,
            shipping,
            Math.round(total * 100) / 100.0
        );
    }

    private double calculateItemDiscount(CartItem item) {
        // Bulk discount: 10% off for 5+ items
        if (item.getQuantity() >= 5) {
            return item.getPrice() * item.getQuantity() * 0.1;
        }
        return 0;
    }

    private double calculateCouponDiscount(double amount) {
        switch (appliedCoupon.getType()) {
            case PERCENTAGE:
                return amount * (appliedCoupon.getValue() / 100.0);
            case FIXED:
                return Math.min(appliedCoupon.getValue(), amount);
            default:
                return 0;
        }
    }

    /**
     * Merge guest cart with user cart on login.
     */
    public static ShoppingCart merge(ShoppingCart guest, ShoppingCart user) {
        ShoppingCart merged = new ShoppingCart(user.getCartId(), user.getUserId());
        
        // Add user items
        for (CartItem item : user.getItems().values()) {
            merged.addItem(item.getProductId(), item.getQuantity(), item.getPrice());
        }
        
        // Add guest items (merge quantities)
        for (CartItem item : guest.getItems().values()) {
            CartItem existing = merged.items.get(item.getProductId());
            if (existing != null) {
                merged.addItem(item.getProductId(), item.getQuantity(), item.getPrice());
            } else {
                merged.addItem(item.getProductId(), item.getQuantity(), item.getPrice());
            }
        }
        
        return merged;
    }

    // --- Getters ---

    public String getCartId() { return cartId; }
    public String getUserId() { return userId; }
    public Map<String, CartItem> getItems() { return Collections.unmodifiableMap(items); }
    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
    public int getItemCount() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }
}

/**
 * Cart item.
 */
class CartItem {
    private final String productId;
    private int quantity;
    private final double price;
    private final LocalDateTime addedAt;

    CartItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.addedAt = LocalDateTime.now();
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
}

/**
 * Cart total breakdown.
 */
class CartTotal {
    private final double subtotal;
    private final double itemDiscount;
    private final double couponDiscount;
    private final double tax;
    private final double shipping;
    private final double total;

    CartTotal(double subtotal, double itemDiscount, double couponDiscount,
              double tax, double shipping, double total) {
        this.subtotal = subtotal;
        this.itemDiscount = itemDiscount;
        this.couponDiscount = couponDiscount;
        this.tax = tax;
        this.shipping = shipping;
        this.total = total;
    }

    public double getSubtotal() { return subtotal; }
    public double getItemDiscount() { return itemDiscount; }
    public double getCouponDiscount() { return couponDiscount; }
    public double getTax() { return tax; }
    public double getShipping() { return shipping; }
    public double getTotal() { return total; }
}

enum CartStatus {
    ACTIVE, SAVED, CONVERTED, ABANDONED
}

/**
 * Coupon.
 */
class Coupon {
    private final String code;
    private final CouponType type;
    private final double value;
    private final int minAmount;
    private final Date validUntil;

    Coupon(String code, CouponType type, double value, int minAmount, Date validUntil) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.minAmount = minAmount;
        this.validUntil = validUntil;
    }

    public String getCode() { return code; }
    public CouponType getType() { return type; }
    public double getValue() { return value; }
    public int getMinAmount() { return minAmount; }
}

enum CouponType {
    PERCENTAGE, FIXED
}

/**
 * Mock coupon service.
 */
class CouponService {
    static Coupon validate(String code, ShoppingCart cart) {
        // Mock validation
        if ("SAVE10".equals(code) && cart.calculateTotal().getSubtotal() >= 50) {
            return new Coupon(code, CouponType.PERCENTAGE, 10, 50, new Date());
        }
        return null;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle cart abandonment?"
> "Email reminder after 1 hour, 24 hours. Push notification. Discount incentive."

### Q2: "How to sync cart across devices?"
> "Store in DB tied to user. Load on login. Merge guest + user carts."

### Q3: "How to handle price changes?"
> "Lock price at add-to-cart. Show 'price changed' alert. Allow price match."

### Q4: "How to handle inventory conflicts?"
> "Check stock on add-to-cart. Reserve for 15 min. Release on abandon."