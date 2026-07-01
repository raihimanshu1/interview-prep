# 📋 Problem 58: Order Management System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: E-commerce, any retail  
> **Est. Time**: 90 min | **Patterns**: State Machine, Observer, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design an order management system (OMS)."

**What the interviewer tests**:
```
1. Can you model order lifecycle? (States and transitions)
2. Can you handle order modifications? (Before shipping)
3. Can you track fulfillment? (Warehouse → shipping → delivery)
4. Can you handle cancellations? (With refund)
```

### Step 2: The "Aha!" Moment

The key insight: **Order is a state machine with saga pattern.**

```
ORDER STATES:
  CREATED → CONFIRMED → PACKING → SHIPPED → DELIVERED
              ↓           ↓         ↓
           CANCELLED    RETURN    RETURNED
  
SAGA (distributed transaction):
  Order created → Inventory reserved → Payment captured → Ship
  
  If payment fails:
    Compensate: Release inventory, cancel order
```

### Step 3: How to handle order modifications?

```
MODIFICATION WINDOW:
  - CREATED: Can modify (add/remove items, change address)
  - CONFIRMED: Can cancel only
  - PACKING+: No modifications (except cancel with penalty)
  
IDEMPOTENCY:
  Each modification has unique ID
  Retry safe: same ID = same result
```

---

## 💻 Core Implementation

```java
package com.order;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: OrderService manages order lifecycle.
 */
public class OrderService {
    
    private final Map<String, Order> orders;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    public OrderService() {
        this.orders = new ConcurrentHashMap<>();
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.shippingService = new ShippingService();
        this.notificationService = new NotificationService();
    }

    /**
     * INTUITION: Create order from cart.
     */
    public synchronized Order createOrder(String userId, String shippingAddress, 
                                           String paymentMethodId) {
        // Get cart
        ShoppingCart cart = cartService.getCart(userId);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        
        // Calculate total
        CartTotal total = cart.calculateTotal();
        
        // Create order
        String orderId = generateOrderId();
        Order order = new Order(orderId, userId, shippingAddress, total);
        
        // Add order items
        for (CartItem item : cart.getItems().values()) {
            OrderItem orderItem = new OrderItem(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            );
            order.addItem(orderItem);
        }
        
        // Set payment method
        order.setPaymentMethodId(paymentMethodId);
        
        // Update inventory
        try {
            for (OrderItem item : order.getItems()) {
                inventoryService.reserveStock(item.getProductId(), item.getQuantity());
            }
        } catch (InsufficientStockException e) {
            throw new OrderCreationException("Insufficient stock", e);
        }
        
        orders.put(orderId, order);
        cart.setStatus(CartStatus.CONVERTED);
        
        // Notify
        notificationService.sendOrderConfirmation(userId, order);
        
        return order;
    }

    /**
     * INTUITION: Confirm payment.
     */
    public synchronized void confirmPayment(String orderId, String paymentId) {
        Order order = orders.get(orderId);
        if (order == null) throw new OrderNotFoundException(orderId);
        
        order.setPaymentId(paymentId);
        order.setStatus(OrderStatus.CONFIRMED);
        
        // Trigger fulfillment
        fulfillOrder(order);
    }

    /**
     * INTUITION: Fulfill order (warehouse processing).
     */
    private void fulfillOrder(Order order) {
        order.setStatus(OrderStatus.PACKING);
        
        // Assign warehouse
        String warehouseId = findBestWarehouse(order);
        order.setWarehouseId(warehouseId);
        
        // Simulate packing
        new Thread(() -> {
            try {
                Thread.sleep(5000);  // 5s packing time
                
                // Ship
                String trackingNumber = shippingService.createShipment(
                    order.getId(), warehouseId, order.getShippingAddress()
                );
                order.setTrackingNumber(trackingNumber);
                order.setStatus(OrderStatus.SHIPPED);
                
                // Capture payment
                paymentService.capture(order.getPaymentId(), order.getTotal());
                
                // Notify
                notificationService.sendShipmentNotification(
                    order.getUserId(), order
                );
                
            } catch (Exception e) {
                order.setStatus(OrderStatus.FAILED);
                notificationService.sendOrderFailureNotification(
                    order.getUserId(), order
                );
            }
        }).start();
    }

    /**
     * INTUITION: Cancel order.
     */
    public synchronized void cancelOrder(String orderId, String reason) {
        Order order = orders.get(orderId);
        if (order == null) throw new OrderNotFoundException(orderId);
        
        // Can only cancel if not shipped
        if (order.getStatus() == OrderStatus.SHIPPED || 
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped order");
        }
        
        // Compensating transactions
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            // Refund payment
            paymentService.refund(order.getPaymentId(), order.getTotal());
        }
        
        // Release inventory
        for (OrderItem item : order.getItems()) {
            inventoryService.releaseStock(item.getProductId(), order.getWarehouseId());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        
        notificationService.sendCancellationNotification(order.getUserId(), order);
    }

    /**
     * INTUITION: Return order.
     */
    public synchronized void returnOrder(String orderId, String reason) {
        Order order = orders.get(orderId);
        if (order == null) throw new OrderNotFoundException(orderId);
        
        order.setStatus(OrderStatus.RETURN_INITIATED);
        
        // Process return
        new Thread(() -> {
            try {
                Thread.sleep(3000);  // Inspect return
                
                // Refund
                paymentService.refund(order.getPaymentId(), order.getTotal());
                
                // Restock
                for (OrderItem item : order.getItems()) {
                    inventoryService.restock(item.getProductId(), order.getWarehouseId());
                }
                
                order.setStatus(OrderStatus.RETURNED);
                notificationService.sendReturnConfirmation(order.getUserId(), order);
                
            } catch (Exception e) {
                order.setStatus(OrderStatus.RETURN_FAILED);
            }
        }).start();
    }

    private String findBestWarehouse(Order order) {
        // Simplified: return first warehouse
        return "WH1";
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public List<Order> getUserOrders(String userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUserId().equals(userId)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }
}

/**
 * Order entity.
 */
class Order {
    private final String orderId;
    private final String userId;
    private final List<OrderItem> items;
    private final String shippingAddress;
    private OrderStatus status;
    private String paymentId;
    private String trackingNumber;
    private String warehouseId;
    private String cancellationReason;
    private CartTotal total;
    private final LocalDateTime createdAt;

    Order(String orderId, String userId, String shippingAddress, CartTotal total) {
        this.orderId = orderId;
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.total = total;
        this.items = new CopyOnWriteArrayList<>();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    void addItem(OrderItem item) {
        items.add(item);
    }

    // Getters
    public String getId() { return orderId; }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String tn) { this.trackingNumber = tn; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String wh) { this.warehouseId = wh; }
    public double getTotal() { return total.getTotal(); }
}

enum OrderStatus {
    CREATED, CONFIRMED, PACKING, SHIPPED, DELIVERED,
    CANCELLED, RETURN_INITIATED, RETURNED, FAILED
}

class OrderItem {
    private final String productId;
    private final int quantity;
    private final double price;

    OrderItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}

class OrderCreationException extends RuntimeException {
    public OrderCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order not found: " + id);
    }
}
```

```java
package com.order;

/**
 * Mock services (would be separate in production).
 */
class CartService {
    ShoppingCart getCart(String userId) {
        // Mock
        return new ShoppingCart("CART1", userId);
    }
}

class InventoryService {
    void reserveStock(String productId, int qty) {
        System.out.println("Reserving " + qty + " of " + productId);
    }
    void releaseStock(String pid, String wh) {}
    void restock(String pid, String wh) {}
}

class PaymentService {
    void capture(String paymentId, double amount) {
        System.out.println("Capturing payment: $" + amount);
    }
    void refund(String paymentId, double amount) {
        System.out.println("Refunding: $" + amount);
    }
}

class ShippingService {
    String createShipment(String orderId, String wh, String address) {
        System.out.println("Creating shipment for " + orderId);
        return "TRACK" + System.currentTimeMillis();
    }
}

class NotificationService {
    void sendOrderConfirmation(String userId, Order order) {
        System.out.println("Order confirmation sent to " + userId);
    }
    void sendShipmentNotification(String userId, Order order) {
        System.out.println("Shipment notification sent to " + userId);
    }
    void sendCancellationNotification(String userId, Order order) {
        System.out.println("Cancellation notification sent to " + userId);
    }
    void sendReturnConfirmation(String userId, Order order) {
        System.out.println("Return confirmation sent to " + userId);
    }
    void sendOrderFailureNotification(String userId, Order order) {
        System.out.println("Order failure notification sent to " + userId);
    }
}

enum OrderStatus {
    CREATED, CONFIRMED, PACKING, SHIPPED, DELIVERED,
    CANCELLED, RETURN_INITIATED, RETURNED, FAILED
}

class ShoppingCart {
    private final String cartId;
    private final String userId;
    private final Map<String, CartItem> items;
    private CartStatus status;

    ShoppingCart(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = new ConcurrentHashMap<>();
        this.status = CartStatus.ACTIVE;
    }

    boolean isEmpty() { return items.isEmpty(); }
    Map<String, CartItem> getItems() { return items; }
    CartStatus getStatus() { return status; }
    void setStatus(CartStatus status) { this.status = status; }
}

class CartItem {
    private final String productId;
    private final int quantity;
    private final double price;

    CartItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}

class CartTotal {
    private final double subtotal;
    private final double tax;
    private final double shipping;
    private final double total;

    CartTotal(double s, double t, double sh, double tot) {
        this.subtotal = s; this.tax = t; this.shipping = sh; this.total = tot;
    }

    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getShipping() { return shipping; }
    public double getTotal() { return total; }
}

enum CartStatus { ACTIVE, CONVERTED }
```

---

## ❓ Follow-up Questions

### Q1: "How to handle partial cancellations?"
> "Split order into sub-orders. Cancel individual items. Refund proportionally."

### Q2: "How to handle international shipping?"
> "Customs calculation. Duties and taxes. Restricted items check. Carrier selection."

### Q3: "How to handle order modifications?"
> "Version order. Track changes in audit log. Re-calculate pricing. Re-reserve inventory."

### Q4: "How to handle fraud detection?"
> "Risk scoring: shipping address, payment method, order velocity. Review if score > threshold."