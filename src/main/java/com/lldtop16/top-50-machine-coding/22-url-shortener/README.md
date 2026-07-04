# 🔗 Problem 22: URL Shortener (like bit.ly)

> **Difficulty**: ⭐⭐ | **Company Fit**: Any tech company  
> **Est. Time**: 60 min | **Patterns**: Hashing, Base62 Encoding, Factory

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Convert long URLs to short ones."

**What the interviewer tests**:
```
1. Can you generate unique short keys? (No collisions)
2. Can you handle high scale? (1B URLs, 100M daily requests)
3. Can you handle expiration? (Links die after 1 year)
4. Can you redirect fast? (< 50ms redirect)
```

### Step 2: The "Aha!" Moment

The key insight: **Base62 encoding of an auto-incrementing ID.**

```
Long URL:  https://www.amazon.com/dp/B08N5WRWNW/ref=sr_1_1?...

1. Hash:    crc32(url) = 1234567890
2. But we don't want sequential IDs (people can guess them)
3. So: Convert to Base62 with random salt
   ID 1  → "a"
   ID 2  → "b"
   ...
   ID 62 → "10"
   
4. Short URL: https://short.io/abc123
```

Base62 uses: `0-9, a-z, A-Z` = 62 characters.
Two digits = 62² = 3,844 combinations.
Six digits = 62⁶ = 56 BILLION URLs. Enough for most use cases.

### Step 3: How to make it fast?

```
WRITE PATH (create short URL):
  1. Check if URL already exists (deduplication)
  2. If new, generate next ID, encode to Base62
  3. Store in DB: shortKey → longURL + expiry
  4. Return https://short.io/abc123

READ PATH (redirect):
  1. Extract shortKey from URL: "abc123"
  2. Lookup in Redis cache (O(1))
  3. If miss, lookup in Database
  4. Return 301 redirect to longURL
```

---

## 💻 Core Implementation

### ShortenerService.java

```java
package com.shortener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INTUITION: The core service.
 * 
 * Thread-safety: Multiple users creating short URLs simultaneously.
 * Use ConcurrentHashMap for cache.
 */
public class ShortenerService {
    
    // In production: Database table with auto-increment ID
    // For demo: in-memory counter
    private final AtomicLong idCounter = new AtomicLong(1);
    
    // Cache: shortKey → URL mapping (Redis in production)
    private final Map<String, String> shortToLong = new ConcurrentHashMap<>();
    private final Map<String, String> longToShort = new ConcurrentHashMap<>();
    
    // Base62 alphabet
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    /**
     * INTUITION: Create a short URL.
     * 
     * 1. Check if this long URL already has a short version (deduplication)
     * 2. If yes, return existing short URL
     * 3. If no, generate new ID, encode to Base62, store mapping
     * 
     * @param longUrl The original long URL
     * @param expiryDays Days until this short URL expires (0 = never)
     * @return The short URL like "https://short.io/abc123"
     */
    public String createShortUrl(String longUrl, int expiryDays) {
        // Step 1: Deduplication - already shortened?
        String existing = longToShort.get(longUrl);
        if (existing != null) {
            return "https://short.io/" + existing;
        }

        // Step 2: Generate unique ID
        long id = idCounter.getAndIncrement();

        // Step 3: Encode ID to Base62
        String shortKey = encodeBase62(id);

        // Step 4: Store mappings (bidirectional)
        shortToLong.put(shortKey, longUrl);
        longToShort.put(longUrl, shortKey);

        // In production: Store in database with ID, shortKey, longUrl, expiry, createdAt
        // INSERT INTO urls (id, short_key, long_url, expiry_date) VALUES (?, ?, ?, ?)

        return "https://short.io/" + shortKey;
    }

    /**
     * INTUITION: Get original URL from short key.
     * 
     * 1. Check cache (Redis) - O(1)
     * 2. If miss, check database
     * 3. If found and not expired, return long URL
     * 4. If expired or not found, return null
     * 
     * @param shortKey The short key like "abc123"
     * @return Original URL or null if not found
     */
    public String getLongUrl(String shortKey) {
        // Step 1: Check cache
        String longUrl = shortToLong.get(shortKey);
        if (longUrl != null) {
            return longUrl;
        }

        // Step 2: Cache miss - get from database
        // SELECT long_url, expiry_date FROM urls WHERE short_key = ?
        // In demo, return null if not in cache
        return null;
    }

    /**
     * INTUITION: Convert a number to Base62 string.
     * 
     * Algorithm:
     * 1. Divide by 62, take remainder
     * 2. Remainder maps to a character in BASE62
     * 3. Repeat until quotient is 0
     * 4. Reverse the result
     * 
     * Example: ID = 12345
     *   12345 % 62 = 25 → 'P'
     *   12345 / 62 = 199
     *   199 % 62 = 13 → 'd'
     *   199 / 62 = 3
     *   3 % 62 = 3 → '3'
     *   3 / 62 = 0 → stop
     *   Result: "3dP" reversed = "Pd3"
     */
    private String encodeBase62(long id) {
        StringBuilder sb = new StringBuilder();
        
        while (id > 0) {
            int remainder = (int) (id % BASE);
            sb.append(BASE62.charAt(remainder));
            id = id / BASE;
        }
        
        // Reverse to get correct order
        return sb.reverse().toString();
    }

    /**
     * Alternative: Hash-based shortener (when IDs aren't sequential).
     * Uses CRC32 for speed.
     */
    private String encodeHash(String longUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("CRC32");
            byte[] hash = md.digest(longUrl.getBytes());
            long crc = new BigInteger(1, hash).longValue();
            return encodeBase62(Math.abs(crc));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### URL.java

```java
package com.shortener;

import java.time.LocalDateTime;

/**
 * The URL entity stored in database.
 */
public class URL {
    private final long id;
    private final String shortKey;
    private final String longUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiryDate;
    private final String createdBy;

    public URL(long id, String shortKey, String longUrl, int expiryDays, String createdBy) {
        this.id = id;
        this.shortKey = shortKey;
        this.longUrl = longUrl;
        this.createdAt = LocalDateTime.now();
        this.expiryDate = expiryDays > 0 
            ? createdAt.plusDays(expiryDays) 
            : null;  // Never expires
        this.createdBy = createdBy;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    // Getters
    public long getId() { return id; }
    public String getShortKey() { return shortKey; }
    public String getLongUrl() { return longUrl; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
}
```

---

## ❓ Follow-up Questions

### Q1: "What if two users create short URLs for the same long URL at the same time?"
> "Use database constraint on longUrl column. On duplicate, catch exception and return existing short URL. Or use distributed lock."

### Q2: "How to handle 1 billion URLs?"
> "Database sharding by ID. CDN for cache. Read replicas for lookups. Analytics stored separately."

### Q3: "How to support custom short URLs (brand names)?"
> "Check availability before creation. Reserve premium keywords. Separate table for custom aliases."

### Q4: "How to add analytics (click counts, geo data)?"
> "Log each redirect with timestamp, IP, user agent. Process logs async into analytics DB."

### Q5: "What if someone uses all 6-character combinations?"
> "Move to 7 characters: 62⁷ = 3.5 trillion combinations. Very unlikely. Or use UUIDs encoded in Base64."