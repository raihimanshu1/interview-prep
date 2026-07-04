
package com.lldtop16.rateLimiter08;
import java.util.HashMap;
import java.util.Map;
public class RateLimiter {
    /*
    ========================================================
    TOKEN BUCKET
    Represents rate limit bucket for one user
    Example:
    User A:
    tokens = 5
    ========================================================
    */
    static class TokenBucket {
        private final int capacity;
        private int tokens;
        private final int refillRate;
        private long lastRefillTime;
        TokenBucket(
                int capacity,
                int refillRate
        ){
            this.capacity = capacity;
            this.tokens = capacity;
            this.refillRate = refillRate;
            this.lastRefillTime =
                    System.currentTimeMillis();
        }
        /*
        ====================================================
        CHECK REQUEST
        1. Refill tokens
        2. Check available token
        3. Consume token
        ====================================================
        */
        public boolean allowRequest(){
            refillTokens();
            if(tokens > 0){
                tokens--;
                return true;
            }
            return false;
        }
        /*
        ====================================================
        TOKEN REFILL
        Example:
        Capacity:
        5 tokens
        Refill:
        1 token every second
        ====================================================
        */
        private void refillTokens(){
            long currentTime =
                    System.currentTimeMillis();
            long timePassed =
                    (currentTime - lastRefillTime)
                    / 1000;
            int tokensToAdd =
                    (int)
                    (timePassed * refillRate);
            if(tokensToAdd > 0){
                tokens =
                Math.min(
                        capacity,
                        tokens + tokensToAdd
                );
                lastRefillTime =
                        currentTime;
            }
        }
    }
    /*
    ========================================================
    RATE LIMITER SERVICE
    Maintains buckets for users
    Example:
    User1 -> Bucket
    User2 -> Bucket
    ========================================================
    */
    static class RateLimiterService {
        private final Map<String, TokenBucket>
                userBuckets;
        private final int capacity;
        private final int refillRate;
        RateLimiterService(
                int capacity,
                int refillRate
        ){
            this.capacity = capacity;
            this.refillRate = refillRate;
            userBuckets =
                    new HashMap<>();
        }
        public boolean allow(
                String userId
        ){
            TokenBucket bucket =
                    userBuckets.computeIfAbsent(
                            userId,
                            id ->
                            new TokenBucket(
                                    capacity,
                                    refillRate
                            )
                    );
            return bucket.allowRequest();
        }
    }
    /*
    ========================================================
    MAIN METHOD
    TESTING RATE LIMITER
    ========================================================
    */
    public static void main(String[] args) {
        /*
        Capacity:
        5 requests
        Refill:
        1 token per second
        */
        RateLimiterService limiter =
                new RateLimiterService(
                        5,
                        1
                );
        String user =
                "user-101";
        for(int i=1;i<=7;i++){
            boolean allowed =
                    limiter.allow(user);
            System.out.println(
                    "Request "
                    +
                    i
                    +
                    " : "
                    +
                    (allowed
                    ?
                    "Allowed"
                    :
                    "Rejected")
            );
        }
        /*
        After waiting:
        Tokens refill
        */
        try{
            Thread.sleep(3000);
        }
        catch(Exception e){
        }
        System.out.println(
                "After refill:"
        );
        System.out.println(
                limiter.allow(user)
        );
    }
}
/*
============================================================
RATE LIMITER SYSTEM (TOKEN BUCKET)
============================================================
PROBLEM STATEMENT
============================================================
Design a Rate Limiter that restricts how many requests
a user/client can make in a given time period.
Example:
Limit:
    5 requests per second
User sends:
Request 1 -> Allowed
Request 2 -> Allowed
Request 3 -> Allowed
Request 4 -> Allowed
Request 5 -> Allowed
Request 6 -> Rejected
Purpose:
- Prevent abuse
- Protect backend services
- Avoid DDOS-like traffic
- Ensure fair resource usage
============================================================
COMMON RATE LIMITING ALGORITHMS
============================================================
1. Fixed Window
Example:
Limit = 100 requests/minute
12:00 - 12:01 => Counter = 100
Issue:
User can make:
100 requests at 12:00:59
100 requests at 12:01:01
Total = 200 requests in 2 seconds
Burst problem.
------------------------------------------------------------
2. Sliding Window
Keeps timestamps of requests.
More accurate.
Memory heavy.
------------------------------------------------------------
3. Leaky Bucket
Requests enter a bucket.
Processed at fixed rate.
Smooths traffic.
------------------------------------------------------------
4. Token Bucket (MOST POPULAR)
Bucket contains tokens.
Request consumes one token.
Tokens are continuously refilled.
If token available:
    Allow
Else:
    Reject
Used by:
- AWS
- Google APIs
- Networking systems
============================================================
WHY TOKEN BUCKET?
============================================================
Supports burst traffic.
Example:
Bucket Capacity = 10
Current Tokens = 10
User sends:
10 requests immediately
All succeed.
Then bucket becomes empty.
Requests are rejected until refill happens.
============================================================
DESIGN
============================================================
                    RateLimiter
                         |
                         |
                -----------------
                |               |
                |               |
            User A          User B
                |               |
                |               |
          TokenBucket      TokenBucket
Each user gets their own bucket.
============================================================
RESPONSIBILITIES
============================================================
TokenBucket
    - Maintain tokens
    - Refill tokens
    - Consume tokens
RateLimiter
    - Maintain buckets
    - Route requests
    - Decide allow/reject
============================================================
FLOW
============================================================
Request arrives
        |
        v
Find bucket for user
        |
        v
Refill tokens
        |
        v
Token available?
      /     \
    YES      NO
     |        |
 Allow     Reject
============================================================
TIME COMPLEXITY
============================================================
Allow Request:
HashMap Lookup = O(1)
Token Refill = O(1)
Consume Token = O(1)
Total = O(1)
============================================================
SPACE COMPLEXITY
============================================================
O(number_of_users)
One bucket per user.
============================================================
INTERVIEW FOLLOW UPS
============================================================
1. Distributed Rate Limiter
Problem:
Multiple application servers.
Server A:
User gets 5 requests
Server B:
User gets another 5 requests
Limit broken.
Solution:
Store counters in Redis.
------------------------------------------------------------
2. Multi-Level Limits
Example:
User Limit = 100/min
API Limit = 1000/min
System Limit = 10000/min
------------------------------------------------------------
3. Different Plans
Free User:
    100 requests/day
Premium:
    10000 requests/day
Enterprise:
    Unlimited
Use Strategy Pattern.
------------------------------------------------------------
4. Thread Safety
Multiple threads may update same bucket.
Use:
- synchronized
- ReentrantLock
- Atomic variables
------------------------------------------------------------
5. Why not use Singleton?
RateLimiter may be singleton in Spring,
but TokenBucket should not be singleton.
Each user needs separate bucket.
============================================================
*/
class RateLimiterSystem {
    /*
    ============================================================
    TOKEN BUCKET
    ============================================================
    Example:
    Capacity = 5
    Tokens = 5
    Request comes:
        consume token
    Tokens = 4
    After some time:
        refill happens
    Tokens increase again.
    */
    static class TokenBucket {
        private final long capacity;
        private final long refillTokensPerSecond;
        private long availableTokens;
        private long lastRefillTimestamp;
        public TokenBucket(
                long capacity,
                long refillTokensPerSecond
        ) {
            this.capacity = capacity;
            this.refillTokensPerSecond = refillTokensPerSecond;
            this.availableTokens = capacity;
            // current timestamp in milliseconds
            this.lastRefillTimestamp =
                    System.currentTimeMillis();
        }
        /*
        Refill tokens based on elapsed time.
        Example:
        Refill Rate = 5/sec
        2 seconds passed
        Add:
            10 tokens
        But never exceed capacity.
        */
        private void refill() {
            long currentTime =
                    System.currentTimeMillis();
            long elapsedMillis =
                    currentTime - lastRefillTimestamp;
            long tokensToAdd =
                    (elapsedMillis * refillTokensPerSecond) / 1000;
            if (tokensToAdd > 0) {
                availableTokens =
                        Math.min(
                                capacity,
                                availableTokens + tokensToAdd
                        );
                lastRefillTimestamp = currentTime;
            }
        }
        /*
        Thread-safe token consumption.
        */
        public synchronized boolean allowRequest() {
            refill();
            if (availableTokens > 0) {
                availableTokens--;
                return true;
            }
            return false;
        }
        public long getAvailableTokens() {
            return availableTokens;
        }
    }
    /*
    ============================================================
    RATE LIMITER
    ============================================================
    Maintains bucket per user.
    Example:
    user1 -> bucket
    user2 -> bucket
    user3 -> bucket
    */
    static class RateLimiter {
        private final Map<String, TokenBucket> buckets =
                new HashMap<>();
        private final long capacity;
        private final long refillRate;
        public RateLimiter(
                long capacity,
                long refillRate
        ) {
            this.capacity = capacity;
            this.refillRate = refillRate;
        }
        public boolean allowRequest(String userId) {
            /*
            Create bucket if user seen first time.
            */
            buckets.computeIfAbsent(
                    userId,
                    id -> new TokenBucket(
                            capacity,
                            refillRate
                    )
            );
            TokenBucket bucket =
                    buckets.get(userId);
            return bucket.allowRequest();
        }
    }
    /*
    ============================================================
    DEMO
    ============================================================
    */
    public static void main(String[] args)
            throws InterruptedException {
        /*
        Capacity = 5
        Refill = 5 tokens/sec
        */
        RateLimiter rateLimiter =
                new RateLimiter(
                        5,
                        5
                );
        String user = "USER_1";
        System.out.println("=== Initial Requests ===");
        for (int i = 1; i <= 7; i++) {
            boolean allowed =
                    rateLimiter.allowRequest(user);
            System.out.println(
                    "Request "
                            + i
                            + " -> "
                            + (allowed ? "ALLOWED" : "REJECTED")
            );
        }
        System.out.println();
        System.out.println("Waiting 2 seconds...");
        System.out.println();
        Thread.sleep(2000);
        System.out.println("=== After Refill ===");
        for (int i = 1; i <= 5; i++) {
            boolean allowed =
                    rateLimiter.allowRequest(user);
            System.out.println(
                    "Request "
                            + i
                            + " -> "
                            + (allowed ? "ALLOWED" : "REJECTED")
            );
        }
    }
}
/*
============================================================
                    RATE LIMITER SYSTEM
============================================================
PROBLEM STATEMENT:
Design a rate limiter that controls how many requests a user
can make within a given time window.
Example:
API allows:
5 requests per minute
User sends:
Request 1  -> Allowed
Request 2  -> Allowed
Request 3  -> Allowed
Request 4  -> Allowed
Request 5  -> Allowed
Request 6  -> Rejected
============================================================
WHY RATE LIMITER IS NEEDED
============================================================
Real systems have APIs:
Example:
GET /user/profile
Without rate limiting:
One user can send:
10 million requests
Problems:
- Server overload
- DDoS protection issue
- Database pressure
- Unfair resource usage
Rate limiter controls traffic.
============================================================
COMMON RATE LIMITING ALGORITHMS
============================================================
1. Fixed Window Counter
Example:
10 requests per minute
12:00 - 12:01
Counter:
5 requests
12:01 resets
Problem:
Boundary issue
Example:
12:00:59
5 requests
12:01:01
5 requests
Total:
10 requests in 2 seconds
------------------------------------------------------------
2. Sliding Window
Tracks exact timestamps.
More accurate.
------------------------------------------------------------
3. Token Bucket
Most commonly used.
Idea:
Bucket contains tokens.
Example:
Capacity = 5 tokens
Every request consumes:
1 token
If tokens available:
Allow request
Else:
Reject
Tokens refill over time.
============================================================
DESIGN USED HERE
============================================================
We implement:
TOKEN BUCKET ALGORITHM
Classes:
RateLimiter
    |
    |
TokenBucket
Request
    |
    |
UserId
============================================================
RESPONSIBILITIES
============================================================
TokenBucket:
Maintains:
- Available tokens
- Refill logic
RateLimiter:
Checks requests
============================================================
DESIGN PATTERN
============================================================
Strategy Pattern can be used:
RateLimitStrategy
        |
        |
TokenBucketStrategy
SlidingWindowStrategy
FixedWindowStrategy
Today:
Token Bucket
Tomorrow:
Change algorithm without changing client code.
============================================================
TIME COMPLEXITY
============================================================
checkRequest()
O(1)
SPACE:
O(number of users)
============================================================
*/
        
        
