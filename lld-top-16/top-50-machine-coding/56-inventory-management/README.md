# 📦 Problem 56: Inventory Management System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: E-commerce, retail  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, State

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Track inventory across warehouses."

**What the interviewer tests**:
```
1. Can you track stock levels? (Real-time updates)
2. Can you handle reservations? (Pending orders)
3. Can you trigger alerts? (Low stock)
4. Can you handle returns? (Restock)
```

### Step 2: The "Aha!" Moment

The key insight: **Inventory is a state machine with events.**

```
STOCK STATES:
  IN_STOCK: Available for sale
  RESERVED: Reserved for pending order
  SOLD: Shipped to customer
  RETURNED: Back in warehouse
  
FLOW:
  Customer orders → RESERVE → CAPTURE → SOLD
  Customer cancels → RELEASE → IN_STOCK
  Customer returns → RESTOCK → IN_STOCK
```

### Step 3: How to handle concurrency?

```
CONSISTENCY:
  - Pessimistic locking: lock on update
  - Optimistic: CAS (compare-and-swap)
  - Eventual: queue updates, reconcile
  
OVERSELLING:
  Track: available = physical - reserved - pending
  
  Example:
  Physical: 100
  Reserved: 30 (pending orders)
  Available: 70
  
  Prevent selling more than 70.
```

---

## 💻 Core Implementation

```java
package com.inventory;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: InventoryService tracks stock levels.
 */
public class InventoryService {
    
    private final Map<String, Warehouse> warehouses;
    private final Map<String, Product> products;
    private final AlertService alertService;

    public InventoryService() {
        this.warehouses = new ConcurrentHashMap<>();
        this.products = new ConcurrentHashMap<>();
        this.alertService = new AlertService();
    }

    /**
     * INTUITION: Reserve stock for order.
     */
    public synchronized Reservation reserveStock(String productId, int quantity, 
                                                  String warehouseId) {
        Warehouse warehouse = warehouses.get(warehouseId);
        Product product = products.get(productId);
        
        if (product == null || warehouse == null) {
            throw new IllegalArgumentException("Invalid product or warehouse");
        }
        
        Stock stock = warehouse.getStock(productId);
        if (stock == null || stock.getAvailable() < quantity) {
            throw new InsufficientStockException("Not enough stock");
        }
        
        // Reserve
        stock.reserve(quantity);
        
        // Create reservation
        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
            reservationId, productId, warehouseId, quantity
        );
        
        // Schedule auto-release after 15 min
        scheduleRelease(reservationId, productId, warehouseId, quantity, 15);
        
        // Check low stock
        if (stock.getAvailable() < product.getLowStockThreshold()) {
            alertService.sendLowStockAlert(productId, warehouseId, stock.getAvailable());
        }
        
        return reservation;
    }

    /**
     * INTUITION: Capture reserved stock (order shipped).
     */
    public synchronized void captureStock(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) return;
        
        Warehouse warehouse = warehouses.get(reservation.getWarehouseId());
        Stock stock = warehouse.getStock(reservation.getProductId());
        
        stock.capture(reservation.getQuantity());
        reservations.remove(reservationId);
    }

    /**
     * INTUITION: Release reservation (order cancelled).
     */
    public synchronized void releaseStock(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) return;
        
        Warehouse warehouse = warehouses.get(reservation.getWarehouseId());
        Stock stock = warehouse.getStock(reservation.getProductId());
        
        stock.release(reservation.getQuantity());
        reservations.remove(reservationId);
    }

    /**
     * Restock after return.
     */
    public synchronized void restock(String productId, String warehouseId, int quantity) {
        Warehouse warehouse = warehouses.get(warehouseId);
        Stock stock = warehouse.getStock(productId);
        
        stock.restock(quantity);
        
        // Notify if stock restored
        if (stock.getAvailable() >= products.get(productId).getLowStockThreshold()) {
            alertService.sendRestockAlert(productId, warehouseId);
        }
    }

    private void scheduleRelease(String reservationId, String productId, 
                                 String warehouseId, int quantity, int minutes) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            // Auto-release expired reservation
            Reservation r = reservations.get(reservationId);
            if (r != null && r.isPending()) {
                releaseStock(reservationId);
            }
        }, minutes, TimeUnit.MINUTES);
    }

    public Stock getStockLevel(String productId, String warehouseId) {
        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse == null) return null;
        
        return warehouse.getStock(productId);
    }

    private Map<String, Reservation> reservations = new ConcurrentHashMap<>();
}

/**
 * Warehouse stores products.
 */
class Warehouse {
    private final String warehouseId;
    private final Map<String, Stock> stock;  // productId → Stock

    Warehouse(String warehouseId) {
        this.warehouseId = warehouseId;
        this.stock = new ConcurrentHashMap<>();
    }

    void addProduct(String productId, int initialStock) {
        stock.put(productId, new Stock(productId, initialStock));
    }

    Stock getStock(String productId) {
        return stock.get(productId);
    }
}

/**
 * Stock level for a product.
 */
class Stock {
    private final String productId;
    private int physicalStock;
    private int reserved;
    private int pending;

    Stock(String productId, int initialStock) {
        this.productId = productId;
        this.physicalStock = initialStock;
    }

    synchronized int getAvailable() {
        return physicalStock - reserved - pending;
    }

    synchronized void reserve(int quantity) {
        this.reserved += quantity;
    }

    synchronized void capture(int quantity) {
        this.reserved -= quantity;
        this.physicalStock -= quantity;
    }

    synchronized void release(int quantity) {
        this.reserved -= quantity;
    }

    synchronized void restock(int quantity) {
        this.physicalStock += quantity;
    }
}

/**
 * Product master data.
 */
class Product {
    private final String productId;
    private String name;
    private int lowStockThreshold;
    private boolean active;

    Product(String productId, String name) {
        this.productId = productId;
        this.name = name;
        this.lowStockThreshold = 10;
        this.active = true;
    }

    public String getProductId() { return productId; }
    public int getLowStockThreshold() { return lowStockThreshold; }
}

/**
 * Reservation for pending order.
 */
class Reservation {
    private final String reservationId;
    private final String productId;
    private final String warehouseId;
    private final int quantity;
    private ReservationStatus status;
    private final long createdAt;

    Reservation(String reservationId, String productId, String warehouseId, int quantity) {
        this.reservationId = reservationId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.status = ReservationStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }

    public String getProductId() { return productId; }
    public String getWarehouseId() { return warehouseId; }
    public int getQuantity() { return quantity; }
}

enum ReservationStatus {
    PENDING, CAPTURED, RELEASED, EXPIRED
}

class AlertService {
    void sendLowStockAlert(String productId, String warehouseId, int available) {
        System.out.println("⚠️ Low stock: " + productId + " in " + warehouseId + 
                          ": " + available + " left");
    }
    
    void sendRestockAlert(String productId, String warehouseId) {
        System.out.println("✓ Stock restored: " + productId + " in " + warehouseId);
    }
}

class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String msg) { super(msg); }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multi-warehouse fulfillment?"
> "Fulfill from nearest warehouse. Split across multiple if needed. Sync inventory."

### Q2: "How to handle batch processing?"
> "Nightly reconciliation. Compare physical vs system. Adjust discrepancies."

### Q3: "How to forecast stock needs?"
> "ML on historical sales. Seasonal trends. Reorder point calculation."

### Q4: "How to handle returns?"
> "Inspect condition. Restock if good. Dispose if damaged. Update inventory."