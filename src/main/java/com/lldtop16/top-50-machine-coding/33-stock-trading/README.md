# 📈 Problem 33: Stock Trading System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Bloomberg, Goldman Sachs, Robinhood  
> **Est. Time**: 120 min | **Patterns**: Strategy, Observer, Transaction, Order Book

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Match buy and sell orders for stocks."

**What the interviewer tests**:
```
1. Can you implement an order book? (bids vs asks)
2. Can you match orders? (highest bid meets lowest ask)
3. Can you handle partial fills? (buy 100 shares, only 50 available)
4. Can you handle transactions? (money in, shares out)
```

### Step 2: The "Aha!" Moment

The key insight: **Two-sided order book.**

```
BUY ORDERS (Bids):   $150 ($100 shares)  ← sorted high to low
                     $149 ($200 shares)
                     $148 ($300 shares)

SELL ORDERS (Asks):  $151 ($100 shares)  ← sorted low to high
                     $152 ($150 shares)
                     $155 ($50 shares)

MATCHING:
  When new buy order $150 comes:
    - Check top ask: $151 → NO MATCH (buy < sell)
    - Order sits in book until price moves or someone sells at $150

  When new sell order $150 comes:
    - Check top bid: $150 → MATCH!
    - Buyer gets 100 shares, seller gets $15000
```

### Step 3: How to handle trade execution?

```
ORDER LIFECYCLE:
  [PENDING] → [PARTIALLY FILLED] → [FILLED]
                   OR
              [CANCELLED]

Example:
  Buy order: 100 shares @ $150
  Sell order: 50 shares @ $149
  
  Result:
  - Trade executed: 50 shares @ $149
  - Buy order: PARTIALLY FILLED (50 of 100 filled)
  - Sell order: FILLED
```

---

## 💻 Core Implementation

```java
package com.trading;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: StockExchange is the order matching engine.
 * 
 * Maintains TWO order books:
 * - BUY: bids sorted by price (high to low)
 * - SELL: asks sorted by price (low to high)
 * 
 * When a new order arrives:
 *   1. Try to match against opposite side
 *   2. If match found → TRADE
 *   3. If no match/additive → REST in book
 * 
 * The magic: matching happens INSTANTLY in same transaction.
 */
public class StockExchange {
    
    // All stocks traded
    private final Map<String, Stock> stocks;
    
    // Order books: stockSymbol → OrderBook
    private final Map<String, OrderBook> orderBooks;
    
    // All orders
    private final Map<String, Order> orders;
    
    // Trade history
    private final List<Trade> trades;

    public StockExchange() {
        this.stocks = new ConcurrentHashMap<>();
        this.orderBooks = new ConcurrentHashMap<>();
        this.orders = new ConcurrentHashMap<>();
        this.trades = new CopyOnWriteArrayList<>();
    }

    /**
     * INTUITION: Place an order.
     * 
     * 1. Validate order (has money/shares?)
     * 2. Add to order book
     * 3. Try to match
     * 
     * @param userId Who is placing the order
     * @param symbol Stock symbol (AAPL)
     * @param type BUY or SELL
     * @param quantity Number of shares
     * @param price Limit price
     * @return Order ID
     */
    public synchronized String placeOrder(String userId, String symbol, 
                                          OrderType type, int quantity, double price) {
        
        // Step 1: Validate stock exists
        Stock stock = stocks.computeIfAbsent(symbol, k -> new Stock(symbol, k, 1000));
        
        // Step 2: Create order
        Order order = new Order(UUID.randomUUID().toString(), userId, symbol, 
                                type, quantity, price);
        orders.put(order.getId(), order);
        
        // Step 3: Get order book
        OrderBook book = orderBooks.computeIfAbsent(symbol, k -> new OrderBook(symbol));
        
        // Step 4: Match orders
        matchOrders(order, book);
        
        // Step 5: If not fully filled, add to book
        if (order.getStatus() == OrderStatus.PENDING) {
            book.addOrder(order);
        }
        
        return order.getId();
    }

    /**
     * INTUITION: Match an order against the order book.
     * 
     * BUY order matches against SELL orders (lowest ask first)
     * SELL order matches against BUY orders (highest bid first)
     * 
     * Match condition:
     *   buy.price >= sell.price
     */
    private void matchOrders(Order order, OrderBook book) {
        if (order.getType() == OrderType.BUY) {
            // Match against SELL orders (lowest price first)
            List<Order> asks = book.getAsks();
            for (Iterator<Order> it = asks.iterator(); it.hasNext();) {
                Order ask = it.next();
                
                // Check if prices match
                if (order.getPrice() >= ask.getPrice()) {
                    // TRADE!
                    int tradeQuantity = Math.min(order.getRemaining(), ask.getRemaining());
                    double tradePrice = ask.getPrice();  // Price of the earlier order
                    
                    Trade trade = executeTrade(order, ask, tradeQuantity, tradePrice);
                    trades.add(trade);
                    
                    // Remove fully filled orders
                    if (ask.getRemaining() == 0) {
                        it.remove();
                        ask.setStatus(OrderStatus.FILLED);
                    }
                    
                    if (order.getRemaining() == 0) {
                        order.setStatus(OrderStatus.FILLED);
                        return;  // Order completed
                    }
                } else {
                    // No more matching (prices too high)
                    break;
                }
            }
            
            // If partially filled or no match
            if (order.getRemaining() == order.getQuantity()) {
                order.setStatus(OrderStatus.PENDING);
            } else {
                order.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
            
        } else {
            // SELL order: match against BUY orders (highest price first)
            // Opposite logic, similar implementation
        }
    }

    private Trade executeTrade(Order buy, Order sell, int quantity, double price) {
        double total = quantity * price;
        
        // Update orders
        buy.setFilled(buy.getFilled() + quantity);
        sell.setFilled(sell.getFilled() + quantity);
        
        return new Trade(buy.getSymbol(), buy.getUserId(), sell.getUserId(), 
                        quantity, price, System.currentTimeMillis());
    }

    public void cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            OrderBook book = orderBooks.get(order.getSymbol());
            if (book != null) {
                book.removeOrder(orderId);
            }
        }
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
}
```

```java
package com.trading;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: OrderBook maintains bid/ask queues.
 * 
 * BUY side: Max heap (highest price first)
 * SELL side: Min heap (lowest price first)
 * 
 * Why?
 *  - Buyer wants cheapest ask
 *  - Seller wants highest bid
 */
class OrderBook {
    private final String symbol;
    
    // BUY orders: max-heap by price
    private final PriorityQueue<Order> bids;
    
    // SELL orders: min-heap by price
    private final PriorityQueue<Order> asks;

    OrderBook(String symbol) {
        this.symbol = symbol;
        this.bids = new PriorityQueue<>((a, b) -> {
            int cmp = Double.compare(b.getPrice(), a.getPrice()); // High to low
            return cmp != 0 ? cmp : Long.compare(a.getTimestamp(), b.getTimestamp());
        });
        this.asks = new PriorityQueue<>((a, b) -> {
            int cmp = Double.compare(a.getPrice(), b.getPrice()); // Low to high
            return cmp != 0 ? cmp : Long.compare(a.getTimestamp(), b.getTimestamp());
        });
    }

    void addOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            bids.offer(order);
        } else {
            asks.offer(order);
        }
    }

    void removeOrder(String orderId) {
        bids.removeIf(o -> o.getId().equals(orderId));
        asks.removeIf(o -> o.getId().equals(orderId));
    }

    List<Order> getBids() {
        return new ArrayList<>(bids);
    }

    List<Order> getAsks() {
        return new ArrayList<>(asks);
    }

    public String getSymbol() { return symbol; }
}
```

```java
package com.trading;

import java.time.LocalDateTime;

/**
 * INTUITION: Order represents an intent to buy/sell.
 * 
 * States:
 * - PENDING: Not yet filled
 * - PARTIALLY_FILLED: Some filled, rest pending
 * - FILLED: All filled
 * - CANCELLED: Cancelled by user
 */
public class Order {
    private final String id;
    private final String userId;
    private final String symbol;
    private final OrderType type;
    private final int quantity;
    private final double price;
    private final long timestamp;
    
    private int filled;
    private OrderStatus status;

    public Order(String id, String userId, String symbol, OrderType type, 
                 int quantity, double price) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.filled = 0;
        this.status = OrderStatus.PENDING;
        this.timestamp = System.currentTimeMillis();
    }

    public int getRemaining() {
        return quantity - filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getSymbol() { return symbol; }
    public OrderType getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public int getFilled() { return filled; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
}

enum OrderType { BUY, SELL }

enum OrderStatus {
    PENDING, PARTIALLY_FILLED, FILLED, CANCELLED
}
```

```java
package com.trading;

import java.time.LocalDateTime;

/**
 * INTUITION: Trade represents a completed transaction.
 * 
 * Generated when buy and sell orders match.
 */
public class Trade {
    private final String id;
    private final String symbol;
    private final String buyerId;
    private final String sellerId;
    private final int quantity;
    private final double price;
    private final double total;
    private final long timestamp;

    public Trade(String symbol, String buyerId, String sellerId, 
                 int quantity, double price, long timestamp) {
        this.id = UUID.randomUUID().toString();
        this.symbol = symbol;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
    public long getTimestamp() { return timestamp; }
}

class Stock {
    private final String symbol;
    private String name;
    private int availableShares;
    private double lastPrice;

    public Stock(String symbol, String name, int availableShares) {
        this.symbol = symbol;
        this.name = name;
        this.availableShares = availableShares;
        this.lastPrice = 0;
    }

    public void setLastPrice(double price) {
        this.lastPrice = price;
    }

    public String getSymbol() { return symbol; }
    public double getLastPrice() { return lastPrice; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle market orders (buy at any price)?"
> "Market order = Buy at best available price. Match against ALL asks until filled. Risk: slippage (price moves during execution). Limit order: buy only if price ≤ $X."

### Q2: "How to prevent insider trading?"
> "Track insiders (executives, directors). Block trades before earnings. Flag unusual volume. Report to SEC."

### Q3: "How to scale to millions of trades per day?"
> "Partition by stock symbol. Use lock-free data structures. Batch trades into blocks (like Bitcoin). Use event sourcing."

### Q4: "How to handle short selling?"
> "Track borrowed shares. Verify user has margin account. Force close if losses exceed limit."

### Q5: "How to display order book to users?"
> "Snapshot top 10 bids/asks. Send via WebSocket for real-time updates. Cache aggregated levels."