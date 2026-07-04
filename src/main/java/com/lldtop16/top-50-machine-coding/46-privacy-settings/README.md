# 🔒 Problem 46: Privacy Settings Engine (Like Facebook/Instagram)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any social media company  
> **Est. Time**: 90 min | **Patterns**: Chain of Responsibility, Strategy, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Control who sees what on social media."

**What the interviewer tests**:
```
1. Can you model privacy levels? (Public, Friends, Only Me)
2. Can you handle granular permissions? (By post type)
3. Can you evaluate complex rules? (Friends of friends, lists)
4. Can you handle blocking? (User A blocks user B)
```

### Step 2: The "Aha!" Moment

The key insight: **Privacy is a chain of checks.**

```
PRIVACY LEVELS:
  PUBLIC:      Anyone (including non-users)
  FRIENDS:     Only friends
  CLOSE_FRIENDS: Only best friends
  ONLY_ME:     Only me
  CUSTOM:      Specific list

RULES EVALUATION (Chain of Responsibility):
  Request: Can user B see user A's photo?
  
  Check 1: Did user A block user B?
    YES → DENY
    NO → Continue
  
  Check 2: Is photo public?
    YES → ALLOW
    NO → Continue
  
  Check 3: Is user B a friend of user A?
    YES → ALLOW
    NO → DENY

FIRST MATCH WINS.
```

### Step 3: How to optimize?

```
INDEXING:
  - Store privacy per post
  - Index by user + privacy level
  - When fetching feed:
    - Get all my posts (ONLY_ME, FRIENDS)
    - Get public posts from friends
    - Get public posts from everyone

BLOCKING:
  - Block table: blocker → blocked
  - Check before ANY content sharing
  - Hard block: no way around
  - Soft block: can see tagged photos
```

---

## 💻 Core Implementation

```java
package com.privacy;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: PrivacyEngine evaluates who can see what.
 * 
 * Uses Chain of Responsibility for rule checking.
 */
public class PrivacyEngine {
    
    private final Map<String, User> users;
    private final Map<String, Post> posts;
    private final BlockManager blockManager;
    private final PrivacyChain privacyChain;

    public PrivacyEngine() {
        this.users = new ConcurrentHashMap<>();
        this.posts = new ConcurrentHashMap<>();
        this.blockManager = new BlockManager();
        this.privacyChain = buildPrivacyChain();
    }

    /**
     * INTUITION: Check if user can see a post.
     * 
     * Walks the chain of privacy checks.
     */
    public boolean canView(String viewerId, String postId) {
        Post post = posts.get(postId);
        if (post == null) return false;
        
        User author = users.get(post.getAuthorId());
        User viewer = users.get(viewerId);
        
        // Walk the privacy chain
        PrivacyContext context = new PrivacyContext(author, viewer, post);
        return privacyChain.check(context);
    }

    /**
     * INTUITION: Get content visible to user.
     * 
     * Returns posts user can see in their feed.
     */
    public List<Post> getFeed(String userId) {
        User user = users.get(userId);
        List<Post> feed = new ArrayList<>();
        
        for (Post post : posts.values()) {
            if (canView(userId, post.getId())) {
                feed.add(post);
            }
        }
        
        // Sort by time
        feed.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        
        return feed;
    }

    /**
     * INTUITION: Block a user.
     */
    public synchronized void blockUser(String blockerId, String blockedId) {
        blockManager.block(blockerId, blockedId);
    }

    /**
     * INTUITION: Unblock a user.
     */
    public synchronized void unblockUser(String blockerId, String blockedId) {
        blockManager.unblock(blockerId, blockedId);
    }

    /**
     * Build the chain of privacy checks.
     * Order matters: first match wins.
     */
    private PrivacyChain buildPrivacyChain() {
        PrivacyChain chain = new PrivacyChain();
        
        // Check 1: Block (highest priority)
        chain.addCheck(new BlockCheck(blockManager));
        
        // Check 2: Privacy level
        chain.addCheck(new PrivacyLevelCheck());
        
        // Check 3: Friendship status
        chain.addCheck(new FriendshipCheck());
        
        // Check 4: Custom list
        chain.addCheck(new CustomListCheck());
        
        return chain;
    }
}

/**
 * Privacy chain - collects multiple checks.
 */
class PrivacyChain {
    private final List<PrivacyCheck> checks = new ArrayList<>();
    
    void addCheck(PrivacyCheck check) {
        checks.add(check);
    }
    
    boolean check(PrivacyContext context) {
        for (PrivacyCheck check : checks) {
            PrivacyResult result = check.evaluate(context);
            if (result.isDecisive()) {
                return result.isAllowed();
            }
        }
        return false;  // Deny by default
    }
}

/**
 * Context for privacy evaluation.
 */
class PrivacyContext {
    private final User author;
    private final User viewer;
    private final Post post;

    PrivacyContext(User author, User viewer, Post post) {
        this.author = author;
        this.viewer = viewer;
        this.post = post;
    }

    public User getAuthor() { return author; }
    public User getViewer() { return viewer; }
    public Post getPost() { return post; }
}

/**
 * Privacy check interface.
 */
interface PrivacyCheck {
    PrivacyResult evaluate(PrivacyContext context);
}

/**
 * Result of privacy check.
 */
class PrivacyResult {
    private final boolean allowed;
    private final boolean decisive;

    PrivacyResult(boolean allowed, boolean decisive) {
        this.allowed = allowed;
        this.decisive = decisive;
    }

    static PrivacyResult allow() { return new PrivacyResult(true, true); }
    static PrivacyResult deny() { return new PrivacyResult(false, true); }
    static PrivacyResult abstain() { return new PrivacyResult(false, false); }

    boolean isAllowed() { return allowed; }
    boolean isDecisive() { return decisive; }
}

/**
 * Check 1: Block check.
 */
class BlockCheck implements PrivacyCheck {
    private final BlockManager blockManager;

    BlockCheck(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    @Override
    public PrivacyResult evaluate(PrivacyContext context) {
        if (blockManager.isBlocked(context.getAuthor().getId(), context.getViewer().getId())) {
            return PrivacyResult.deny();  // Blocked!
        }
        return PrivacyResult.abstain();  // Not blocked, check next
    }
}

/**
 * Check 2: Privacy level.
 */
class PrivacyLevelCheck implements PrivacyCheck {
    @Override
    public PrivacyResult evaluate(PrivacyContext context) {
        Post post = context.getPost();
        User viewer = context.getViewer();
        User author = context.getAuthor();
        
        switch (post.getPrivacy()) {
            case PUBLIC:
                return PrivacyResult.allow();
            
            case ONLY_ME:
                return viewer.equals(author) ? PrivacyResult.allow() : PrivacyResult.deny();
            
            case FRIENDS:
                return author.getFriends().contains(viewer.getId()) ? 
                    PrivacyResult.allow() : PrivacyResult.deny();
            
            case CLOSE_FRIENDS:
                return author.getCloseFriends().contains(viewer.getId()) ? 
                    PrivacyResult.allow() : PrivacyResult.deny();
            
            default:
                return PrivacyResult.abstain();
        }
    }
}

/**
 * Check 3: Friendship.
 */
class FriendshipCheck implements PrivacyCheck {
    @Override
    public PrivacyResult evaluate(PrivacyContext context) {
        User author = context.getAuthor();
        User viewer = context.getViewer();
        
        if (author.getFriends().contains(viewer.getId())) {
            return PrivacyResult.allow();
        }
        return PrivacyResult.deny();
    }
}

/**
 * Check 4: Custom list.
 */
class CustomListCheck implements PrivacyCheck {
    @Override
    public PrivacyResult evaluate(PrivacyContext context) {
        Post post = context.getPost();
        User viewer = context.getViewer();
        
        if (post.getAllowedUsers() != null && 
            post.getAllowedUsers().contains(viewer.getId())) {
            return PrivacyResult.allow();
        }
        
        return PrivacyResult.abstain();
    }
}
```

```java
package com.privacy;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: User with privacy settings.
 */
public class User {
    private final String userId;
    private String name;
    private final Set<String> friends;
    private final Set<String> closeFriends;
    private final Set<String> blockedUsers;
    private final Map<String, PrivacyList> customLists;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.friends = ConcurrentHashMap.newKeySet();
        this.closeFriends = ConcurrentHashMap.newKeySet();
        this.blockedUsers = ConcurrentHashMap.newKeySet();
        this.customLists = new ConcurrentHashMap<>();
    }

    public void addFriend(String userId) {
        friends.add(userId);
    }

    public void addCloseFriend(String userId) {
        closeFriends.add(userId);
    }

    public void blockUser(String userId) {
        blockedUsers.add(userId);
        friends.remove(userId);  // Also unfriend
    }

    public boolean isFriend(String userId) {
        return friends.contains(userId);
    }

    public boolean isBlocked(String userId) {
        return blockedUsers.contains(userId);
    }

    public String getId() { return userId; }
    public Set<String> getFriends() { return Collections.unmodifiableSet(friends); }
    public Set<String> getCloseFriends() { return Collections.unmodifiableSet(closeFriends); }
}
```

```java
package com.privacy;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Post with privacy settings.
 */
public class Post {
    private final String postId;
    private final String authorId;
    private String content;
    private final LocalDateTime createdAt;
    private PrivacyLevel privacy;
    private final Set<String> allowedUsers;  // For CUSTOM privacy
    private final Set<String> taggedUsers;

    public Post(String postId, String authorId, String content) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.privacy = PrivacyLevel.FRIENDS;  // Default
        this.allowedUsers = new HashSet<>();
        this.taggedUsers = new HashSet<>();
    }

    // Getters
    public String getId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public PrivacyLevel getPrivacy() { return privacy; }
    public Set<String> getAllowedUsers() { return allowedUsers; }

    public void setPrivacy(PrivacyLevel privacy) {
        this.privacy = privacy;
    }

    public void addAllowedUser(String userId) {
        allowedUsers.add(userId);
    }
}

enum PrivacyLevel {
    PUBLIC,        // Anyone
    FRIENDS,       // Only friends
    CLOSE_FRIENDS, // Best friends only
    ONLY_ME,       // Private
    CUSTOM         // Specific list
}
```

```java
package com.privacy;

import java.util.*;

/**
 * INTUITION: BlockManager handles user blocking.
 */
class BlockManager {
    private final Map<String, Set<String>> blocks;

    BlockManager() {
        this.blocks = new ConcurrentHashMap<>();
    }

    void block(String blockerId, String blockedId) {
        blocks.computeIfAbsent(blockerId, k -> ConcurrentHashMap.newKeySet())
              .add(blockedId);
    }

    void unblock(String blockerId, String blockedId) {
        Set<String> blocked = blocks.get(blockerId);
        if (blocked != null) {
            blocked.remove(blockedId);
        }
    }

    boolean isBlocked(String blockerId, String blockedId) {
        Set<String> blocked = blocks.get(blockerId);
        return blocked != null && blocked.contains(blockedId);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle tagged photos?"
> "Tagged user gets notification. If tagged user rejects, remove tag. Privacy: tagged photo visible to friends of both."

### Q2: "How to implement shadow ban?"
> "User sees their own content, but no one else does. Mimic successful post. Silence is the punishment."

### Q3: "How to handle data deletion (GDPR)?"
> "Delete from all tables. Remove from backups (within 30 days). Anonymize analytics. Audit log."

### Q4: "How to handle close friends feature?"
> "Separate list from friends. Exclude from 'friends only' posts. Highlight close friend stories."