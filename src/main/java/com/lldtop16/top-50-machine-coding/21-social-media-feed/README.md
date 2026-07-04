# 📱 Problem 21: Social Media Feed System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Facebook, Twitter, Instagram, LinkedIn  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, Fan-out

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a social media feed — like Twitter or Instagram."

**What the interviewer tests**:
```
1. Can you handle fan-out? (User posts → 10M followers see it)
2. Can you personalize feeds? (Algorithm: recency, engagement, relevance)
3. Can you handle different content types? (Text, image, video, story)
4. Can you handle real-time updates? (New post appears instantly)
```

### Step 2: The "Aha!" Moment

The core problem is **Fan-out on Write vs Fan-out on Read**.

```
FAN-OUT ON WRITE (Push model):
  User A posts → System writes to ALL followers' feeds
  Pro: Reading is O(1) — just read your feed
  Con: User with 10M followers → 10M writes per post
  Best for: Most users have < 10K followers (Twitter, Instagram)

FAN-OUT ON READ (Pull model):
  User B opens feed → System fetches posts from ALL followed users
  Pro: One write per post (efficient for celebrities)
  Con: Reading is O(following) — must merge multiple feeds
  Best for: Most users follow many people (Facebook)

HYBRID (Best of both):
  Normal users: Fan-out on write
  Celebrities (100K+ followers): Fan-out on read
  When reading: merge celebrity posts at read time
```

### Step 3: How to make it fast?

```
Feed retrieval is the HOT PATH - must be < 100ms.

Use:
1. In-memory cache (Redis) for top 1000 posts per user
2. Pre-computed feed stored as sorted set by timestamp
3. Pagination with cursor-based loading
4. CDN for images/videos
```

---

## 💻 Core Implementation

```java
package com.social;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: The SocialService is the coordinator.
 * 
 * It uses a HYBRID approach:
 * - Normal users: fan-out on write (push to followers)
 * - Celebrities: fan-out on read (pull at read time)
 */
public class SocialService {
    private final PostService postService;
    private final FeedGenerator feedGenerator;
    private final UserService userService;
    private final NotificationService notificationService;
    
    // Threshold for celebrity status
    private static final int CELEBRITY_THRESHOLD = 100_000;

    /**
     * INTUITION: Create a post and distribute it.
     * 
     * For normal users: Push to all followers' feeds immediately.
     * For celebrities: Don't push. Followers will pull at read time.
     * 
     * @param userId Who is posting
     * @param content Post content
     * @param type Post type (TEXT, IMAGE, VIDEO)
     * @return The created post
     */
    public Post createPost(String userId, String content, PostType type) {
        // Create the post
        Post post = postService.createPost(userId, content, type);
        
        // Get follower count
        int followerCount = userService.getFollowerCount(userId);
        
        if (followerCount < CELEBRITY_THRESHOLD) {
            // NORMAL USER: Fan-out on write
            // Push post to all followers' feeds
            fanOutOnWrite(post);
        } else {
            // CELEBRITY: Don't fan-out, will be pulled at read time
            System.out.println("Celebrity post - will be pulled at read time");
        }
        
        // Send notifications (async)
        notificationService.notifyFollowers(userId, post.getId());
        
        return post;
    }

    /**
     * INTUITION: Fan-out on write.
     * 
     * For each follower, add this post to their home timeline.
     * This is a WRITE-HEAVY operation but makes READ super fast.
     * 
     * For 1M followers, this is 1M writes. Do it asynchronously.
     */
    private void fanOutOnWrite(Post post) {
        List<String> followers = userService.getFollowers(post.getUserId());
        
        // Use batch insert for efficiency
        feedGenerator.addToFeeds(followers, post);
    }

    /**
     * INTUITION: Get a user's personalized feed.
     * 
     * For normal users: Read from pre-computed feed (O(1))
     * For celebrities: Pull from followed users at read time (O(following))
     * 
     * @param userId Whose feed to get
     * @param page Page number
     * @param pageSize Number of posts per page
     * @return List of posts
     */
    public List<Post> getFeed(String userId, int page, int pageSize) {
        // Get pre-computed feed (fan-out on write)
        List<Post> feed = feedGenerator.getFeed(userId, page, pageSize);
        
        // Get posts from celebrities user follows (fan-out on read)
        List<String> followingCelebrities = userService.getFollowingCelebrities(userId);
        List<Post> celebrityPosts = feedGenerator.pullPostsFromUsers(
            followingCelebrities, page, pageSize
        );
        
        // Merge, sort by time, deduplicate
        List<Post> merged = mergeAndSort(feed, celebrityPosts);
        
        // Paginate
        int start = page * pageSize;
        int end = Math.min(start + pageSize, merged.size());
        
        return merged.subList(start, end);
    }
}
```

```java
package com.social;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: A post is immutable once created.
 * 
 * Why immutable?
 * - Thread-safe (no race conditions on updates)
 * - Cache-friendly (can store in Redis indefinitely)
 * - Simple reasoning (post never changes)
 */
public class Post {
    private final String id;
    private final String userId;
    private final String content;
    private final PostType type;
    private final LocalDateTime createdAt;
    private final Map<String, Object> metadata;  // image URLs, video length, etc.
    private final int likeCount;
    private final int commentCount;
    private final int shareCount;

    public Post(String userId, String content, PostType type) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.likeCount = 0;
        this.commentCount = 0;
        this.shareCount = 0;
    }

    // Getters only - immutable!
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public PostType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

public enum PostType {
    TEXT(1),       // Text only
    IMAGE(2),      // Text + images
    VIDEO(3),      // Text + video
    STORY(4);      // 24hr ephemeral

    private final int weight;  // For sorting

    PostType(int weight) { this.weight = weight; }
    public int getWeight() { return weight; }
}
```

```java
package com.social;

import java.util.*;

/**
 * INTUITION: FeedGenerator manages pre-computed feeds.
 * 
 * Uses Redis Sorted Set:
 * - Key: "feed:{userId}"
 * - Score: timestamp (for sorting)
 * - Value: postId
 * 
 * This gives O(log N) insertion and O(log N + K) range queries.
 * Perfect for "get latest 20 posts" requests.
 */
public class FeedGenerator {
    private final PostService postService;
    
    // In production: use Redis Sorted Set (ZADD, ZRANGE)
    // For demo: in-memory TreeMap (sorted by timestamp)
    private final Map<String, TreeMap<LocalDateTime, String>> userFeeds = new ConcurrentHashMap<>();

    /**
     * Add a post to multiple users' feeds.
     * 
     * @param userIds List of user IDs whose feeds to add to
     * @param post The post to add
     */
    public void addToFeeds(List<String> userIds, Post post) {
        for (String userId : userIds) {
            userFeeds.computeIfAbsent(userId, k -> new TreeMap<>())
                     .put(post.getCreatedAt(), post.getId());
        }
    }

    /**
     * Get a user's feed with pagination.
     * 
     * TreeMap.descendingMap() gives newest-first.
     * Skip to page * pageSize, then take pageSize items.
     */
    public List<Post> getFeed(String userId, int page, int pageSize) {
        TreeMap<LocalDateTime, String> feed = userFeeds.get(userId);
        if (feed == null) return Collections.emptyList();

        // Get descending (newest first)
        Collection<Map.Entry<LocalDateTime, String>> entries = 
            feed.descendingMap().entrySet();
        
        // Paginate: skip to start, then collect pageSize items
        int start = page * pageSize;
        int count = 0;
        List<Post> result = new ArrayList<>();
        
        Iterator<Map.Entry<LocalDateTime, String>> it = entries.iterator();
        while (it.hasNext() && count < start + pageSize) {
            Map.Entry<LocalDateTime, String> entry = it.next();
            if (count >= start) {
                String postId = entry.getValue();
                Post post = postService.getPost(postId);
                if (post != null) {
                    result.add(post);
                }
            }
            count++;
        }
        
        return result;
    }

    /**
     * INTUITION: Pull posts from users at read time.
     * Used for celebrities (who have too many followers for fan-out on write).
     * 
     * Algorithm:
     * 1. Get all posts from followed celebrities
     * 2. Sort by timestamp (newest first)
     * 3. Return top N
     */
    public List<Post> pullPostsFromUsers(List<String> userIds, int page, int pageSize) {
        List<Post> allPosts = new ArrayList<>();
        
        for (String userId : userIds) {
            allPosts.addAll(postService.getUserPosts(userId));
        }
        
        // Sort by timestamp (newest first)
        allPosts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // Paginate
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allPosts.size());
        
        return allPosts.subList(start, end);
    }

    /**
     * Merge two lists and sort by recency.
     */
    private List<Post> mergeAndSort(List<Post> list1, List<Post> list2) {
        List<Post> merged = new ArrayList<>(list1);
        merged.addAll(list2);
        
        // Sort newest first
        merged.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // Deduplicate by post ID
        Set<String> seen = new HashSet<>();
        merged.removeIf(post -> !seen.add(post.getId()));
        
        return merged;
    }
}
```

```java
package com.social;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: PostService handles post CRUD.
 * 
 * In production: Database + Redis cache.
 * In demo: In-memory ConcurrentHashMap.
 */
public class PostService {
    private final Map<String, Post> posts = new ConcurrentHashMap<>();
    private final Map<String, List<Post>> userPosts = new ConcurrentHashMap<>();

    public Post createPost(String userId, String content, PostType type) {
        Post post = new Post(userId, content, type);
        posts.put(post.getId(), post);
        userPosts.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                 .add(post);
        return post;
    }

    public Post getPost(String postId) {
        return posts.get(postId);
    }

    public List<Post> getUserPosts(String userId) {
        return userPosts.getOrDefault(userId, Collections.emptyList());
    }

    public List<Post> getUserPosts(String userId, int page, int pageSize) {
        List<Post> all = userPosts.getOrDefault(userId, Collections.emptyList());
        int start = Math.min(page * pageSize, all.size());
        int end = Math.min(start + pageSize, all.size());
        return all.subList(start, end);
    }
}
```

```java
package com.social;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: UserService manages users, followers, following.
 */
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> followers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> following = new ConcurrentHashMap<>();

    public void follow(String followerId, String followeeId) {
        followers.computeIfAbsent(followeeId, k -> ConcurrentHashMap.newKeySet())
                 .add(followerId);
        following.computeIfAbsent(followerId, k -> ConcurrentHashMap.newKeySet())
                 .add(followeeId);
    }

    public void unfollow(String followerId, String followeeId) {
        Set<String> followerSet = followers.get(followeeId);
        if (followerSet != null) followerSet.remove(followerId);
        
        Set<String> followingSet = following.get(followerId);
        if (followingSet != null) followingSet.remove(followeeId);
    }

    public List<String> getFollowers(String userId) {
        return new ArrayList<>(followers.getOrDefault(userId, Collections.emptySet()));
    }

    public List<String> getFollowing(String userId) {
        return new ArrayList<>(following.getOrDefault(userId, Collections.emptySet()));
    }

    public int getFollowerCount(String userId) {
        return followers.getOrDefault(userId, Collections.emptySet()).size();
    }

    public List<String> getFollowingCelebrities(String userId) {
        // Return users with 100K+ followers that this user follows
        List<String> celebrities = new ArrayList<>();
        for (String followeeId : getFollowing(userId)) {
            if (getFollowerCount(followeeId) > 100_000) {
                celebrities.add(followeeId);
            }
        }
        return celebrities;
    }
}

class User {
    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    // Getters...
}
```

---

## ❓ Follow-up Questions

### Q1: "How to rank posts in feed (not just chronological)?"
> "Use a scoring function: score = (recency_weight * time_score) + (engagement_weight * like_score) + (relevance_weight * affinity_score). Store in Redis sorted set with score as priority."

### Q2: "How to handle viral posts (1M likes, 1M comments)?"
> "Counter caching using Redis INCR. Expose aggregated counts. Pagination on comments (load 20 at a time)."

### Q3: "How to support stories (disappear after 24 hours)?"
> "Use TTL index in database. Scheduled job deletes expired stories. Store story as separate entity with expiry timestamp."

### Q4: "How to prevent spam in feeds?"
> "Rate limiting: max 10 posts/hour for new users. Content analysis via ML. Shadow ban repeat offenders. User reporting + manual review."

### Q5: "How to handle millions of concurrent active users?"
> "Cache hot feeds in Redis. CDN for media. Horizontal scaling (shard by userId). Connection pooling. Load balancer."